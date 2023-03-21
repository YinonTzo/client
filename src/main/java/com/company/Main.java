package com.company;

import com.company.config.Configuration;
import com.company.server.Client;

import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties properties = Configuration.load();
        String serverIp = properties.getProperty("SERVER_IP");
        int serverClientsPort = Integer.parseInt(properties.getProperty("SERVER_CLIENTS_PORT"));

        Client runnerClient = new Client();
        runnerClient.startConnection(serverIp, serverClientsPort);
        runnerClient.run();
    }
}
