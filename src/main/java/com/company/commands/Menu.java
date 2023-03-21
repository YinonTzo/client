package com.company.commands;

import com.company.common.messages.clientToServer.ExecutionData;
import com.company.common.messages.serverToClient.BaseServerToClient;

import java.util.Map;

/**
 * The "Menu" class represents a menu of commands that can be executed on a remote server.
 * It contains a map of available commands loaded by the "CommandLoader" class.
 * The class provides a method to execute a command based on a message received from the server.
 * The execute method takes a "BaseServerToClient" message and returns an "ExecutionData" object.
 * It first determines the command to execute based on the message type and then calls the execute
 * method of the corresponding command.
 * The method also updates the value of the "run" flag based on whether the executed command should keep running or not.
 */
public class Menu {

    private final Map<String, Command> commands;

    private boolean run = true;

    public Menu(Map<String, Command> commands) {
        this.commands = commands;
    }

    public ExecutionData execute(BaseServerToClient serverToClients) {
        Command wantedCommand = getCommand(serverToClients);

        if (wantedCommand == null) {
            return null;
        }

        run = wantedCommand.isKeepRunningCommand();

        return wantedCommand.execute(serverToClients);
    }

    private Command getCommand(BaseServerToClient serverToClients) {
        return commands.get(serverToClients.getType());
    }

    public boolean isRun() {
        return run;
    }
}
