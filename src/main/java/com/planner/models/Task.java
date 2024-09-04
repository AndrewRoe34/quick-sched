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
        this(id, name, hours, date);
        setCard(card);
    }

    /**
     * <p>Secondary constructor for Task.</p>
     * <p>Initializes Card to null</p>
     *
     * @param id ID of Task
     * @param name name of Task
     * @param hours number of hours for Task
     * @param date number of days till due date for Task
     */
    public Task(int id, String name, double hours, Calendar date)  {
        setId(id);
        setName(name);
        setTotalHours(hours);
        setDueDate(date);
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
     * @throws IllegalArgumentException when attempting to set the ID to be less than <code>0</code>
     */
    private void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Task ID cannot be less than 0.");
        }
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
     * @throws IllegalArgumentException when attempting to set the name to an empty string or <code>null</code>
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be null or empty.");
        }
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
     * @throws IllegalArgumentException when attempting to set the due date to <code>null</code>
     */
    public void setDueDate(Calendar dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("Task due date cannot be null.");
        }
        this.dueDate = dueDate;
    }

    /**
     * Gets the due date stamp
     *
     * @return due date stamp as a String
     */
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

    /**
     * Gets the card of the Task
     *
     * @return card of the Task
     */
    public Card getCard() {
        return card;
    }

    /**
     * Sets the card of the Task
     *
     * @param card card of the Task
     */
    public void setCard(Card card) {
        this.card = card;
    }

    /**
     * <p>Gets the tag of the Task</p>
     *
     * @return the name of the Task's card if it exists
     */
    public String getTag() {
        return card != null ? card.getName() : null;
    }

    /**
     * <p>Gets the color of the Task's card</p>
     * <p>Calls the <code>card.getColorId()</code> method to get the id of the card</p>
     *
     * @return color of the Task's card
     */
    public Card.Color getColor() {
        return card != null ? card.getColorId() : null;
    }


    /**
     * Adds a new SubTask contains a reference to the parent Task
     *
     * @param hours number of hours for SubTask
     * @param overflow boolean value for overflow status
     * @param timeStamp time interval in which the SubTask should be completed
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
     * Resets the Task in all of its properties, resets <code>subTotalHours</code> to zero
     */
    public void reset() {
        subTotalHours = 0;
    }

    /**
     * Gets the number of SubTask hour slots currently unfilled, calculated as the difference
     * between <code>totalHours</code> and <code>subTotalHours</code>
     *
     * @return unfilled SubTask hours
     */
    public double getSubTotalHoursRemaining() {
        return totalHours - subTotalHours;
    }

    /**
     * Compares two Task objects based on their due date
     *
     * @param anotherTask the Task to be compared.
     * @return <code>0</code> if there is no time difference between the due dates and the subtasks total hours are equal;
     * and <code>1</code> if the due date of this Task is after the argument Task or when the argument Task has more remaining subtasks hours;
     * and <code>-1</code> if the due date of this Task is before the argument Task or when the argument Task has less remaining subtasks hours
     */
    @Override
    public int compareTo(Task anotherTask) {
        long timeDiff = this.dueDate.compareTo(anotherTask.dueDate);
        if(timeDiff < 0 || timeDiff == 0 && this.getSubTotalHoursRemaining() > anotherTask.getSubTotalHoursRemaining()) {
            return -1;
        } else if(timeDiff > 0 || this.getSubTotalHoursRemaining() < anotherTask.getSubTotalHoursRemaining()) {
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
     * @throws IllegalArgumentException when attempting to set the Task hours to <code>0</code> or less
     * and when attempting to set the Task hours to numbers that have a decimal other than <code>0.5</code>
     */
    public void setTotalHours(double total) {
        if (total <= 0) throw new IllegalArgumentException("Task hours cannot be zero or negative");
        double dec = total % 1;
        if (dec != 0 && dec != 0.5) throw new IllegalArgumentException("Task hours cannot have a decimal besides 0.5");
        this.totalHours = total;
    }

    /**
     * Converts the Task object into a String format, includes the Task name and total hours
     *
     * @return Task object as String
     */
    @Override
    public String toString() {
        return "Task [name=" + name + ", total=" + totalHours + "]";
    }

    /**
     * Checks if two Task object are equal by comparing ID, total hours, SubTask total hours, name, and due date
     *
     * @param anotherTask another Task object
     * @return <code>true</code> if the two Task objects are the same or when the two Tasks have equal members;
     * and <code>false</code> when the other Task is null, of different type, or when the two Tasks have unequal members
     */
    @Override
    public boolean equals(Object anotherTask) {
        if (this == anotherTask) return true;
        if (anotherTask == null || getClass() != anotherTask.getClass()) return false;
        Task task = (Task) anotherTask;
        return id == task.id && totalHours == task.totalHours && subTotalHours == task.subTotalHours &&
                name.equals(task.name) && dueDate.equals(task.dueDate);
    }

    /**
     * returns the hash code of the Task members (id, name, due date, total hours, subtasks total hours)
     *
     * @return hash code of Task
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, dueDate, totalHours, subTotalHours);
    }

    /**
     * The individual component of a parent Task in the form of a SubTask
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
        /** Time interval in which the SubTask should be completed */
        private final Time.TimeStamp timeStamp;

        /**
         * Primary constructor for SubTask
         *
         * @param parentTask parent of the SubTask
         * @param hours number of hours for the SubTask
         * @param overflow boolean value for overflow status
         * @param timeStamp time interval of the SubTask
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

        /**
         * Gets time interval of SubTask
         *
         * @return time interval
         */
        public Time.TimeStamp getTimeStamp() {
            return timeStamp;
        }

        /**
         * Converts SubTask Object to String format, includes name, and hours
         *
         * @return SubTask as a String
         */
        @Override
        public String toString() {
            return "SubTask [name=" + parentTask.getName() + ", hours=" + hours + "]";
        }

        /**
         * Compares two SubTasks by comparing their parent Tasks
         *
         * @param anotherSubTask the object to be compared.
         * @return the result of comparing the parent Tasks
         */
        @Override
        public int compareTo(SubTask anotherSubTask) {
            return this.parentTask.compareTo(anotherSubTask.parentTask);
        }
    }
}
