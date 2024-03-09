package com.agile.planner.schedule;

import com.agile.planner.schedule.day.Day;
import com.agile.planner.models.Task;
import com.agile.planner.user.UserConfig;
import com.agile.planner.util.EventLog;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The class {@code CompactScheduler} implements the interface {@link Scheduler} for compact scheduling actions.
 * This involves the utilization of Longest-Job-First in order to compute the schedule for the week. {@link UserConfig}
 * options allow for another style such as Shortest-Job-First well as other restraints for scheduling.
 * <p>
 * Schedule corrections occur when overflows happen and are properly handled in accordance with the algorithm type. In {@code CompactScheduler},
 * this involves maintaining that distributive mindset when reallocating the tasks across the given week.
 *
 * @author Andrew Roe
 * @since 0.3.0
 */
public class CompactScheduler implements Scheduler {

    /** Singleton for CompactScheduler */
    private static CompactScheduler singleton;
    /** Holds relevant data for user settings in scheduling */
    private final UserConfig userConfig;
    /** EventLog for logging data on Day actions */
    private final EventLog eventLog;

    /**
     * Constructs a new {@code CompactScheduler} with a given {@link UserConfig} and {@link EventLog}
     *
     * @param userConfig user settings for scheduling purposes
     * @param eventLog EventLog for logging data on Day actions
     */
    private CompactScheduler(UserConfig userConfig, EventLog eventLog) {
        this.userConfig = userConfig;
        this.eventLog = eventLog;

    }

    /**
     * Retrieves a singleton of {@code CompactScheduler} for scheduling purposes
     *
     * @param userConfig user settings for scheduling purposes
     * @param eventLog EventLog for logging data on Day actions
     * @return instance of {@code CompactScheduler}
     */
    public static CompactScheduler getSingleton(UserConfig userConfig, EventLog eventLog) {
        if(singleton == null) {
            singleton = new CompactScheduler(userConfig, eventLog);
        }
        return singleton;
    }

    @Override
    public int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> taskManager) {
        //Note: We do not need to worry about 'min_hours' since CompactScheduler focuses on filling up each day with tasks
        PriorityQueue<Task> incomplete = new PriorityQueue<>();
        int numErrors = errorCount;
        while(day.hasSpareHours() && taskManager.size() > 0) {
            Task task = taskManager.remove();
            int maxHours = 0;
            if(task.getDueDate().equals(day.getDate())) {
                maxHours = userConfig.isFitSchedule() ? Math.min(day.getSpareHours(), task.getSubTotalHoursRemaining()) : task.getSubTotalHoursRemaining();
            } else {
                maxHours = Math.min(day.getSpareHours(), task.getSubTotalHoursRemaining());
            }
            boolean validTaskStatus = day.addSubTaskManually(task, maxHours);
            if(userConfig.isFitSchedule() && task.getSubTotalHoursRemaining() > 0) {
                eventLog.reportDayAction(day, task, validTaskStatus);
                complete.add(task);
                while(taskManager.size() > 0 && taskManager.peek().getDueDate().equals(day.getDate())) {
                    complete.add(taskManager.remove());
                }
            } else {
                if(task.getSubTotalHoursRemaining() > 0) {
                    incomplete.add(task);
                } else {
                    complete.add(task);
                }
                eventLog.reportDayAction(day, task, validTaskStatus);
                numErrors += validTaskStatus ? 0 : 1;
                if(!day.hasSpareHours() && !userConfig.isFitSchedule()) {
                    while(taskManager.size() > 0 && taskManager.peek().getDueDate().equals(day.getDate())) {
                        Task dueTask = taskManager.remove();
                        complete.add(dueTask);
                        day.addSubTaskManually(dueTask, dueTask.getSubTotalHoursRemaining());
                        eventLog.reportDayAction(day, dueTask, validTaskStatus);
                        numErrors++;
                    }
                }
            }
        }
        while(incomplete.size() > 0) {
            taskManager.add(incomplete.remove());
        }
        return numErrors;
    }

    @Override
    public boolean correctSchedule(List<Day> schedule, int errorCount) {
        List<Integer> overflowIndex = new ArrayList<>();
        List<Integer> overflowHours = new ArrayList<>();
        int totalOverflowHours = 0;
        for(int i = schedule.size() - 1; i >= 0; i--) {
            Day d1 = schedule.get(i);
            int hoursOverflow = d1.getCapacity() - d1.getTotalHours() < 0 ? d1.getTotalHours() - d1.getCapacity() : 0;
            if(hoursOverflow > 0) {
                overflowIndex.add(i);
                overflowHours.add(hoursOverflow);
                totalOverflowHours += hoursOverflow;
            }
        }
        int leftoverDays = overflowIndex.size() - schedule.size();
        return false;
    }
}
