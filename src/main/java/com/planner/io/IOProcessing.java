package com.planner.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.planner.schedule.day.Day;
import com.planner.scripter.exception.InvalidGrammarException;

/**
 * Handles all input/output functionality for the ScheduleManager
 *
 * @author Andrew Roe
 */
public class IOProcessing {

    /** Maximum buffer size for input/output processing */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Writes the JBin string to a binary file
     *
     * @param filename name of output file
     * @param jBin JBin string
     */
    public static void writeJBinFile(String filename, String jBin) {
        if(filename.length() < 5 || !".jbin".equals(filename.substring(filename.length() - 5))) {
            throw new InvalidGrammarException("Expected .jbin file extension but was not");
        }
        try (OutputStream binaryWriter = new FileOutputStream(filename)) {
            byte[] bytes = jBin.getBytes(StandardCharsets.UTF_8);
            binaryWriter.write(bytes);
        } catch (IOException e) {
            //empty catch block
        }
    }

    /**
     * Reads the Java Binary Serialization file
     *
     * @param filename name of saved system file
     * @return JBin string
     */
    public static String readJBinFile(String filename) {
        if(filename.length() < 5 || !".jbin".equals(filename.substring(filename.length() - 5))) {
            throw new InvalidGrammarException("Expected .jbin file extension but was not");
        }
        StringBuilder sb = new StringBuilder();
        try (FileInputStream binaryReader = new FileInputStream(filename)) {
            byte[] bytes = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while((bytesRead = binaryReader.read(bytes)) != -1) {
                appendBytesToString(sb, bytes, bytesRead);
            }
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }

    /**
     * Appends bytes to StringBuilder
     *
     * @param sb StringBuilder being utilized
     * @param bytes bytes array being processed
     * @param bytesRead number of bytes available
     */
    private static void appendBytesToString(StringBuilder sb, byte[] bytes, int bytesRead) {
        for(int i = 0; i < bytesRead; i++) {
            sb.append((char) bytes[i]);
        }
    }

    public static void writeScripterLogToFile(String str) throws IOException {
        // Define the path for the 'logs' directory
        Path logsDirectory = Paths.get("logs");

        // Create the 'logs' directory if it does not exist
        if (!Files.exists(logsDirectory)) {
            Files.createDirectories(logsDirectory);
        }

        // Create a PrintStream to write to a file in the 'logs' directory
        Path logFile = logsDirectory.resolve("scripter.log");
        try (PrintStream outputStream = new PrintStream(logFile.toFile())) {
            outputStream.print(str);
        }
    }

    public static void writeSysLogToFile(String str) throws IOException {
        // Define the path for the 'logs' directory
        Path logsDirectory = Paths.get("logs");

        // Create the 'logs' directory if it does not exist
        if (!Files.exists(logsDirectory)) {
            Files.createDirectories(logsDirectory);
        }

        // Create a PrintStream to write to a file in the 'logs' directory
        Path logFile = logsDirectory.resolve("system.log");
        try (PrintStream outputStream = new PrintStream(logFile.toFile())) {
            outputStream.print(str);
        }
    }

    public static void writeSesLogToFile(String str) throws IOException {
        // Define the path for the 'logs' directory
        Path logsDirectory = Paths.get("logs");

        // Create the 'logs' directory if it does not exist
        if (!Files.exists(logsDirectory)) {
            Files.createDirectories(logsDirectory);
        }

        // Create a PrintStream to write to a file in the 'logs' directory
        Path logFile = logsDirectory.resolve("session.log");
        try (PrintStream outputStream = new PrintStream(logFile.toFile())) {
            outputStream.print(str);
        }
    }

    public static void writeScripterPage(String page, String scriptName) throws IOException {
        // Define the path for the 'logs' directory
        Path htmlDirectory = Paths.get("data/html");

        // Create the 'logs' directory if it does not exist
        if (!Files.exists(htmlDirectory)) {
            Files.createDirectories(htmlDirectory);
        }

        // Create a PrintStream to write to a file in the 'logs' directory
        Path htmlFile = htmlDirectory.resolve(scriptName + ".html");
        try (PrintStream outputStream = new PrintStream(htmlFile.toFile())) {
            outputStream.print(page);
        }
    }
}