package com.company.codeExecution;

import java.io.IOException;

/**
 * The "CodeRunner" class provides methods to compile and run Java code that has been written and stored in a directory.
 * <p>
 * The compile method takes a directory path as input, compiles all the Java files within the directory,
 * and returns a Process object that represents the compilation process.
 * The method uses the javac command to compile the Java files and specifies the output directory
 * where the compiled files will be stored.
 * <p>
 * The run method takes the path of the compiled Java files directory and command-line arguments as input,
 * runs the Java code, and returns a Process object that represents the running process.
 * The method uses the java command to run the Java code and specifies the classpath,
 * which includes the path to the compiled Java files directory,
 * and the main class to execute, along with any command-line arguments.
 * <p>
 * This is a helper class of the "SendPayloadCommand".
 */
public class CodeRunner {

    public Process compile(String initializedCodeDirectory) throws IOException, InterruptedException {
        String outputDirectory = initializedCodeDirectory + "/out";
        String compileCommand = "javac -d " + outputDirectory + " " + initializedCodeDirectory + "/*.java";
        return Runtime.getRuntime().exec(compileCommand);
    }

    public Process run(String path, String arguments) throws IOException {
        String runCommand = "java -cp " + path + "/out Main " + arguments;

        return Runtime.getRuntime().exec(runCommand);
    }
}
