package agile.planner.schedule;

import agile.planner.schedule.day.Day;
import agile.planner.data.Task;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Interface for all scheduling implementations to sort out tasks in the planner
 *
 * @author Andrew Roe
 */
public interface Scheduler {

    /**
     * Assigns current day a set of SubTasks via one of the specified scheduling algorithms
     *
     * @param day Day being processed
     * @param errorCount number of errors in current schedule
     * @param complete Tasks that are "finished scheduling" are added here
     * @param taskManager PriorityQueue of all Tasks in sorted order
     * @return number of errors in scheduling Day
     */
    int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> taskManager);

    /**
     * Corrects schedule by eliminating all overflows via adjusting number of hours per day
     * NOTE: This assumes that past days are stored in an archived list (thus, the first index
     * of schedule is for today)
     *
     * @param schedule list of days scheduled
     * @param errorCount number of errors currently in schedule
     * @return boolean value if successful (e.g. would be false if there are more than 24 hours assigned for a task due today)
     */
    boolean correctSchedule(List<Day> schedule, int errorCount);
}
