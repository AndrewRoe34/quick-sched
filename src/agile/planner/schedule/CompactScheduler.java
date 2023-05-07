package agile.planner.schedule;

import agile.planner.schedule.day.Day;
import agile.planner.data.Task;
import agile.planner.user.UserConfig;
import agile.planner.util.EventLog;

import java.util.PriorityQueue;

/**
 * Provides a more compact solution with generating a schedule
 *
 * @author Andrew Roe
 */
public class CompactScheduler implements Scheduler {

    /** Holds relevant data for user settings in scheduling */
    private final UserConfig userConfig;
    /** EventLog for logging data on Day actions */
    private EventLog eventLog;

    /**
     * Primary constructor for CompactScheduler
     *
     * @param userConfig user settings for scheduling purposes
     * @param eventLog EventLog for logging data on Day actions
     */
    public CompactScheduler(UserConfig userConfig, EventLog eventLog) {
        this.userConfig = userConfig;
        this.eventLog = eventLog;

    }

    @Override
    public int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> incomplete, PriorityQueue<Task> taskManager) {
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
}
