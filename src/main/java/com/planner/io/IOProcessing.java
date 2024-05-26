package com.planner.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
     * Outputs the current day in text format
     *
     * @param day current day from Schedule
     * @param errorCount number of errors with the schedule
     * @param output PrintStream for where output is directed
     */
    public static void outputDay(Day day, int errorCount, PrintStream output) {
        PrintStream outputStream = output;
        if(output == null) {
            outputStream = System.out;
        }
        if(errorCount > 0) {
            outputStream.println(errorCount + " overflows have occurred within schedule...");
        }
        outputStream.print("Day 1: ");
        outputStream.print(day.toString());
    }

    /**
     * Outputs the schedule in text format
     *
     * @param list a list of Days from the Schedule
     * @param errorCount number of errors with the schedule
     * @param output PrintStream for where output is directed
     */
    public static void outputSchedule(List<Day> list, int errorCount, PrintStream output) {
        PrintStream outputStream = output;
        if(output == null) {
            outputStream = System.out;
        }
        if(errorCount > 0) {
            outputStream.println(errorCount + " overflows have occurred within schedule...");
        }
        int i = 1;
        for(Day day : list) {
            outputStream.print("Day " + i + ": ");
            outputStream.print(day.formattedString());
            i++;
        }
    }

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

    public static void writeScripterLogToFile(String str) throws FileNotFoundException {
        PrintStream outputStream = new PrintStream("logs/scripter.log");
        outputStream.print(str);
    }

    public static void writeSysLogToFile(String str) throws FileNotFoundException {
        PrintStream outputStream = new PrintStream("logs/system.log");
        outputStream.print(str);
    }

    public static void writeScripterPage(String page, String scriptName) throws FileNotFoundException {
        PrintStream outputStream = new PrintStream("data/html/" + scriptName + ".html");
        outputStream.print(page);
    }
}