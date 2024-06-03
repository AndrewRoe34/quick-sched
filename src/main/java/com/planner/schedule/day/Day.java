package com.planner.schedule.day;

import java.text.SimpleDateFormat;
import java.util.*;

import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.models.Task.SubTask;
import com.planner.models.UserConfig;
import com.planner.util.Time;
import com.planner.util.Time.TimeStamp;

/**
 * Represents a single Day in the year
 *
 * @author Andrew Roe
 */
public class Day {

    /** Holds the date and time of the particular Day */
    private Calendar date;
    /** Number of hours possible for a given Day */
    private double capacity;
    /** Number of hours filled for a given Day */
    private double size;
    /** TreeSet of all SubTasks */
    private final List<SubTask> subtaskManager;
    /** List of time stamps for all subtasks */
    private final List<TimeStamp> taskTimeStamps;
    /** Map by starting hour and the associated event */
    private final List<Event> eventList;

    private final List<TimeStamp> eventTimeStamps;
    /** ID for the specific Day */
    private int id;

    /**
     * Primary constructor for Day
     *
     * @param id ID specifier for Day
     * @param capacity total capacity for the day
     * @param incrementation number of days from present day (0=today, 1=tomorrow, ...)
     */
    public Day(int id, double capacity, int incrementation) {
        setId(id);
        setCapacity(capacity);
        setDate(incrementation);
        subtaskManager = new ArrayList<>();
        taskTimeStamps = new ArrayList<>();
        eventList = new ArrayList<>();
        eventTimeStamps = new ArrayList<>();
    }

    public Day(int id, double capacity, Calendar date) {
        setId(id);
        setCapacity(capacity);
        this.date = date;
        subtaskManager = new ArrayList<>();
        taskTimeStamps = new ArrayList<>();
        eventList = new ArrayList<>();
        eventTimeStamps = new ArrayList<>();
    }

    /**
     * Sets the capacity for the Day
     *
     * @param capacity total possible hours
     */
    private void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the total capacity for the Day
     *
     * @return total capacity for Day
     */
    public double getCapacity() { return this.capacity; }

    /**
     * Sets the due date for the Day
     *
     * @param incrementation number of days from present for the Date to be set
     */
    private void setDate(int incrementation) {
        this.date = Time.getFormattedCalendarInstance(incrementation);
    }

    public double getSize() {
        return size;
    }

    /**
     * Gets the Date from the Day
     *
     * @return Date from Day
     */
    public Calendar getDate() {
        return date;
    }

    public void addSubTask(Task task, double hours, boolean overflow) {
        Task.SubTask subtask = task.addSubTask(hours, overflow);
        subtaskManager.add(subtask);
        this.size += hours;
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
     * @param task  Task to be added
     * @param hours number of hours for the SubTask
     * @return boolean status for success of adding SubTask manually
     */
    public boolean addSubTaskManually(Task task, double hours, UserConfig userConfig, Calendar time, boolean isToday) {
        if (hours <= 0) return false;
        boolean overflow = this.size + hours > this.capacity;
        SubTask subtask = task.addSubTask(hours, overflow); // todo need to rearrange this here

        subtaskManager.add(subtask);
        this.size += hours;

        // todo will end up using the while loop code below since we'll be merging the two methods together
        // nothing changes here (thank God)
        if (eventList.isEmpty()) {
            createNonEventTimeStamps(hours, userConfig, time, isToday);
            // add subtask to subtaskManager
        } else {
            while (hours > 0) {
                hours -= createEventTimeStamps(hours, userConfig, time, isToday);
                // add subtask to subtaskManager
            }
        }

        return this.size <= this.capacity;
    }

    private void createNonEventTimeStamps(double hours, UserConfig userConfig, Calendar time, boolean isToday) {
        // handles the creation of timestamps for subtasks created
        int taskHours = (int) hours;
        int taskMin = hours % 1 == 0.5 ? 30 : 0;
        Calendar temp;
        if (isToday) {
            temp = time;
        } else {
            temp = this.date;
        }
        Calendar startTime = Time.getFirstAvailableTimeInDay(taskTimeStamps, eventTimeStamps, userConfig, temp, isToday);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, taskHours);
        endTime.add(Calendar.MINUTE, taskMin);

        taskTimeStamps.add(new TimeStamp(startTime, endTime));
    }

    // [COMPLETE]
    private double createEventTimeStamps(double hours, UserConfig userConfig, Calendar time, boolean isToday) {
        /*
        Steps:
        1. Call getFirstAvailableTime(...) to determine first available (and usable) timeslot (Note: usable means > 30 min)
        2. Call getTimeUntilEvent(...) to determine how much time is available
            a. If enough for all, add the task
            b. If not enough for all but some, add the task and return how many hours/min are remaining
            c. If not enough at all, return 0 (return value represent how much time the task was just scheduled)
            d. Repeat until task is completed (remember, Day needs a method that tells the scheduler how many hours are AVAILABLE, so we can assume we have enough)
        3. Done
         */
        Calendar startTime = Time.getFirstAvailableTimeInDay(taskTimeStamps, eventTimeStamps, userConfig, time, isToday);
        TimeStamp eventTimeStamp = null;
        for (TimeStamp eTS : eventTimeStamps) { // this list needs to be sorted (given assumption below)
            if (Time.isBeforeEvent(startTime, eTS.getStart())) {
                eventTimeStamp = eTS;
                break;
            }
        }

        if (eventTimeStamp != null) {
            hours = Time.getTimeInterval(startTime, eventTimeStamp.getStart());
        }

        int taskHours = (int) hours;
        int taskMin = hours % 1 == 0.5 ? 30 : 0;

        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, taskHours);
        endTime.add(Calendar.MINUTE, taskMin);

        taskTimeStamps.add(new TimeStamp(startTime, endTime));
        return hours;
    }

    public boolean addEvent(Event event) {
        int idx = 0;
        boolean idxFound = false;

        for (Event e1 : eventList) {
            if (Time.isConflictingEvent(event, e1))
                return false;
            else {
                if (Time.isBeforeEvent(event.getTimeStamp().getStart(), e1.getTimeStamp().getStart()))
                    idxFound = true;
                else
                    idx++;
            }
        }

        if (idxFound) {
            eventList.add(idx, event);
            eventTimeStamps.add(idx, event.getTimeStamp());
        } else {
            eventList.add(event);
            eventTimeStamps.add(event.getTimeStamp());
        }

        // todo will need to add config option here that tallies event hours just the same as tasks (used with counting hours left in day)

        return true;
    }

    public SubTask getSubTask(int subtaskIndex) {
        return subtaskManager.get(subtaskIndex);
    }

    /**
     * Gets the number of SubTasks possessed by the Day
     *
     * @return number of SubTasks possessed by the Day
     */
    public int getNumSubTasks() {
        return subtaskManager.size();
    }

    private void setId(int id) {
        this.id = id;
    }

    /**
     * Gets ID for Day
     *
     * @return ID for Day
     */
    public int getId() {
        return id;
    }

    /**
     * Gets spare hours from the Day
     *
     * @return number of free hours available for scheduling
     */
    public double getSpareHours() {
        return Math.max(capacity - size, 0);
    }

    /**
     * Gets the number of hours assigned for a given day
     *
     * @return number of hours assigned for day
     */
    public double getHoursFilled() {
        return this.size;
    }

    /**
     * Determines whether there are spare hours in the Day
     *
     * @return boolean value for opening in Day
     */
    public boolean hasSpareHours() {
        return getSpareHours() > 0;
    }

    public List<TimeStamp> getTaskTimeStamps() {
        return taskTimeStamps;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return "Day [" + sdf.format(this.date.getTime()) + "]";
    }

    /**
     * Provides a formatted string for output purposes
     *
     * @return formatted String
     */
    public String formattedString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        StringBuilder sb = new StringBuilder(sdf.format(this.date.getTime()) + "\n");
        for(SubTask st : subtaskManager) {
            sb.append("-");
            sb.append(st.getParentTask().getName()).append(", ");
            sb.append(st.getSubTaskHours()).append("hr, Due ");
            sb.append(sdf.format(st.getParentTask().getDueDate().getTime()));
            if(st.isOverflow()) {
                sb.append(" OVERFLOW");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Gets the List of SubTasks for the given Day
     *
     * @return List of SubTasks
     */
    public Iterable<? extends SubTask> getSubTasks() { // todo need to replace with getSubTasks() --> List<SubTask>
        return subtaskManager;
    }

    public List<SubTask> getSubtaskManager() { // todo need to delete for refactoring purposes
        return subtaskManager;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public List<TimeStamp> getEventTimeStamps() {
        return eventTimeStamps;
    }
}