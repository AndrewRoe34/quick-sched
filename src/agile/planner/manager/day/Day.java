package agile.planner.manager.day;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import agile.planner.task.Task;
import agile.planner.task.Task.SubTask;
import agile.planner.util.Time;

/**
 * Represents a single Day in the year
 *
 * @author Andrew Roe
 */
public class Day {

    /** Holds the date and time of the particular Day */
    private Calendar date;
    /** Number of hours possible for a given Day */
    private int capacity;
    /** Number of hours filled for a given Day */
    private int size;
    /** TreeSet of all SubTasks */
    private LinkedList<SubTask> subtaskManager;

    /**
     * Primary constructor for Day
     *
     * @param capacity total capacity for the day
     * @param incrementation number of days from present day (0=today, 1=tomorrow, ...)
     */
    public Day(int capacity, int incrementation) {
        setCapacity(capacity);
        setDate(incrementation);
        subtaskManager = new LinkedList<>();
    }

    /**
     * Sets the capacity for the Day
     *
     * @param capacity total possible hours
     */
    private void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Sets the Date for the Day
     *
     * @param incrementation number of days from present for the Date to be set
     */
    private void setDate(int incrementation) {
        this.date = Time.getFormattedCalendarInstance(incrementation);
    }

    /**
     * Gets the Date from the Day
     *
     * @return Date from Day
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * Adds a Task to the Day
     *
     * @param task task to be added in the form of a SubTask
     * @return whether task was inserted without overflow
     */
    public boolean addSubTask(Task task) {
        if(this.date.equals(task.getDueDate())) {
            boolean overflow = task.getSubTotalHoursRemaining() > getSpareHours();
            int hours = task.getSubTotalHoursRemaining();
            SubTask subtask = task.addSubTask(hours, overflow);
            subtaskManager.addLast(subtask);
            this.size += hours;
            return !overflow;
        }
        if(task.getAverageNumHours() == 0) {
            task.setAverageNumHours(this.date);
        }
        int hours = task.getAverageNumHours();
        //Handles the case where we actually have less hours available due to scheduling
        hours = hours > task.getSubTotalHoursRemaining() ? task.getSubTotalHoursRemaining() : hours;
        //Fixes the number of hours according to what the Day has available
        hours = hours + size > capacity ? capacity - size : hours;

        SubTask subtask = task.addSubTask(hours, false);

        subtaskManager.addLast(subtask);
        this.size += hours;

        return true;
    }

    /**
     * Gets the parent task based on the specified subtask index value
     *
     * @param subtaskIndex index of the subtask
     * @return parent of subtask
     */
    public Task getParentTask(int subtaskIndex) {
        SubTask subtask = subtaskManager.get(subtaskIndex);
        return subtask.getParentTask();
    }

    /**
     * Adds a SubTask manually to the Day
     *
     * @param task Task to be added
     * @param hours number of hours for the SubTask
     */
    public void addSubTaskManually(Task task, int hours) {
        //TODO will be utilized for the schedule parsing IO
    }

    /**
     * Gets the number of SubTasks possessed by the Day
     *
     * @return number of SubTasks possessed by the Day
     */
    public int getNumSubTasks() {
        return subtaskManager.size();
    }

    /**
     * Gets spare hours from the Day
     *
     * @return number of free hours available for scheduling
     */
    public int getSpareHours() {
        return capacity - size;
    }

    /**
     * Determines whether there are spare hours in the Day
     *
     * @return boolean value for opening in Day
     */
    public boolean hasSpareHours() {
        return getSpareHours() > 0;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        StringBuilder sb = new StringBuilder(sdf.format(this.date.getTime()) + "\n");
        int count = 1;
        for(SubTask st : subtaskManager) {
            sb.append(count + ". ");
            count++;
            sb.append(st.getParentTask().getName() + ", ");
            sb.append(st.getSubTaskHours() + "hr, Due ");
            sb.append(sdf.format(st.getParentTask().getDueDate().getTime()));
            if(st.getOverflowStatus()) {
                sb.append(" OVERFLOW");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}