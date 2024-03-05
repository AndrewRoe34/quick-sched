package com.agile.planner.schedule;

import com.agile.planner.schedule.day.Day;
import com.agile.planner.data.Task;
import com.agile.planner.user.UserConfig;
import com.agile.planner.util.EventLog;

import java.util.List;
import java.util.PriorityQueue;

/**
 * The class {@code DynamicScheduler} implements the interface {@link Scheduler} for more complex scheduling actions.
 * This involves the combination of Longest-Remaining-Time-First + Round-Robin in order to compute the schedule for the week.
 * {@link UserConfig} options allow for other varieties such as Shortest-Remaining-Time-First + Round-Robin as well as other
 * restraints for scheduling.
 * <p>
 * Schedule corrections occur when overflows happen and are properly handled in accordance with the algorithm type. In {@code DynamicScheduler},
 * this involves maintaining that distributive mindset when reallocating the tasks across the given week.
 *
 * @author Andrew Roe
 * @author Lucia Langaney
 * @since 0.3.0
 */
public class DynamicScheduler implements Scheduler {

    /** Singleton for DynamicScheduler */
    private static DynamicScheduler singleton;
    /** Holds relevant data for user settings in scheduling */
    private final UserConfig userConfig;
    /** EventLog for logging data on Day actions */
    private EventLog eventLog;

    /**
     * Constructs a new {@code DynamicScheduler} with a given {@link UserConfig} and {@link EventLog}
     *
     * @param userConfig user settings for scheduling purposes
     * @param eventLog EventLog for logging data on Day actions
     */
    private DynamicScheduler(UserConfig userConfig, EventLog eventLog) {
        this.userConfig = userConfig;
        this.eventLog = eventLog;
    }

    /**
     * Retrieves a singleton of {@code DynamicScheduler} for scheduling purposes
     *
     * @param userConfig user settings for scheduling purposes
     * @param eventLog EventLog for logging data on Day actions
     * @return instance of {@code DynamicScheduler}
     */
    public static DynamicScheduler getSingleton(UserConfig userConfig, EventLog eventLog) {
        if(singleton == null) {
            singleton = new DynamicScheduler(userConfig, eventLog);
        }
        return singleton;
    }

    @Override
    public int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> taskManager) {
        PriorityQueue<Task> incomplete = new PriorityQueue<>();
        int numErrors = errorCount;
        while(day.hasSpareHours() && !taskManager.isEmpty()) {
            Task task = taskManager.remove();
            boolean validTaskStatus = true;
            if(day.getDate().equals(task.getDueDate())) {
                validTaskStatus = addTaskSameDay(task, day);
            } else {
                if(task.getAverageNumHours() == 0) {
                    task.setAverageNumHours(day.getDate());
                }
                int hours = task.getAverageNumHours();
                //Handles the case where we actually have fewer hours available due to scheduling
                hours = Math.min(hours, task.getSubTotalHoursRemaining());
                //Fixes the number of hours according to what the Day has available
                hours = hours + day.getSize() > day.getCapacity() ? day.getCapacity() - day.getSize() : hours;
                day.addSubTask(task, hours, false);
            }
            if(task.getSubTotalHoursRemaining() > 0) {
                eventLog.reportDayAction(day, task, validTaskStatus);
                incomplete.add(task);
            } else {
                complete.add(task);
                eventLog.reportDayAction(day, task, validTaskStatus);
                numErrors += validTaskStatus ? 0 : 1;
                if(!day.hasSpareHours()) {
                    while(taskManager.size() > 0 && taskManager.peek().getDueDate().equals(day.getDate())) {
                        Task dueTask = taskManager.remove();
                        complete.add(dueTask);
                        validTaskStatus = addTaskSameDay(dueTask, day);
                        eventLog.reportDayAction(day, dueTask, validTaskStatus);
                        numErrors++;
                    }
                }
            }
        }
        while(!incomplete.isEmpty()) {
            taskManager.add(incomplete.remove());
        }
        return numErrors;
    }

    /**
     * Handles adding a Task when it is due the same day
     *
     * @param task Task being added
     * @param day Day being modified
     * @return boolean status for whether Task was added without overflow
     */
    private boolean addTaskSameDay(Task task, Day day) {
        boolean overflow = task.getSubTotalHoursRemaining() > day.getSpareHours();
        int hours = task.getSubTotalHoursRemaining();
        day.addSubTask(task, hours, overflow);
        return !overflow;
    }

    @Override
    public boolean correctSchedule(List<Day> schedule, int errorCount) {
        return false;
    }
}
