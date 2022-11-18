package agile.planner.task;

import java.util.Calendar;

import agile.planner.util.Time;

/**
 * The core element of the underlying schedule. Possesses a name, a due date,
 * along with a total number of hours. It is responsible for the construction of
 * SubTasks.
 *
 * @author Andrew Roe
 */
public class Task implements Comparable<Task> {

    /** Name of the Task */
    private String name;
    /** Due date of the Task */
    private Calendar dueDate;
    /** Total number of hours for the Task */
    private int totalHours;
    /** Number of SubTask hours */
    private int subTotalHours;
    /** # of hours / (DueDate - StartingDay) */
    private int averageNumHours;

    /**
     * Primary constructor for Task
     *
     * @param name name of Task
     * @param hours number of hours for Task
     * @param incrementation number of days till due date for Task
     */
    public Task(String name, int hours, int incrementation) {
        setName(name);
        setTotalHours(hours);
        setDueDate(incrementation);
    }

    /**
     * Adds a SubTask for a given Task
     *
     * @param hours number of hours
     * @param overflow overflow status of the SubTask
     * @return SubTask
     */
    public SubTask addSubTask(int hours, boolean overflow) {
        SubTask subtask = null;
        if(hours > 0 && subTotalHours + hours <= totalHours) {
            subtask = new SubTask(this, hours, overflow);
            subTotalHours += hours;
        }
        return subtask;
    }

    /**
     * Resets the Task in all of its properties
     */
    public void reset() {
        subTotalHours = 0;
        this.averageNumHours = 0;
    }

    /**
     * Gets the number of SubTask hour slots currently unfilled
     *
     * @return number of unfilled hours for SubTasks
     */
    public int getSubTotalHoursRemaining() {
        return totalHours - subTotalHours;
    }

    @Override
    public int compareTo(Task o) {
        long timeDiff = this.dueDate.compareTo(o.dueDate);
        if(timeDiff < 0 || timeDiff == 0 && this.getSubTotalHoursRemaining() > o.getSubTotalHoursRemaining()) {
            return -1;
        } else if(timeDiff > 0 || timeDiff == 0 && this.getSubTotalHoursRemaining() < o.getSubTotalHoursRemaining()) {
            return 1;
        } else {
            return 0;
        }
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
    private void setName(String name) {	//TODO will need to include exceptions for the setters
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
     * @param incrementation number of days till due date for Task
     */
    private void setDueDate(int incrementation) { //TODO will need to include exceptions for the setters
        this.dueDate = Time.getFormattedCalendarInstance(incrementation);
    }

    /**
     * Gets the total number of hours for the Task
     *
     * @return number of hours for the Task
     */
    public int getTotalHours() {
        return totalHours;
    }

    /**
     * Sets the total number of hours for the Task
     *
     * @param total number of hours for the Task
     */
    private void setTotalHours(int total) { //TODO will need to include exceptions for the setters
        this.totalHours = total;
    }

    /**
     * Computes and sets the average number of hours based on (number of hours / days)
     *
     * @param date starting date that the task is available
     */
    public void setAverageNumHours(Calendar date) {
        int days = Time.determineRangeOfDays(date, this.dueDate) + 1;
        int avgHours = this.totalHours / days;
        avgHours += this.totalHours % days == 0 ? 0 : 1;
        this.averageNumHours = avgHours;
    }

    /**
     * Gets the average number of hours for the Task
     *
     * @return average number of hours for the Task
     */
    public int getAverageNumHours() {
        return averageNumHours;
    }

    @Override
    public String toString() {
        return "Task [name=" + name + ", total=" + totalHours + "]";
    }


    /**
     * The individual component of a parent Task in the form of a SubTask
     *
     * TODO will need to include a markComplete() method
     *
     * @author Andrew Roe
     */
    public class SubTask implements Comparable<SubTask> {

        /** Parent Task of the SubTask */
        private Task parentTask;
        /** Number of hours for the SubTask */
        private int hours;
        /** Number of overflow hours due to scheduling */
        private boolean overflowStatus;

        /**
         * Primary constructor for SubTask
         *
         * @param parentTask parent of the SubTask
         * @param hours number of hours for the SubTask
         */
        private SubTask(Task parentTask, int hours, boolean overflow) {
            this.parentTask = parentTask;
            this.hours = hours;
            this.overflowStatus = overflow;
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
        public int getSubTaskHours() {
            return hours;
        }

        /**
         * Determines whether SubTask overflowed
         *
         * @return overflow status
         */
        public boolean getOverflowStatus() {
            return overflowStatus;
        }

        @Override
        public String toString() {
            return "SubTask [name=" + name + ", hours=" + hours + "]";
        }

        @Override
        public int compareTo(SubTask o) {
            return this.parentTask.compareTo(o.parentTask);
        }
    }
}
