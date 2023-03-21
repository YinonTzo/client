package com.company.plugins;

import com.company.common.messages.clientToServer.ExecutionData;
import com.company.common.messages.serverToClient.BaseServerToClient;
import com.company.common.statuses.ExecutionStatus;
import com.company.commands.Command;

/**
 * The "RemoveClientCommand" class is a plugin that implements the "Command" interface.
 * It represents the real execute of "RemoveClientCommand" command from the CLI side, and terminates the client.
 * <p>
 * execute() method takes a "BaseServerToClient" message and returns an "ExecutionData" object.
 * In this class, the method returns a new "ExecutionData" object with a finished status and a message that indicates
 * the client was removed successfully.
 * <p>
 * then, in the isKeepRunningCommand returns false and indicate to the menu to finish.
 */
public class RemoveClientCommand implements Command {

    public static final String COMMAND_NAME = "RemoveClient";
    public static final String CLIENT_FINISHED_SUCCESSFULLY = "Client finished successfully.";

    @Override
    public ExecutionData execute(BaseServerToClient serverToClients) {
        return new ExecutionData(serverToClients.getId(), ExecutionStatus.FINISHED, CLIENT_FINISHED_SUCCESSFULLY);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public boolean isKeepRunningCommand() {
        return false;
    }
}
