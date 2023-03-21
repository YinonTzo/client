package com.company.commands;

import com.company.common.messages.clientToServer.ExecutionData;
import com.company.common.messages.serverToClient.BaseServerToClient;

/**
 * The "Command" interface defines a contract for a command that can be executed by the "Menu" class.
 * It contains three methods:
 * <p>
 * execute() method takes a "BaseServerToClient" message and returns an "ExecutionData" object.
 * This method is responsible for executing the command and returning any relevant data.
 * <p>
 * getCommandName() method returns a String that represents the name of the command.
 * This method is used by the "Menu" class to identify the command that needs to be executed.
 * <p>
 * isKeepRunningCommand() method returns a boolean value that indicates whether the executed command
 * should keep running or not.
 * This method is used by the "Menu" class to determine whether the "run" flag should be updated or not.
 */
public interface Command {

    ExecutionData execute(BaseServerToClient serverToClients);

    String getCommandName();

    boolean isKeepRunningCommand();
}
