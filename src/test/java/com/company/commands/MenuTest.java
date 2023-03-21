package com.company.commands;

import com.company.common.messages.clientToServer.ExecutionData;
import com.company.common.messages.serverToClient.BaseServerToClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MenuTest {

    public static final String COMMAND1 = "command1";
    public static final String COMMAND2 = "command2";
    private Menu menu;
    private Map<String, Command> commands;

    @BeforeEach
    void setUp() {
        commands = new HashMap<>();
        commands.put(COMMAND1, mock(Command.class));
        commands.put(COMMAND2, mock(Command.class));
        menu = new Menu(commands);
    }

    @Test
    void execute() {
        //given
        BaseServerToClient serverToClient = new BaseServerToClient();
        serverToClient.setType(COMMAND1);
        int messageId = 1;
        ExecutionData expectedExecutionData = new ExecutionData(messageId);

        when(commands.get(COMMAND1).isKeepRunningCommand()).thenReturn(true);
        when(commands.get(COMMAND1).execute(serverToClient)).thenReturn(expectedExecutionData);

        //when
        ExecutionData actualExecutionData = menu.execute(serverToClient);

        //then
        assertEquals(expectedExecutionData, actualExecutionData);
        assertTrue(menu.isRun());
    }
}