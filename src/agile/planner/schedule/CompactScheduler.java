package agile.planner.schedule;

import agile.planner.schedule.day.Day;
import agile.planner.data.Task;
import agile.planner.user.UserConfig;

import java.util.PriorityQueue;

/**
 * Provides a more compact solution with generating a schedule
 *
 * @author Andrew Roe
 */
public class CompactScheduler implements Scheduler {

    /** Holds relevant data for user settings in scheduling */
    private final UserConfig userConfig;

    /**
     * Primary constructor for CompactScheduler
     *
     * @param userConfig user settings for scheduling purposes
     */
    public CompactScheduler(UserConfig userConfig) {
        this.userConfig = userConfig;
    }

    @Override
    public int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> incomplete, PriorityQueue<Task> taskManager) {
        int numErrors = errorCount;
        while(day.hasSpareHours() && taskManager.size() > 0) {
            Task t1 = taskManager.remove();
            if(t1.getDueDate().equals(day.getDate())) { //TODO this shouldn't be here (fill up day until it's full, or if there's more tasks due that day)
                int maxHours = userConfig.isFitSchedule() ? Math.min(day.getSpareHours(), t1.getSubTotalHoursRemaining()) : t1.getSubTotalHoursRemaining();
                boolean status = day.addSubTaskManually(t1, maxHours);
                complete.add(t1);
                numErrors += status ? 0 : 1;
                if(!day.hasSpareHours() && !userConfig.isFitSchedule()) {
                    while(taskManager.size() > 0 && taskManager.peek().getDueDate().equals(day.getDate())) {
                        Task dueTask = taskManager.remove();
                        complete.add(dueTask);
                        day.addSubTaskManually(dueTask, dueTask.getSubTotalHoursRemaining());
                        //eventLog.reportDayAction(day, dueTask, validTaskStatus); //TODO need to add back
                        numErrors++;
                    }
                }
            } else {
                int maxHours = Math.min(day.getSpareHours(), t1.getSubTotalHoursRemaining());
                day.addSubTaskManually(t1, maxHours);
                if(day.getSpareHours() > 0) {
                    incomplete.add(t1);
                } else {
                    complete.add(t1);
                }
            }
        }
        while(incomplete.size() > 0) {
            taskManager.add(incomplete.remove());
        }
        return numErrors;
    }
}
