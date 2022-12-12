package agile.planner.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

import agile.planner.manager.day.Day;
import agile.planner.task.Task;

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
     * @return last due date for Task
     * @throws FileNotFoundException if file does not exist
     */
    public static int readTasks(String filename, PriorityQueue<Task> pq) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new File(filename));
        fileScanner.useDelimiter(",|\\r\\n|\\n");
        int maxDate = 0;
        int id = 0;
        while(fileScanner.hasNextLine()) {
            String name = fileScanner.next();
            int hours = fileScanner.nextInt();
            int date = fileScanner.nextInt();
            pq.add(new Task(name, hours, date));
            maxDate = Math.max(maxDate, date);
            id++;
        }
        fileScanner.close();
        return maxDate;
    }

    /**
     * Processes the cfg file for the contents of the week
     *
     * @return int array for number of hours for each day
     */
    public static int[] readCfg() throws FileNotFoundException {
        Scanner cfgScanner = new Scanner(new File("week.cfg"));
        int[] week = new int[7];
        for(int i = 0; i < week.length; i++) {
            week[i] = cfgScanner.nextInt();
        }
        return week;
    }
}