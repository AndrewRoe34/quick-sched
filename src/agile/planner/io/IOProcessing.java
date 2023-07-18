package agile.planner.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import agile.planner.schedule.day.Day;
import agile.planner.data.Task;
import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.user.UserConfig;

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
     * Processes the specified file for the list of all specified Tasks
     *
     * @param filename file to be processed
     * @param pq       PriorityQueue for holding Tasks in sorted order
     * @param taskMap Map for holding ID/Task relationship
     * @param taskId   identification number for each Task
     * @return last due date for Task
     * @throws FileNotFoundException if task file does not exist
     */
    @Deprecated
    public static int readTasks(String filename, PriorityQueue<Task> pq, Map<Integer, Task> taskMap, int taskId) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new File(filename));
        fileScanner.useDelimiter(",|\\r\\n|\\n");
        int maxDate = 0;
        int taskCount = taskId;
        while(fileScanner.hasNextLine()) {
            String name = fileScanner.next();
            int hours = fileScanner.nextInt();
            int date = fileScanner.nextInt();
            Task t1 = new Task(taskCount++, name, hours, date);
            pq.add(t1);
            taskMap.put(taskCount - 1, t1);
            maxDate = Math.max(maxDate, date);
        }
        fileScanner.close();
        return maxDate;
    }

    /**
     * Processes the cfg file for the contents of the week
     *
     * @param filename name of file
     * @return UserConfig instance
     * @throws FileNotFoundException if cfg file does not exist
     */
    public static UserConfig readCfg(String filename) throws FileNotFoundException {
        String filePath = filename;
        if(filePath == null) {
            filePath = "settings/profile.cfg";
        }

        Scanner fileScanner = new Scanner(new File(filePath));

        int variableCount = 10;

        int count = 0;
        String userName = null;
        String userEmail = null;
        int[] globalHr = new int[7];
        int maxDays = 14;
        int archiveDays = 14;
        boolean priority = false;
        boolean overflow = true;
        boolean fitSchedule = false;
        int schedulingAlgorithm = 1;
        int minHours = 1;

        while(fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if(line.charAt(0) != '#') {
                count++;
                if(count == 1 && line.length() > 10 && "user_name=".equals(line.substring(0, 10))) {
                    userName = line.substring(10);
                } else if(count == 2 && line.length() > 11 && "user_email=".equals(line.substring(0, 11))) {
                    userEmail = line.substring(11);
                } else if(count == 3 && line.length() == 28 && "global_hours=".equals(line.substring(0, 13))) {
                    Scanner lineScanner = new Scanner(line.substring(13));
                    lineScanner.useDelimiter(",");
                    globalHr[0] = Integer.parseInt(lineScanner.next().substring(1));
                    globalHr[1] = lineScanner.nextInt();
                    globalHr[2] = lineScanner.nextInt();
                    globalHr[3] = lineScanner.nextInt();
                    globalHr[4] = lineScanner.nextInt();
                    globalHr[5] = lineScanner.nextInt();
                    String satStr = lineScanner.next();
                    globalHr[6] = Integer.parseInt(satStr.substring(0, satStr.length() - 1));
                } else if(count == 4 && line.length() > 9 && "max_days=".equals(line.substring(0, 9))) {
                    maxDays = Integer.parseInt(line.substring(9));
                } else if(count == 5 && line.length() > 13 && "archive_days=".equals(line.substring(0, 13))) {
                    archiveDays = Integer.parseInt(line.substring(13));
                } else if(count == 6 && line.length() > 9 && "priority=".equals(line.substring(0, 9))) {
                    priority = Boolean.parseBoolean(line.substring(9));
                } else if (count == 7 && line.length() > 9 && "overflow=".equals(line.substring(0, 9))) {
                    overflow = Boolean.parseBoolean(line.substring(9));
                } else if (count == 8 && line.length() > 13 && "fit_schedule=".equals(line.substring(0, 13))) {
                    fitSchedule = Boolean.parseBoolean(line.substring(13));
                } else if (count == 9 && line.length() > 19 && "schedule_algorithm=".equals(line.substring(0, 19))) {
                    schedulingAlgorithm = Integer.parseInt(line.substring(19));
                } else if (count == 10 && line.length() > 10 && "min_hours=".equals(line.substring(0, 10)) ){
                    minHours = schedulingAlgorithm = Integer.parseInt(line.substring(10));
                } else {
                    throw new InputMismatchException("Config file improperly configured");
                }
            }
        }
        if(count != variableCount) {
            throw new InputMismatchException("Config file missing data");
        }
        return new UserConfig(userName, userEmail, globalHr, maxDays, archiveDays, priority, overflow, fitSchedule, schedulingAlgorithm, minHours);
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
}