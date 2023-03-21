package com.company.codeExecution;

import com.company.common.messages.serverToClient.PayloadServerToClient;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The "CodeFileHandler" class is responsible for handling the code files received from the client
 * and managing the directories where the code is stored.
 * <p>
 * The class has a constructor that takes a PayloadServerToClient object as a parameter,
 * which contains the code and other metadata related to the code.
 * <p>
 * The initialize method creates a subdirectory with the name of the PayloadServerToClient object's ID,
 * extracts the Java files and their code from the payload,
 * and writes the code to the corresponding Java files in the subdirectory.
 * <p>
 * The cleanup method is used to delete the subdirectory and its contents once the execution is completed.
 * <p>
 * Overall, the CodeFileHandler class is a useful utility class for handling and managing code files in the server,
 * and is a helper class of the "SendPayloadCommand".
 */
@Slf4j
public class CodeFileHandler {

    public static final String CODE_DIRECTORY_NAME = "code"; //TODO: will be in configuration

    public static final String FILE_NAME_DELIMITER = "file_name";
    public static final String EOF_DELIMITER = "EOF";

    private final PayloadServerToClient payloadServerToClient;

    public CodeFileHandler(PayloadServerToClient payloadServerToClient) {
        this.payloadServerToClient = payloadServerToClient;
    }

    public File initialize() throws IOException {
        File subDirectory = createDirectory(String.valueOf(payloadServerToClient.getId()));

        List<Pair> projectFiles = extractJavaFilesAndCode(payloadServerToClient);

        writeCodeToJavaFiles(subDirectory, projectFiles);

        return subDirectory;
    }

    private File createDirectory(String path) {
        File directory = new File(CODE_DIRECTORY_NAME);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create subdirectory with id name
        File subDirectory = new File(directory, path);
        if (!subDirectory.exists()) {
            subDirectory.mkdir();
        }

        return subDirectory;
    }

    private List<Pair> extractJavaFilesAndCode(PayloadServerToClient payloadServerToClient) {
        List<Pair> files = new ArrayList<>();

        String payload = new String(payloadServerToClient.getPayload());

        List<String> filesData = List.of(payload.split(EOF_DELIMITER));
        for (String fileData : filesData) {
            List<String> fileParts = List.of(fileData.split(FILE_NAME_DELIMITER));
            String fileName = fileParts.get(0);
            String code = fileParts.get(1);
            files.add(new Pair(fileName, code));
        }
        return files;
    }

    private void writeCodeToJavaFiles(File subDirectory, List<Pair> projectFiles) throws IOException {

        for (Pair file : projectFiles) {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(subDirectory + "/" + file.fileName + ".java")
            );
            writer.write(file.javaCode);
            writer.close();
        }
    }

    public void cleanup() {
        log.info("Clean up the directories..");
        cleanup(CODE_DIRECTORY_NAME, String.valueOf(payloadServerToClient.getId()));
    }

    private void cleanup(String directoryName, String subDirectoryName) {

        File subDirectory = new File(directoryName, subDirectoryName);

        if (subDirectory.exists()) {
            // Delete all files and directories within the subdirectory
            for (File file : Objects.requireNonNull(subDirectory.listFiles())) {
                if (file.isDirectory()) {
                    cleanup(subDirectory.getAbsolutePath(), file.getName());
                } else {
                    file.delete();
                }
            }

            // Delete the subdirectory itself
            subDirectory.delete();
        }
    }
}
