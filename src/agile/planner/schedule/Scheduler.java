package agile.planner.schedule;

import agile.planner.schedule.day.Day;
import agile.planner.task.Task;

import java.util.PriorityQueue;

/**
 * Interface for all scheduling implementations to sort out tasks in the planner
 *
 * @author Andrew Roe
 */
public interface Scheduler {

    /**
     * Assigns current day a set of SubTasks
     *
     * @param day Day being processed
     * @param errorCount number of errors in current schedule
     * @param complete Tasks that are "finished scheduling" are added here
     * @param incomplete Tasks that are incomplete and need to be scheduled for later days
     * @param taskManager PriorityQueue of all Tasks in sorted order
     * @return number of errors in scheduling Day
     */
    int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> incomplete, PriorityQueue<Task> taskManager);
}
