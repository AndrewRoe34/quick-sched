package agile.planner.schedule;

import agile.planner.schedule.day.Day;
import agile.planner.task.Task;

import java.util.PriorityQueue;

/**
 * Provides a more compact solution with generating a schedule
 *
 * @author Andrew Roe
 */
public class CompactScheduler implements Scheduler {

    @Override
    public int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> incomplete, PriorityQueue<Task> taskManager) {
        return 0;
    }
}
