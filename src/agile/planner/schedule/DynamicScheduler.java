package agile.planner.schedule;

import agile.planner.schedule.day.Day;
import agile.planner.data.Task;

import java.util.PriorityQueue;

/**
 * Provides a more dynamic solution with generating a schedule
 *
 * @author Andrew Roe
 */
public class DynamicScheduler implements Scheduler {

    @Override
    public int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> incomplete, PriorityQueue<Task> taskManager) {
        int numErrors = errorCount;
        while(day.hasSpareHours() && taskManager.size() > 0) {
            Task task = taskManager.remove();
            boolean validTaskStatus = day.addSubTask(task);
            if(task.getSubTotalHoursRemaining() > 0) {
                //eventLog.reportDayAction(day, task, validTaskStatus); TODO need to add back
                incomplete.add(task);
            } else {
                complete.add(task);
                //eventLog.reportDayAction(day, task, validTaskStatus); //TODO need to add back
                numErrors += validTaskStatus ? 0 : 1;
                if(!day.hasSpareHours()) {
                    while(taskManager.size() > 0 && taskManager.peek().getDueDate().equals(day.getDate())) {
                        Task dueTask = taskManager.remove();
                        complete.add(dueTask);
                        day.addSubTask(dueTask);
                        //eventLog.reportDayAction(day, dueTask, validTaskStatus); //TODO need to add back
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
