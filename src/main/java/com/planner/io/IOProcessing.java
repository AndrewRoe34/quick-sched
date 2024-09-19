package com.planner.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles all input/output functionality for the ScheduleManager
 *
 * @author Andrew Roe
 */
public class IOProcessing {

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

    public static void writeSerializationFile(String filename, String str) throws IOException {
        File scheduleFile = new File("schedules\\" + filename);
        if (!scheduleFile.getParentFile().exists()) {
            scheduleFile.getParentFile().mkdir();
        }
        scheduleFile.createNewFile();

        FileWriter fw = new FileWriter(scheduleFile.getAbsoluteFile());
        BufferedWriter scheduleWriter = new BufferedWriter(fw);

        scheduleWriter.write(str);
        scheduleWriter.close();
    }
}