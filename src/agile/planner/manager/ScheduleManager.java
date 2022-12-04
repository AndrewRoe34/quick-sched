package agile.planner.manager;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

import agile.planner.io.IOProcessing;
import agile.planner.manager.day.Day;
import agile.planner.task.Task;
import agile.planner.util.EventLog;

/**
 * Handles the generation and management of the overall schedule
 *
 * @author Andrew Roe
 */
public class ScheduleManager {

    /** LinkedList of Days representing a single schedule */
    private List<Day> schedule;
    /** PriorityQueue of all Tasks in sorted order */
    private PriorityQueue<Task> taskManager;
    /** Singleton for ScheduleManager */
    private static ScheduleManager singleton;
    /** Logs all actions performed by user */
    private static EventLog eventLog;
    /** Standard number of hours for the week */
    private int[] week;
    /** Stores custom hours for future days */
    private Map<Integer, Integer> customHours;
    /** Total count for the number of errors that occurred in schedule generation */
    private int errorCount;
    /** Last day Task is due */
    private int lastDueDate;

    /**
     * Private constructor of ScheduleManager
     * Initially performs task processing as well as schedule generation
     *
     * @throws FileNotFoundException if event log file does not exist
     */
    private ScheduleManager() throws FileNotFoundException {
        taskManager = new PriorityQueue<>();
        schedule = new LinkedList<>();
        customHours = new HashMap<>();
        taskManager = new PriorityQueue<>();
        eventLog = EventLog.getEventLog();
        processSettingsCfg();
    }

    /**
     * Processes all the settings configurations to be used
     */
    private void processSettingsCfg() {
        try {
            week = IOProcessing.readCfg();
        } catch (FileNotFoundException e) {
            System.out.println("Could not process settings cfg");
        }
    }

    /**
     * Gets a singleton of ScheduleManager
     *
     * @return singleton of ScheduleManager
     * @throws FileNotFoundException if event log file does not exist
     */
    public static ScheduleManager getSingleton() throws FileNotFoundException {
        if(singleton == null) {
            singleton = new ScheduleManager();
        }
        return singleton;
    }

    /**
     * Sets standard hours for a day
     *
     * @param day day to be modified
     * @param hours number of hours to be set
     */
    public void setGlobalHours(int day, int hours) {
        if(day >= 0 && day < 7 && hours >= 0 && hours <= 24) {
            week[day] = hours;
        } else {
            throw new IllegalArgumentException("Invalid data for standard days of week");
        }
    }

    /**
     * Sets custom hours for a single instance of a day
     *
     * @param day day to be modified
     * @param hours number of hours to be set
     */
    public void setCustomHours(int day, int hours) {
        if(day >= 0 && hours >= 0 && hours <= 24) {
            customHours.put(day, hours);
        } else {
            throw new IllegalArgumentException("Invalid data for custom days of week");
        }

    }

    /**
     * Processes the Tasks from the given file
     *
     * @param filename file to be processed
     */
    public void processSchedule(String filename) {
        try {
            lastDueDate = IOProcessing.readTasks("data/" + filename, taskManager);
            schedule = new ArrayList<>();
            errorCount = 0;
            generateSchedule();
        } catch (FileNotFoundException e) {
            System.out.println("File could not be located");
        }
    }

    /**
     * Adds a task to the schedule
     *
     * @param task the task to be added to schedule
     */
    public void addTask(Task task) {
        resetSchedule();
        taskManager.add(task);
        generateSchedule();
    }

    /**
     * Removes a task from the schedule given the day and task indices
     *
     * @param dayIndex day that the task exists
     * @param taskIndex index of the task to be removed
     * @return Task removed from Day
     */
    public Task removeTask(int dayIndex, int taskIndex) {
        dayIndex--;
        taskIndex--;
        if(dayIndex < 0 || dayIndex >= schedule.size()) {
            return null;
        }
        Day day = schedule.get(dayIndex);
        if(taskIndex < 0 || taskIndex >= day.getNumSubTasks()) {
            return null;
        }
        Task task = day.getParentTask(taskIndex);
        taskManager.remove(task);
        resetSchedule();
        generateSchedule();
        return task;
    }

    /**
     * Edits a task from the schedule given the day and task indices
     *
     * @param dayIndex day that the task exists
     * @param taskIndex index of the task to be removed
     */
    public void editTask(int dayIndex, int taskIndex) {

    }

    /**
     * Resets all the tasks as well as the entire schedule for it to be regenerated
     * TODO will need to update to include the generate() methods
     * TODO will need to sync the client configuration settings to determine the type of scheduling
     */
    public void resetSchedule() {
        schedule = new LinkedList<>();
        PriorityQueue<Task> copy = new PriorityQueue<>();
        while(taskManager.size() > 0) {
            Task task = taskManager.remove();
            task.reset();
            copy.add(task);
        }
        taskManager = copy;
        errorCount = 0;
    }

    /**
     * Generates an entire schedule following a distributive approach
     */
    private void generateSchedule() {
        PriorityQueue<Task> copy = new PriorityQueue<>();
        PriorityQueue<Task> processed = new PriorityQueue<>();

        schedule = new ArrayList<>(14);
        Calendar today = Calendar.getInstance();
        int idx = today.get(Calendar.DAY_OF_WEEK) - 1;
        int dayIdx = 0;

        while(taskManager.size() > 0) {
            Day day = null; //TODO              // schedule.add(new Day(week[idx % 7], i));
            assignDay(day, copy, processed);
        }
        this.taskManager = copy;
    }

    private void assignDay(Day day, PriorityQueue<Task> copy, PriorityQueue<Task> processed) {
        while(day.hasSpareHours() && taskManager.size() > 0) {
            Task task = taskManager.remove();
            boolean validTaskStatus = day.addSubTask(task);
            if(task.getSubTotalHoursRemaining() > 0) {
                processed.add(task);
            } else {
                copy.add(task);
                errorCount += validTaskStatus ? 0 : 1;
                if(!validTaskStatus || !day.hasSpareHours()) {
                    while(taskManager.size() > 0 && taskManager.peek().getDueDate().equals(day.getDate())) {
                        copy.add(taskManager.peek());
                        day.addSubTask(taskManager.remove());
                        errorCount++;
                    }
                }
            }
        }
        while(processed.size() > 0) {
            taskManager.add(processed.remove());
        }
    }

    /**
     * Generates an entire schedule following the rushed approach
     */
    private void generateCrammedSchedule() {
        //TODO will be implemented in v0.9.0
    }

    /**
     * Outputs the current day's schedule to console
     */
    public void outputCurrentDayToConsole() {
        if(schedule.isEmpty()) {
            System.out.println("Schedule is empty");
        } else {
            IOProcessing.writeDay(schedule.get(0), errorCount, null);
        }
    }

    /**
     * Outputs the total schedule to console
     */
    public void outputScheduleToConsole() {
        if(schedule.isEmpty()) {
            System.out.println("Schedule is empty");
        } else {
            IOProcessing.writeSchedule(schedule, errorCount, null);
        }
    }

    /**
     * Outputs the schedule to the specified file
     *
     * @param filename file to be outputted to
     */
    public void outputScheduleToFile(String filename) {
        try {
            PrintStream output = new PrintStream(filename);
            IOProcessing.writeSchedule(schedule, errorCount, output);
            output.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error with processing file");
        }
    }

    /**
     * Determines whether the schedule is empty
     *
     * @return boolean value for whether schedule is empty
     */
    public boolean scheduleIsEmpty() {
        return schedule.isEmpty();
    }

}