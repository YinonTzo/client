package com.company.plugins;

import com.company.commands.Command;
import com.company.common.messages.clientToServer.ExecutionData;
import com.company.common.messages.serverToClient.BaseServerToClient;
import com.company.common.statuses.ExecutionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RemoveClientCommandTest {

    Command removeClientCommand;
    @BeforeEach
    void setUp() {
        removeClientCommand = new RemoveClientCommand();
    }

    @Test
    void execute() {
        //given
        BaseServerToClient message = new BaseServerToClient();
        message.setType("RemoveClient");

        //when
        ExecutionData data = removeClientCommand.execute(message);

        //then
        assertEquals(ExecutionStatus.FINISHED, data.getStatus());
        assertEquals("Client finished successfully.", data.getResult());
    }

    @Test
    void getCommandName() {
        //no given

        //when
        String className = removeClientCommand.getCommandName();

        //then
        assertEquals("RemoveClient", className);
    }

    @Test
    void isKeepRunningCommand() {
        //no given

        //when
        boolean isRunning = removeClientCommand.isKeepRunningCommand();

        //then
        assertFalse(isRunning);
    }
}