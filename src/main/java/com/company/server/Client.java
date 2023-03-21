package com.company.server;

import com.company.commands.CommandLoader;
import com.company.commands.Menu;
import com.company.common.messages.clientToServer.ExecutionData;
import com.company.common.messages.serverToClient.BaseServerToClient;
import com.company.common.statuses.ExecutionStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The "Client" responsible for establish a connection with a server,
 * send and receive messages between the client and the server, and execute commands received from the server.
 * <p>
 * The "Client" class includes 2 threads: main thread is responsible for receiving commands from the server,
 * and "StatusSender" sends status messages back to the server.
 * <p>
 * When the main thread receives message it adds the message to the commandsQueue and creates a new "CommandExecutor".
 * The "CommandExecutor" implements the Runnable interface,
 * and responsible for executing commands received from the server. When finishes executing the command,
 * add the result to the statusesQueue then the "StatusSender" send them back to the server.
 */

@Slf4j
public class Client {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Menu menu;

    public Client() {
        this.menu = new Menu(CommandLoader.loadCommands());
    }

    BlockingQueue<ExecutionData> statusesQueue = new LinkedBlockingQueue<>();
    BlockingQueue<BaseServerToClient> commandsQueue = new LinkedBlockingQueue<>();

    public void startConnection(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            log.info("connected to {}:{}", ip, port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            new Thread(new StatusSender()).start();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void stopConnection() throws IOException {
        socket.close();
    }

    public void run() {
        try {
            while (true) {
                BaseServerToClient serverToClients = receiveMessage();
                ExecutionData start = new ExecutionData(serverToClients.getId(), ExecutionStatus.RECEIVED);
                statusesQueue.put(start);
                commandsQueue.put(serverToClients);
                new Thread(new CommandExecutor()).start();

                //I cannot avoid it. If I check the condition inside the while(), there will be a race condition between
                //the CommandExecutor() and the main thread.
                //Alternatively I could check the condition after the CommandExecutor() completes,
                // but this approach would be detrimental to the concurrency.
                if (serverToClients.getType().equals("RemoveClient"))
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("Main run has been finished.");
    }

    private BaseServerToClient receiveMessage() throws Exception {
        return (BaseServerToClient) in.readObject();
    }

    private class CommandExecutor implements Runnable {
        @Override
        public void run() {
            try {
                BaseServerToClient serverToClients = commandsQueue.take();

                ExecutionData result = menu.execute(serverToClients);

                if (result == null) {
                    log.error("The client has no command {}", serverToClients.getType());
                } else {
                    statusesQueue.put(result);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            } finally {
                log.info("CommandExecutor has been finished");
            }
        }
    }

    private class StatusSender implements Runnable {
        @Override
        public void run() {
            try {
                while (menu.isRun() || !statusesQueue.isEmpty()) {
                    ExecutionData status = statusesQueue.take();
                    log.info(status.toString());
                    String base64Data = Base64.getEncoder().encodeToString(status.getResult().getBytes());
                    status.setResult(base64Data);
                    sendMessage(status);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            } finally {
                log.info("Closing the socket..");
                try {
                    stopConnection();
                } catch (IOException e) {
                    log.error("Failed to close the socket. {}", e.getMessage());
                }
            }
            log.info("status sender has been finished.");
        }

        private void sendMessage(ExecutionData status) {
            try {
                out.writeObject(status);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
