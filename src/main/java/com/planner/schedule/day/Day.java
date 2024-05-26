package com.planner.schedule.day;

import java.text.SimpleDateFormat;
import java.util.*;

import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.models.Task.SubTask;
import com.planner.models.UserConfig;
import com.planner.util.Time;

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
    private final List<TimeStamp> timeStamps;
    /** Map by starting hour and the associated event */
    private final List<Event> eventList;
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
        timeStamps = new ArrayList<>();
        eventList = new ArrayList<>();
    }

    public Day(int id, double capacity, Calendar date) {
        setId(id);
        setCapacity(capacity);
        this.date = date;
        subtaskManager = new ArrayList<>();
        timeStamps = new ArrayList<>();
        eventList = new ArrayList<>();
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
        SubTask subtask = task.addSubTask(hours, overflow);

        subtaskManager.add(subtask);
        this.size += hours;

        // nothing changes here (thank God)
        if (eventList.isEmpty()) {
            createTimeStamp(hours, userConfig, time, isToday);
            return this.size <= this.capacity;
        }

        // todo this right here is wrong (we need to check the last timestamp in the list for subtasks to figure this out)
        //   and also, retrieving the first event doesn't make sense either (we need to properly vet here)
        //   however, once we fix these, we can use the 'createTimeStamp' method with full force like the while loop below
        //   after this, we can then work on the optimize day method to allocate tasks according to their length
        //  this shit is a real pain in the ass
        double capableHours = Time.getTimeUntilEvent(Time.getNearestQuarterOfHour(time, true), eventList.get(0).getTimeStamp());
        if (hours <= capableHours) {
            createTimeStamp(hours, userConfig, time, isToday);
        } else {
            while (hours > 0) {
                createTimeStamp(capableHours, userConfig, time, isToday);
                hours -= capableHours;
                capableHours = Time.getTimeUntilEvent(Time.getNearestQuarterOfHour(time, true), eventList.get(0).getTimeStamp());
            }
        }
        // todo might need to change this tbh
        return this.size <= this.capacity;
    }

    private void createTimeStamp(double hours, UserConfig userConfig, Calendar time, boolean isToday) {
        // handles the creation of timestamps for subtasks created
        int taskHours = (int) hours;
        int taskMin = hours % 1 == 0.5 ? 30 : 0;
        int endHr = 0;
        int endMin = 0;
        if (isToday && time.get(Calendar.HOUR_OF_DAY) >= userConfig.getRange()[0] && timeStamps.isEmpty() && !userConfig.isDefaultAtStart()) {
            Calendar startTime = Time.getNearestQuarterOfHour(time, true);
            endMin = startTime.get(Calendar.MINUTE) + taskMin;
            if (endMin >= 60) {
                endHr++;
                endMin %= 60;
            }
            endHr = (endHr + startTime.get(Calendar.HOUR_OF_DAY) + taskHours) % 24;

            timeStamps.add(new TimeStamp(startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), endHr, endMin));
        } else if (timeStamps.isEmpty()) {
            timeStamps.add(new TimeStamp(userConfig.getRange()[0], 0, (userConfig.getRange()[0] + taskHours) % 24, taskMin));
        } else {
            TimeStamp ts = timeStamps.get(timeStamps.size() - 1);
            endMin = ts.getEndMin() + taskMin;
            if (endMin >= 60) {
                endHr++;
                endMin %= 60;
            }
            endHr = (endHr + ts.getEndHour() + taskHours) % 24;

            timeStamps.add(new TimeStamp(ts.getEndHour(), ts.getEndMin(), endHr, endMin));
        }
    }

    public boolean addEvent(Event event) {
        // todo check that event does not overlap with a preexisting event
        //   will need to do a quick interval check and then insert it into Day (will need to then check events for the day when scheduling)
        return false;
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
    public double getSpareHours() {  // todo need to overhaul due to Events and their spacing relative to other items
        return Math.max(capacity - size, 0);
    }

    /**
     * Gets the number of hours assigned for a given day
     *
     * @return number of hours assigned for day
     */
    public double getHoursFilled() { // todo need to overhaul due to Events and their spacing relative to other items
        return this.size;
    }

    /**
     * Determines whether there are spare hours in the Day
     *
     * @return boolean value for opening in Day
     */
    public boolean hasSpareHours() { // todo need to overhaul due to Events and their spacing relative to other items
        return getSpareHours() > 0;
    }

    public List<TimeStamp> getTimeStamps() {
        return timeStamps;
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
    public Iterable<? extends SubTask> getSubTasks() {
        return subtaskManager;
    }

    public List<SubTask> getSubtaskManager() {
        return subtaskManager;
    }

    /**
     * Manages the creation of a time interval along with properly formatting its display.
     * <p>
     * Example: 12:30pm-4:15pm
     *
     * @author Andrew Roe
     */
    public static class TimeStamp {
        private final int startHour;
        private final int startMin;
        private final int endHour;
        private final int endMin;
        private String strStamp;

        public TimeStamp(int startHour, int startMin, int endHour, int endMin) {
            this.startHour = startHour;
            this.startMin = startMin;
            this.endHour = endHour;
            this.endMin = endMin;
            buildStamp();
        }

        public int getStartHour() {
            return startHour;
        }

        public int getStartMin() {
            return startMin;
        }

        public int getEndHour() {
            return endHour;
        }

        public int getEndMin() {
            return endMin;
        }
        
        private void buildStamp() {
            StringBuilder sb = new StringBuilder();

            int _startHour = startHour - 12;
            if (startHour <= 9 || startHour >= 13 && startHour <= 21) sb.append("0");
            if (startHour <= 12) sb.append(startHour);
            if (startHour > 12) sb.append(_startHour);
            sb.append(":");
            if (startMin < 10) sb.append("0");
            sb.append(startMin);
            if (startHour < 12) sb.append("am");
            else sb.append("pm");

            sb.append("-");

            int _endHour = endHour - 12;
            if (endHour <= 9 || endHour >= 13 && endHour <= 21) sb.append("0");
            if (endHour <= 12) sb.append(endHour);
            if (endHour > 12) sb.append(_endHour);
            sb.append(":");
            if (endMin < 10) sb.append("0");
            sb.append(endMin);
            if (endHour < 12) sb.append("am");
            else sb.append("pm");

            strStamp = sb.toString();
        }

        @Override
        public String toString() {
            return strStamp;
        }
    }
}