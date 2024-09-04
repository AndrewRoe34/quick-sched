package com.planner.models;

import java.util.Calendar;
import java.util.Objects;

import com.planner.util.Time;

/**
 * The core element of the underlying schedule. Possesses a name, a due date,
 * along with a total number of hours. It is responsible for the construction of
 * SubTasks.
 *
 * @author Andrew Roe
 */
public class Task implements Comparable<Task> {

    /** ID of the Task */
    private int id;
    /** Name of the Task */
    private String name;
    /** Due date of the Task */
    private Calendar dueDate;
    /** Total number of hours for the Task */
    private double totalHours;
    /** Number of SubTask hours */
    private double subTotalHours;
    /** Card of the task */
    private Card card;

    /**
     * Primary constructor for Task
     *
     * @param id ID of Task
     * @param name name of Task
     * @param hours number of hours for Task
     * @param date number of days till due date for Task
     * @param card reference to Card for tag and color
     */
    public Task(int id, String name, double hours, Calendar date, Card card) {
        setId(id);
        setName(name);
        setTotalHours(hours);
        setDueDate(date);
        setCard(card);
    }

    public Task(int id, String name, double hours, Calendar date)  {
        this(id, name, hours, date, null);
    }

    /**
     * Gets the ID for a Task
     *
     * @return Task ID
     */
    public int getId() { //TODO need to add id to every task
        return id;
    }

    /**
     * Sets the ID for a Task
     *
     * @param id ID for Task
     */
    private void setId(int id) { //TODO need exception for case where ID is negative
        this.id = id;
    }

    /**
     * Gets the name of the Task
     *
     * @return name of Task
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Task
     *
     * @param name name of Task
     */
    public void setName(String name) {	//TODO will need to include exceptions for the setters
        this.name = name;
    }

    /**
     * Gets the due date of the Task
     *
     * @return due date of Task
     */
    public Calendar getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date of the Task
     *
     * @param dueDate due date of Task
     */
    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }


    public String getDateStamp() {
        int day = dueDate.get(Calendar.DAY_OF_MONTH);
        int year = dueDate.get(Calendar.YEAR);

        // Calendar.MONTH is zero-indexed.
        int month = dueDate.get(Calendar.MONTH) + 1;

        StringBuilder dateStamp = new StringBuilder();
        if (day < 10) dateStamp.append("0");
        dateStamp.append(day);
        dateStamp.append("-");
        if (month < 10) dateStamp.append("0");
        dateStamp.append(month);
        dateStamp.append("-");
        dateStamp.append(year);

        return dateStamp.toString();
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getTag() {
        return card != null ? card.getName() : null;
    }

    public Card.Color getColor() {
        return card != null ? card.getColorId() : null;
    }

    /**
     * Adds a SubTask for a given Task
     *
     * @param hours number of hours
     * @param overflow overflow status of the SubTask
     * @return SubTask
     */
    public SubTask addSubTask(double hours, boolean overflow, Time.TimeStamp timeStamp) {
        SubTask subtask = null;
        if(hours > 0 && subTotalHours + hours <= totalHours) {
            subtask = new SubTask(this, hours, overflow, timeStamp);
            subTotalHours += hours;
        }
        return subtask;
    }

    /**
     * Resets the Task in all of its properties
     */
    public void reset() {
        subTotalHours = 0;
    }

    /**
     * Gets the number of SubTask hour slots currently unfilled
     *
     * @return number of unfilled hours for SubTasks
     */
    public double getSubTotalHoursRemaining() {
        return totalHours - subTotalHours;
    }

    @Override
    public int compareTo(Task o) {
        long timeDiff = this.dueDate.compareTo(o.dueDate);
        if(timeDiff < 0 || timeDiff == 0 && this.getSubTotalHoursRemaining() > o.getSubTotalHoursRemaining()) {
            return -1;
        } else if(timeDiff > 0 || this.getSubTotalHoursRemaining() < o.getSubTotalHoursRemaining()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Gets the total number of hours for the Task
     *
     * @return number of hours for the Task
     */
    public double getTotalHours() {
        return totalHours;
    }

    /**
     * Sets the total number of hours for the Task
     *
     * @param total number of hours for the Task
     */
    public void setTotalHours(double total) {
        if (total <= 0) throw new IllegalArgumentException("Task hours cannot be zero or negative");
        double dec = total % 1;
        if (dec != 0 && dec != 0.5) throw new IllegalArgumentException("Task hours cannot have a decimal besides 0.5");
        this.totalHours = total;
    }

    @Override
    public String toString() {
        return "Task [name=" + name + ", total=" + totalHours + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && totalHours == task.totalHours && subTotalHours == task.subTotalHours &&
                name.equals(task.name) && dueDate.equals(task.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dueDate, totalHours, subTotalHours);
    }

    /**
     * The individual component of a parent Task in the form of a SubTask
     * <p>
     * TODO will need to include a markComplete() method
     *
     * @author Andrew Roe
     */
    public static class SubTask implements Comparable<SubTask> {

        /** Parent Task of the SubTask */
        private final Task parentTask;
        /** Number of hours for the SubTask */
        private final double hours;
        /** Number of overflow hours due to scheduling */
        private final boolean overflowStatus;
        private final Time.TimeStamp timeStamp;

        /**
         * Primary constructor for SubTask
         *
         * @param parentTask parent of the SubTask
         * @param hours number of hours for the SubTask
         * @param overflow boolean value for overflow status
         */
        private SubTask(Task parentTask, double hours, boolean overflow, Time.TimeStamp timeStamp) {
            this.parentTask = parentTask;
            this.hours = hours;
            this.overflowStatus = overflow;
            this.timeStamp = timeStamp;
        }

        /**
         * Gets the parent of the SubTask
         *
         * @return Task parent of SubTask
         */
        public Task getParentTask() {
            return parentTask;
        }

        /**
         * Gets the number of SubTask hours
         *
         * @return number of SubTask hours
         */
        public double getSubTaskHours() {
            return hours;
        }

        /**
         * Determines whether SubTask overflowed
         *
         * @return overflow status
         */
        public boolean isOverflow() {
            return overflowStatus;
        }

        public Time.TimeStamp getTimeStamp() {
            return timeStamp;
        }

        @Override
        public String toString() {
            return "SubTask [name=" + parentTask.getName() + ", hours=" + hours + "]";
        }

        @Override
        public int compareTo(SubTask o) {
            return this.parentTask.compareTo(o.parentTask);
        }
    }
}
