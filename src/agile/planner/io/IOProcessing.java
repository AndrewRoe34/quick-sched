package agile.planner.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

import agile.planner.manager.scheduler.day.Day;
import agile.planner.task.Task;
import agile.planner.user.UserConfig;

/**
 * Handles all input/output functionality for the ScheduleManager
 *
 * @author Andrew Roe
 */
public class IOProcessing {

    /**
     * Outputs the current day in text format
     *
     * @param day current day from Schedule
     * @param errorCount number of errors with the schedule
     * @param output PrintStream for where output is directed
     */
    public static void writeDay(Day day, int errorCount, PrintStream output) {
        if(output == null) {
            output = System.out;
        }
        if(errorCount > 0) {
            output.println(errorCount + " overflows have occurred within schedule...");
        }
        output.print("Day 1: ");
        output.println(day.toString());
    }

    /**
     * Outputs the schedule in text format
     *
     * @param list a list of Days from the Schedule
     * @param errorCount number of errors with the schedule
     * @param output PrintStream for where output is directed
     */
    public static void writeSchedule(List<Day> list, int errorCount, PrintStream output) {
        if(output == null) {
            output = System.out;
        }
        if(errorCount > 0) {
            output.println(errorCount + " overflows have occurred within schedule...");
        }
        int i = 1;
        for(Day day : list) {
            output.print("Day " + i + ": ");
            output.println(day.toString());
            i++;
        }
    }

    /**
     * Processes the specified file for the list of all specified Tasks
     *
     * @param filename file to be processed
     * @param pq PriorityQueue for holding Tasks in sorted order
     * @param taskId identification number for each Task
     * @return last due date for Task
     * @throws FileNotFoundException if task file does not exist
     */
    public static int readTasks(String filename, PriorityQueue<Task> pq, int taskId) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new File(filename));
        fileScanner.useDelimiter(",|\\r\\n|\\n");
        int maxDate = 0;
        while(fileScanner.hasNextLine()) {
            String name = fileScanner.next();
            int hours = fileScanner.nextInt();
            int date = fileScanner.nextInt();
            pq.add(new Task(taskId++, name, hours, date));
            maxDate = Math.max(maxDate, date);
        }
        fileScanner.close();
        return maxDate;
    }

    /**
     * Processes the cfg file for the contents of the week
     *
     * @return UserConfig instance
     * @throws FileNotFoundException if cfg file does not exist
     */
    public static UserConfig readCfg() throws FileNotFoundException {
        Scanner fileScanner = new Scanner("settings/profile.cfg");

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

        while(fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if(line.charAt(0) != '#') {
                if(count == 0 && "user_name=".equals(line.substring(0, 10))) {
                    userName = line.substring(10);
                } else if(count == 1 && "user_email=".equals(line.substring(0, 11))) {
                    userEmail = line.substring(11);
                } else if(count == 2 && "global_hours=".equals(line.substring(0, 13))) {
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
                } else if(count == 3 && "max_days=".equals(line.substring(0, 9))) {
                    maxDays = Integer.parseInt(line.substring(9));
                } else if(count == 4 && "archive_days=".equals(line.substring(0, 13))) {
                    archiveDays = Integer.parseInt(line.substring(13));
                } else if(count == 5 && "priority=".equals(line.substring(0, 9))) {
                    priority = Boolean.parseBoolean(line.substring(9));
                } else if (count == 6 && "overflow=".equals(line.substring(0, 9))) {
                    overflow = Boolean.parseBoolean(line.substring(9));
                } else if (count == 7 && "fit_schedule=".equals(line.substring(0, 13))) {
                    fitSchedule = Boolean.parseBoolean(line.substring(13));
                } else if (count == 8 && "schedule_algorithm=".equals(line.substring(0, 19))) {
                    schedulingAlgorithm = Integer.parseInt(line.substring(19));
                }
                count++;
            }
        }
        if(count != 9) {
            throw new InputMismatchException("Config file missing data");
        }
        return new UserConfig(userName, userEmail, globalHr, maxDays, archiveDays, priority, overflow, fitSchedule, schedulingAlgorithm);
    }

    public static List<Day> readSchedule(String filename) {
        return null; //TODO need to finish
    }

    /*

    #Day ...:
    -Task1
    -Task2
    -Task3

    #Day ...:
    ...
     */
}