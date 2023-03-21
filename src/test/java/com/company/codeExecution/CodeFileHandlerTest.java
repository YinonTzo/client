package com.company.codeExecution;

import com.company.common.messages.serverToClient.PayloadServerToClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CodeFileHandlerTest {

    @Mock
    private PayloadServerToClient payloadServerToClientMock;

    private CodeFileHandler codeFileHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        codeFileHandler = new CodeFileHandler(payloadServerToClientMock);
    }

    @AfterEach
    public void tearDown() {
        codeFileHandler.cleanup();
    }

    @Test
    void initialize() {
        //given
        when(payloadServerToClientMock.getId()).thenReturn(123);

        String payload = "Mainfile_name" +
                "public class Main {" +
                "    public static void main(String[] args) { " +
                "       System.out.println(\"Hello World\");" +
                "    }" +
                "}";

        byte[] payloadAsBytes = payload.getBytes(StandardCharsets.UTF_8);

        when(payloadServerToClientMock.getPayload()).thenReturn(payloadAsBytes);

        //when
        File directory = null;
        try {
            directory = codeFileHandler.initialize();
        } catch (IOException e) {
            fail(e);
        }

        //then
        assertTrue(directory.exists());
        assertTrue(directory.isDirectory());

        File subDirectory = new File(CodeFileHandler.CODE_DIRECTORY_NAME, "123");
        assertTrue(subDirectory.exists());
        assertTrue(subDirectory.isDirectory());

        File mainFile = new File(subDirectory, "Main.java");
        assertTrue(mainFile.exists());
        assertTrue(mainFile.isFile());

        List<String> expectedLines = Arrays.asList(
                "public class Main {" +
                        "    public static void main(String[] args) {" +
                        "        System.out.println(\"Hello World\");" +
                        "    }" +
                        "}"
        );

        List<String> actualLines = null;
        try {
            actualLines = Files.readAllLines(Paths.get(mainFile.toURI()));
        } catch (IOException e) {
            fail(e);
        }
        assertEquals(expectedLines, actualLines);
    }

    @Test
    void cleanup() {
        //given
        File subDirectory = new File(CodeFileHandler.CODE_DIRECTORY_NAME, "123");
        subDirectory.mkdirs();
        File mainFile = new File(subDirectory, "Main.java");
        try {
            mainFile.createNewFile();
        } catch (IOException e) {
            fail(e);
        }

        assertTrue(subDirectory.exists());
        assertTrue(mainFile.exists());

        when(payloadServerToClientMock.getId()).thenReturn(123);

        //when
        codeFileHandler.cleanup();

        //then
        assertFalse(subDirectory.exists());
        assertFalse(mainFile.exists());
    }
}