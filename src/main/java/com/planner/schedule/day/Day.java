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
 * @author Abah Olotuche Gabriel
 */
public class Day {

    /** Holds the date and time of the particular Day */
    private Calendar date;
    /** Number of hours possible for a given Day */
    private double capacity;
    /** Number of hours filled for a given Day */
    private double size;
    /** TreeSet of all SubTasks */
    private final List<SubTask> subTaskList;
    /** List of time stamps for all subtasks */
    private final List<TimeStamp> taskTimeStamps;
    /** Map by starting hour and the associated event */
    private final List<Event> eventList;

    private final List<TimeStamp> eventTimeStamps;
    /** ID for the specific Day */
    private int id;

    /**
     * Constructor for Day that utilizes an incrementation value
     *
     * @param id ID specifier for Day
     * @param capacity total capacity for the day
     * @param incrementation number of days from present day (0=today, 1=tomorrow, ...)
     */
    public Day(int id, double capacity, int incrementation) {
        setId(id);
        setCapacity(capacity);
        setDate(incrementation);
        subTaskList = new ArrayList<>();
        taskTimeStamps = new ArrayList<>();
        eventList = new ArrayList<>();
        eventTimeStamps = new ArrayList<>();
    }

    /**
     * Constructor for Day that utilizes a specified Calendar date
     *
     * @param id ID specifier for Day
     * @param capacity total capacity for Day
     * @param date date on which this Day occurs
     */
    public Day(int id, double capacity, Calendar date) {
        setId(id);
        setCapacity(capacity);
        this.date = date;
        subTaskList = new ArrayList<>();
        taskTimeStamps = new ArrayList<>();
        eventList = new ArrayList<>();
        eventTimeStamps = new ArrayList<>();
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

    /**
     * Gets the Date from the Day
     *
     * @return Date from Day
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * Number of hours filled for a given Day
     *
     * @return number of hours filled for Day
     */
    public double getSize() {
        return size;
    }

    /**
     * Adds a pre-formatted SubTask to the Day
     *
     * @param task  Task to be added
     * @param hours number of hours for the SubTask
     * @param overflow boolean status for success of adding SubTask
     */
    public void addFormattedSubTask(Task task, double hours, boolean overflow) {
        Task.SubTask subtask = task.addSubTask(hours, overflow, null);
        subTaskList.add(subtask);
        this.size += hours;
    }

    /**
     * Adds a SubTask manually to the Day
     *
     * @param task  Task to be added
     * @param hours number of hours for the SubTask
     * @return boolean status for success of adding SubTask manually
     */
    public boolean addPlainSubTask(Task task, double hours, UserConfig userConfig, Calendar time, boolean isToday) {
        if (hours <= 0) return false;
        boolean overflow = this.size + hours > this.capacity;
//        SubTask subtask = task.addSubTask(hours, overflow); // todo need to rearrange this here

//        subtaskManager.add(subtask);
//        this.size += hours;

        // nothing changes here (thank God)
        if (eventList.isEmpty()) {
            createNonEventTimeStamps(hours, userConfig, time, isToday);
            SubTask subTask = task.addSubTask(hours, overflow, taskTimeStamps.get(taskTimeStamps.size() - 1));
            subTaskList.add(subTask);
            this.size += hours;
        } else {
            while (hours > 0) {
                double prevHours = hours;
                hours -= createEventTimeStamps(hours, userConfig, time, isToday);
                // add subtask to subtaskManager
                SubTask subTask = task.addSubTask(prevHours - hours, overflow, taskTimeStamps.get(taskTimeStamps.size() - 1));
                subTaskList.add(subTask);
                this.size += (prevHours - hours);
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
        // todo we'll handle event edge case here (simply check whether it's within the bounds for the day)
        //  avoid 'clever' solutions
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
        Calendar temp;
        if (isToday) {
            temp = time;
        } else {
            temp = this.date;
        }
        Calendar startTime = Time.getFirstAvailableTimeInDay(taskTimeStamps, eventTimeStamps, userConfig, temp, isToday);
        // todo check whether startTime is within valid bounds (if it is, cap it at: Min(hours_remaining, hours_till_end_of_day) )
        //  once this is done (as well as handling 'eventTimeStamp'), return '0' (note: make sure to check whether 'fit_day' is true )

        TimeStamp eventTimeStamp = null;
        for (TimeStamp eTS : eventTimeStamps) { // this list needs to be sorted (given assumption below)
            if (Time.isBefore(startTime, eTS.getStart())) {
                eventTimeStamp = eTS;
                break;
            }
        }

        if (eventTimeStamp != null) {
            hours = Math.min(hours, Time.getTimeInterval(startTime, eventTimeStamp.getStart()));
        }

        int taskHours = (int) hours;
        int taskMin = hours % 1 == 0.5 ? 30 : 0;

        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, taskHours);
        endTime.add(Calendar.MINUTE, taskMin);

        taskTimeStamps.add(new TimeStamp(startTime, endTime));
        return hours;
    }

    /**
     * Gets a particular SubTask from the Day's list
     *
     * @param subtaskIndex index for SubTask
     * @return specified SubTask
     */
    public SubTask getSubTask(int subtaskIndex) {
        return subTaskList.get(subtaskIndex);
    }

    /**
     * Gets the number of SubTasks possessed by the Day
     *
     * @return number of SubTasks possessed by the Day
     */
    public int getNumSubTasks() {
        return subTaskList.size();
    }

    /**
     * Adds an Event to the Day
     *
     * @param event Event being added
     * @return boolean status for success of adding Event
     */
    public boolean addEvent(Event event) {
        if (event.isRecurring()) { // this fixes issue for recurring events since they can happen on any day
            Calendar start = (Calendar) date.clone();
            start.set(Calendar.HOUR_OF_DAY, event.getTimeStamp().getStartHour());
            start.set(Calendar.MINUTE, event.getTimeStamp().getStartMin());
            Calendar end = (Calendar) date.clone();
            end.set(Calendar.HOUR_OF_DAY, event.getTimeStamp().getEndHour());
            end.set(Calendar.MINUTE, event.getTimeStamp().getEndMin());
            event = new Event(event.getId(), event.getName(), event.getColor(), new TimeStamp(start, end), event.getDays());
        }
        int idx = 0;
        boolean idxFound = false;
        for (Event e1 : eventList) {
            if (Time.isConflictingEvent(event, e1))
                return false;
            else {
                if (Time.isBefore(event.getTimeStamp().getStart(), e1.getTimeStamp().getStart())) {
                    idxFound = true;
                    break; // this prevents the search from incrementing idx past its correct spot
                } else {
                    idx++;
                }
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

    /**
     * Gets a particular Event from the Day's list
     *
     * @param eventIdx index for Event
     * @return specified Event
     */
    public Event getEvent(int eventIdx) {
        return eventList.get(eventIdx);
    }

    /**
     * Gets the number of Events for the given Day
     *
     * @return number of Events
     */
    public int getNumEvents() {
        return eventList.size();
    }

    /**
     * Determines whether there are spare hours in the Day
     *
     * @return boolean value for opening in Day
     */
    public boolean hasSpareHours() {
        return getSpareHours() > 0;
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
     * Gets the SubTask list from Day
     *
     * @return SubTask list
     */
    public List<SubTask> getSubTaskList() { // todo need to delete for refactoring purposes
        return subTaskList;
    }

    /**
     * Gets the Event list from Day
     *
     * @return Event list
     */
    public List<Event> getEventList() {
        return eventList;
    }

    /**
     * Gets the SubTask timestamps
     *
     * @return SubTask timestamps
     */
    public List<TimeStamp> getTaskTimeStamps() {
        return taskTimeStamps;
    }

    /**
     * Gets the Event timestamps
     *
     * @return Event timestamps
     */
    public List<TimeStamp> getEventTimeStamps() {
        return eventTimeStamps;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return "Day [" + sdf.format(this.date.getTime()) + "]";
    }
}