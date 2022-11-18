package agile.planner.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.PriorityQueue;

import agile.planner.io.IOProcessing;
import agile.planner.manager.day.Day;
import agile.planner.task.Task;

/**
 * Handles the generation and management of the overall schedule
 *
 * @author Andrew Roe
 */
public class ScheduleManager {

    /** LinkedList of Days representing a single schedule */
    private LinkedList<Day> schedule;
    /** PriorityQueue of all Tasks in sorted order */
    private PriorityQueue<Task> taskManager;
    /** Singleton for ScheduleManager */
    private static ScheduleManager singleton;
    /** Total count for the number of errors that occurred in schedule generation */
    private int errorCount;

    /**
     * Private constructor of ScheduleManager
     *
     * Initially performs task processing as well as schedule generation
     */
    private ScheduleManager() {
        taskManager = new PriorityQueue<>();
        schedule = new LinkedList<>();
        processGeneratedSchedule();
    }

    /**
     * Gets a singleton of ScheduleManager
     *
     * @return singleton of ScheduleManager
     */
    public static ScheduleManager getSingleton() {
        if(singleton == null) {
            singleton = new ScheduleManager();
        }
        return singleton;
    }

    /**
     * Processes the Tasks from the given file
     *
     * @param filename file to be processed
     */
    public void processTasks(String filename) {
        try {
            taskManager = IOProcessing.readSchedule("data/" + filename);
            schedule = new LinkedList<>();
            errorCount = 0;
            generateDistributiveSchedule();
        } catch (FileNotFoundException e) {
            System.out.println("File could not be located");
        }
    }

    /**
     * Handles the processing of an already generated schedule via its tasks and subtasks
     */
    private void processGeneratedSchedule() {
        //Automatically called when scheduler system begins
    }

    /**
     * Adds a task to the schedule
     *
     * @param task the task to be added to schedule
     */
    public void addTask(Task task) {
        resetSchedule();
        taskManager.add(task);
        generateDistributiveSchedule();
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
        generateDistributiveSchedule();
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
     *
     * TODO will need to update to include the generate() methods
     * TODO will need to sync the client configuration settings to determine the type of scheduling
     */
    private void resetSchedule() {
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
    private void generateDistributiveSchedule() {
        PriorityQueue<Task> copy = new PriorityQueue<>();
        PriorityQueue<Task> processed = new PriorityQueue<>();
        int dayCount = 0;
        while(taskManager.size() > 0) {
            Day day = new Day(8, dayCount++);
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
            schedule.addLast(day);
        }
        this.taskManager = copy;
    }

    /**
     * Generates an entire schedule following a cram approach
     */
    private void generateCrammedSchedule() {
        //TODO will be implemented in v0.9.0
    }

    /**
     * Outputs the current day's schedule to console
     */
    public void outputCurrentDayToConsole() {
        if(schedule.isEmpty()) {
            System.out.println("Schedule is emtpy");
        } else {
            IOProcessing.writeDay(schedule.getFirst(), errorCount, null);
        }
    }

    /**
     * Outputs the total schedule to console
     */
    public void outputScheduleToConsole() {
        if(schedule.isEmpty()) {
            System.out.println("Schedule is emtpy");
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
            PrintStream output = new PrintStream(new File(filename));
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