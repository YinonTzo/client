package com.company.plugins;

import com.company.commands.Command;
import com.company.common.messages.clientToServer.ExecutionData;
import com.company.common.messages.serverToClient.PayloadServerToClient;
import com.company.common.statuses.ExecutionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class SendPayloadCommandTest {

    Command sendPayloadCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sendPayloadCommand = new SendPayloadCommand();
    }

    @Test
    void execute() {

        String payload = "Mainfile_name" +
                "public class Main {" +
                "    public static void main(String[] args) { " +
                "       System.out.println(\"Hello World\");" +
                "    }" +
                "}";

        //given
        PayloadServerToClient payloadServerToClient = new PayloadServerToClient();
        payloadServerToClient.setPayload(payload.getBytes(StandardCharsets.UTF_8));

        //when
        ExecutionData executionData = sendPayloadCommand.execute(payloadServerToClient);

        //then
        assertEquals(ExecutionStatus.FINISHED, executionData.getStatus());
        assertEquals("Hello World", executionData.getResult());
    }

    @Test
    void getCommandName() {
        //no given

        //when
        String className = sendPayloadCommand.getCommandName();

        //then
        assertEquals("SendPayloadCommand", className);
    }

    @Test
    void isKeepRunningCommand() {
        //no given

        //when
        boolean isRunning = sendPayloadCommand.isKeepRunningCommand();

        //then
        assertTrue(isRunning);
    }
}