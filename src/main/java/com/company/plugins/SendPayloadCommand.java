package com.company.plugins;

import com.company.codeExecution.CodeFileHandler;
import com.company.codeExecution.CodeRunner;
import com.company.common.messages.clientToServer.ExecutionData;
import com.company.common.messages.serverToClient.BaseServerToClient;
import com.company.common.messages.serverToClient.PayloadServerToClient;
import com.company.common.statuses.ExecutionStatus;
import com.company.commands.Command;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.StringJoiner;

/**
 * The "SendPayloadCommand" class is a plugin that implements the "Command" interface.
 * It is responsible for receiving a "PayloadServerToClient" message, which contains code and arguments
 * to be run on the server, compiling and executing the code, and returning the output as an "ExecutionData" object.
 * <p>
 * The execute() method takes a "BaseServerToClient" message as input and returns an "ExecutionData" object.
 * First, the payload is cast as a "PayloadServerToClient" object and its code is handled by a "CodeFileHandler" object.
 * The code is then compiled and run using a "CodeRunner" object.
 * If the compilation or execution results in any errors, an "ExecutionData" object is returned with an error status and
 * an appropriate error message.
 * Otherwise, the output of the execution is returned as an "ExecutionData" object with a finished status.
 */

@Slf4j
public class SendPayloadCommand implements Command {

    public static final String COMMAND_NAME = "SendPayloadCommand";

    @Override
    public ExecutionData execute(BaseServerToClient message) {
        PayloadServerToClient payloadServerToClient = (PayloadServerToClient) message;

        log.info("Received code: " + payloadServerToClient);

        CodeFileHandler codeFileHandler = new CodeFileHandler(payloadServerToClient);
        try {
            File initializedCode = codeFileHandler.initialize();

            CodeRunner codeRunner = new CodeRunner();
            Process compiledCode = codeRunner.compile(initializedCode.getAbsolutePath());

            if (compiledCode.waitFor() != 0) {
                String compilationErrors = readStream(compiledCode.getErrorStream());
                log.error("There are compilation errors: " + compilationErrors);
                return getExecutionData(payloadServerToClient.getId(), compilationErrors, ExecutionStatus.ERROR);
            }

            Process runProcess = codeRunner.run(initializedCode.getAbsolutePath(), payloadServerToClient.getArguments());

            String errorsResult = readStream(runProcess.getErrorStream());
            if (!errorsResult.isEmpty()) {
                log.error("Program finishes with errors: " + errorsResult);
                return getExecutionData(payloadServerToClient.getId(), errorsResult, ExecutionStatus.ERROR);
            }

            String runResult = readStream(runProcess.getInputStream());
            log.info("Server code has been finished successfully. Here the results: " + runResult);
            return getExecutionData(payloadServerToClient.getId(), runResult, ExecutionStatus.FINISHED);

        } catch (Exception e) {
            log.error("An error occurred: " + e.getMessage());
            return getExecutionData(payloadServerToClient.getId(), e.getMessage(), ExecutionStatus.ERROR);
        } finally {
            codeFileHandler.cleanup();
        }
    }

    private ExecutionData getExecutionData(int payloadId, String messageException, ExecutionStatus status) {
        ExecutionData executionFailed = new ExecutionData(payloadId);
        executionFailed.setStatus(status);
        executionFailed.setResult(messageException);
        return executionFailed;
    }

    private String readStream(InputStream errorStream) throws IOException {
        BufferedReader error = new BufferedReader(new InputStreamReader(errorStream));
        StringJoiner joiner = new StringJoiner("\n");
        String line;
        while ((line = error.readLine()) != null) {
            joiner.add(line);
        }
        return joiner.toString();
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public boolean isKeepRunningCommand() {
        return true;
    }
}