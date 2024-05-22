package com.planner.schedule;

import com.planner.schedule.day.Day;
import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.util.EventLog;
import com.planner.util.Time;

import java.util.Calendar;
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
    protected static CompactScheduler getSingleton(UserConfig userConfig, EventLog eventLog) {
        if(singleton == null) {
            singleton = new CompactScheduler(userConfig, eventLog);
        }
        return singleton;
    }

    @Override
    public int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> taskManager, Calendar date) {
        PriorityQueue<Task> incomplete = new PriorityQueue<>();
        int numErrors = errorCount;
        while ((!taskManager.isEmpty() && day.hasSpareHours()) ||
                (!taskManager.isEmpty() && taskManager.peek().getDueDate().equals(day.getDate()))) {
            // gets first task from heap and finds max possible hours available
            Task task = taskManager.remove();
            int maxHours = getMaxHours(day, task, date);
            // status of task creation
            boolean validTaskStatus = day.addSubTaskManually(task, maxHours);
            // adds task to relevant completion heap
            if (task.getDueDate().equals(day.getDate()) || task.getSubTotalHoursRemaining() == 0) complete.add(task);
            else incomplete.add(task);
            // reports scheduling action
            eventLog.reportDayAction(day, task, validTaskStatus);
            // updates number of errors
            numErrors += validTaskStatus ? 0 : 1;

            if (!validTaskStatus && Time.differenceOfDays(task.getDueDate(), day.getDate()) > 0) break;
        }
        while (!incomplete.isEmpty()) {
            taskManager.add(incomplete.remove());
        }
        return numErrors;
    }

    private int getMaxHours(Day day, Task task, Calendar date) {
        // sets up the starting hour for the day based on the given time from 'date'
        int startingHour = date.get(Calendar.HOUR_OF_DAY);
        boolean sameDate = false;
        if (date.get(Calendar.DATE) == day.getDate().get(Calendar.DATE)
                && date.get(Calendar.MONTH) == day.getDate().get(Calendar.MONTH)
                && date.get(Calendar.YEAR) == day.getDate().get(Calendar.YEAR)) {
            startingHour = Math.max(userConfig.getRange()[0], startingHour);
            sameDate = true;
        } else {
            startingHour = userConfig.getRange()[0];
        }

        int maxHours = 0;
        if (task.getDueDate().equals(day.getDate())) {
            if (userConfig.isFitDay()) {
                int remainingHours = 24 - (startingHour + day.getHoursFilled());
                maxHours = Math.min(remainingHours, task.getSubTotalHoursRemaining());
            } else {
                maxHours = task.getSubTotalHoursRemaining();
            }
        } else if (sameDate) { // we need to deal with Scenarios C & D [DONE]
            int remainingHours = userConfig.getRange()[1] - (startingHour + day.getHoursFilled());
            if (remainingHours > 0) {
                maxHours = Math.min(remainingHours, task.getSubTotalHoursRemaining());
            }
        } else {
            maxHours = Math.min(day.getSpareHours(), task.getSubTotalHoursRemaining());
        }
        return maxHours;
    }

    @Override
    public boolean correctSchedule(List<Day> schedule, int errorCount) {
        return false;
    }
}
