package com.planner.schedule;

import com.planner.manager.ScheduleManager;
import com.planner.schedule.day.Day;
import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.util.EventLog;

import java.util.Calendar;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The interface {@code Scheduler} is for all scheduling implementations to sort out tasks via the {@link ScheduleManager}
 *
 * @author Andrew Roe
 */
public interface Scheduler {

    static Scheduler getInstance(UserConfig userConfig, EventLog eventLog, int idx) {
        if (idx == 0) return DynamicScheduler.getSingleton(userConfig, eventLog);
        return CompactScheduler.getSingleton(userConfig, eventLog);
    }

    /**
     * Assigns current {@link Day} a set of {@link Task.SubTask} via one of the specified scheduling algorithms
     *
     * @param day Day being processed
     * @param errorCount number of errors in current schedule
     * @param complete Tasks that are "finished scheduling" are added here
     * @param taskManager PriorityQueue of all Tasks in sorted order
     * @return number of errors in scheduling Day
     */
    int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> taskManager, Calendar date);

    /**
     * Optimizes a {@link Day}'s set of {@link com.planner.models.Task.SubTask} around {@link com.planner.models.Event}
     * by reducing the amount of interruptions in task blocks. This functions similar to how dynamic memory is fitted
     * to maximize spacing.
     * <p>
     * NOTE: This operation will only occur if the user has the 'optimizeDay' config option set to 'true'
     *
     * @param day day being optimized around {@link com.planner.models.Event}
     * @return number of times a {@link com.planner.models.Task.SubTask} was not uniformly fitted
     */
    int optimizeDay(Day day);

    void updateConfig(UserConfig userConfig);
}
