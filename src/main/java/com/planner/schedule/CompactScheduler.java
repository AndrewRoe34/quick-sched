package com.planner.schedule;

import com.planner.models.Event;
import com.planner.schedule.day.Day;
import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.util.EventLog;
import com.planner.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The class {@code CompactScheduler} implements the interface {@link Scheduler} for compact scheduling actions.
 * This involves the utilization of Longest-Job-First in order to compute the schedule for the week. {@link UserConfig}
 * options allow for another style such as Shortest-Job-First well as other restraints for scheduling.
 * <p>
 * Day optimizations occur when {@link com.planner.models.Task.SubTask} can be more uniformly fitted around {@link com.planner.models.Event}.
 * However, the user must have 'optimizeDay' config option set to true via the {@link UserConfig}.
 *
 * @author Andrew Roe
 */
public class CompactScheduler implements Scheduler {

    /** Singleton for CompactScheduler */
    private static CompactScheduler singleton;
    /** Holds relevant data for user settings in scheduling */
    private UserConfig userConfig;
    /** EventLog for logging data on Day actions */
    private final EventLog eventLog;
    /** Boolean value for whether we are scheduling a Day that is in fact today */
    private boolean isToday;

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
        double maxHours = getMaxHours(day, date);

        PriorityQueue<Task> incomplete = new PriorityQueue<>();
        int numErrors = errorCount;

        while ((!taskManager.isEmpty() && day.hasSpareHours()) ||
                (!taskManager.isEmpty() && Time.doDatesMatch(taskManager.peek().getDueDate(), day.getDate()))) {
            // gets first task from heap and finds max possible hours available
            Task task = taskManager.remove();

            double hours = Math.min(task.getSubTotalHoursRemaining(), maxHours);
            maxHours -= hours;

            // status of task creation
            boolean validTaskStatus = day.addSubTask(task, hours, userConfig, date, isToday);

            if (userConfig.isOverflow()) {
                if (hours == 0) {
                    day.addOverflowError(task.getId(), task.getSubTotalHoursRemaining(), false);
                } else if (Time.doDatesMatch(task.getDueDate(), day.getDate()) && task.getSubTotalHoursRemaining() > 0) {
                    day.addOverflowError(task.getId(), task.getSubTotalHoursRemaining(), true);
                }
            }

            // adds task to relevant completion heap
            if (Time.doDatesMatch(task.getDueDate(), day.getDate()) || task.getSubTotalHoursRemaining() == 0) complete.add(task);
            else incomplete.add(task);
            // reports scheduling action
            eventLog.reportDayAction(day, task, validTaskStatus);
            // updates number of errors
            numErrors += validTaskStatus ? 0 : 1;

            if (!validTaskStatus && !Time.doDatesMatch(task.getDueDate(), day.getDate())
                    && Time.differenceOfDays(task.getDueDate(), day.getDate()) > 0) {
                break;
            }
        }
        while (!incomplete.isEmpty()) {
            taskManager.add(incomplete.remove());
        }
        return numErrors;
    }

    private double getMaxHours(Day day, Calendar date) {
        int startingHour = getStartingHour(day, date);

        if (!userConfig.isDefaultAtStart() && startingHour >= userConfig.getDailyHoursRange()[1]) {
            return 0.0;
        }

        Calendar time = (Calendar) date.clone();
        time.set(Calendar.HOUR_OF_DAY, startingHour);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);

        Calendar start = Time.getFirstAvailableTimeInDay(new ArrayList<>(), day.getEventTimeStamps(), userConfig, time, Time.doDatesMatch(time, date));
        Calendar end = (Calendar) time.clone();
        end.set(Calendar.HOUR_OF_DAY, userConfig.getDailyHoursRange()[1]);

        double hours = 0.0;
        for (Event e : day.getEventList()) {
            if (start.compareTo(e.getTimeStamp().getStart()) < 0) {
                if (e.getTimeStamp().getStart().compareTo(end) < 0) {
                    hours += Time.getTimeInterval(start, e.getTimeStamp().getStart());
                    start = e.getTimeStamp().getEnd();
                } else {
                    // compute time until end of day
                    hours += Time.getTimeInterval(start, end);
                    return hours;
                }
            } else if (start.compareTo(e.getTimeStamp().getEnd()) <= 0) {
                start = e.getTimeStamp().getEnd();
            }
        }

        hours += Time.getTimeInterval(start, end);

        return Math.min(hours, userConfig.getHoursPerDayOfWeek()[date.get(Calendar.DAY_OF_WEEK) - 1]);
    }

    private int getStartingHour(Day day, Calendar date) {
        int startingHour = date.get(Calendar.HOUR_OF_DAY);
        isToday = false;
        if (Time.doDatesMatch(date, day.getDate())) {
            startingHour = Math.max(userConfig.getDailyHoursRange()[0], startingHour);
            isToday = true;
        } else {
            startingHour = userConfig.getDailyHoursRange()[0];
        }
        return startingHour;
    }

    @Override
    public int optimizeDay(Day day) {
        List<Double> timeBlocks = Time.computeTimeBlocks(day);
        List<Task.SubTask> subTasks = day.getSubTaskList();
        // todo need to combine subTasks that were possibly broken up due to events
        //   also, if there are no events (and subTasks were not broken up), there is no further optimization
        //   while we could sort the subTasks, there is little need since the most we'll realistically have is 24 (tiny number, so bruteforce is acceptable)

        return 0;
    }

    @Override
    public void updateConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
    }
}
