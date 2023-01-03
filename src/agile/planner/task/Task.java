package agile.planner.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import agile.planner.util.Time;

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
    private int totalHours;
    /** Number of SubTask hours */
    private int subTotalHours;
    /** # of hours / (DueDate - StartingDay) */
    private int averageNumHours;
    /** CheckList of Items for Task */
    private CheckList checkList; //TODO need to integrate
    /** Label for String */ //TODO will need to finish later
    private List<String> label;

    /**
     * Primary constructor for Task
     *
     * @param id ID of Task
     * @param name name of Task
     * @param hours number of hours for Task
     * @param incrementation number of days till due date for Task
     */
    public Task(int id, String name, int hours, int incrementation) {
        setId(id);
        setName(name);
        setTotalHours(hours);
        setDueDate(incrementation);
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
        } else if(timeDiff > 0 || this.getSubTotalHoursRemaining() < o.getSubTotalHoursRemaining()) {
            return 1;
        } else {
            return 0;
        }
    }

    //TODO need an equals method

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

    /**
     * Adds a CheckList for the Task
     *
     * @param title Title for the CheckList
     * @return CheckList instance
     */
    public CheckList addCheckList(String title) {
        if(checkList == null) {
            this.checkList = new CheckList(title);
        }
        return checkList;
    }

    /**
     * Adds an Item to the CheckList
     *
     * @param description Description info for the Item
     * @return boolean status for whether successful or not
     */
    public boolean addItem(String description) {
        return checkList != null && checkList.addItem(description);
    }

    /**
     * Removes an Item from the CheckList
     *
     * @param idx index for removal
     * @return Item removed from the CheckList
     */
    public CheckList.Item removeItem(int idx) {
        return checkList != null ? checkList.removeItem(idx) : null;
    }

    /**
     * Shifts an Item in the CheckList
     *
     * @param idx index of Item
     * @param shiftIdx new index for Item
     * @return boolean status for whether successful or not
     */
    public boolean shiftItem(int idx, int shiftIdx) {
        if(checkList != null) {
            checkList.shiftItem(idx, shiftIdx);
            return true;
        }
        return false;
    }

    /**
     * Marks an Item as complete or incomplete
     *
     * @param idx index for Item
     * @param flag completion status flag
     * @return boolean value for successful operation
     */
    public boolean markItem(int idx, boolean flag) {
        if(checkList != null) {
            checkList.markItem(idx, flag);
            return true;
        }
        return false;
    }

    /**
     * Gets Item from CheckList
     *
     * @param idx index of Item
     * @return return Item
     */
    public CheckList.Item getItem(int idx) {
        return checkList != null ? checkList.getItem(idx) : null;
    }

    /**
     * Edits an Item in the CheckList
     *
     * @param description newly updated description
     */
    public void editItem(String description) {
        //TODO
    }

    /**
     * Gets String format for CheckList
     *
     * @return String formatted CheckList
     */
    public String getStringCheckList() {
        return checkList != null ? checkList.toString() : null;
    }

    /**
     * Resets the CheckList and removes all of its Items
     *
     * @return boolean status for whether successful or not
     */
    public boolean resetCheckList() {
        if(checkList != null) {
            checkList.resetCheckList();
            return true;
        }
        return false;
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
                averageNumHours == task.averageNumHours && name.equals(task.name) && dueDate.equals(task.dueDate) &&
                Objects.equals(checkList, task.checkList) && Objects.equals(label, task.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dueDate, totalHours, subTotalHours, averageNumHours, checkList, label);
    }

    /**
     * The individual component of a parent Task in the form of a SubTask
     * TODO will need to include a markComplete() method
     *
     * @author Andrew Roe
     */
    public static class SubTask implements Comparable<SubTask> {

        /** Parent Task of the SubTask */
        private final Task parentTask;
        /** Number of hours for the SubTask */
        private final int hours;
        /** Number of overflow hours due to scheduling */
        private final boolean overflowStatus;

        /**
         * Primary constructor for SubTask
         *
         * @param parentTask parent of the SubTask
         * @param hours number of hours for the SubTask
         * @param overflow boolean value for overflow status
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
        public boolean isOverflow() {
            return overflowStatus;
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

    /**
     * CheckList for each individual task assigned to a day
     *
     * @author Andrew Roe
     */
    public static class CheckList {

        /** Name for CheckList */
        private String name;
        /** List of all Items for a task */
        private List<Item> list;
        /** Number of Items completed */
        private int completed;

        /**
         * Primary instance for a CheckList
         *
         * @param name title of the CheckList
         */
        private CheckList(String name) {
            setName(name);
            list = new ArrayList<>();
        }

        /**
         * Sets the title for the CheckList
         *
         * @param name title of the CheckList
         */
        private void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the title for the CheckList
         *
         * @return title for CheckList
         */
        public String getName() {
            return name;
        }

        /**
         * Helper method for verifying the index of the list
         *
         * @param idx index to be verified
         * @return same index if valid (-1 if invalid)
         */
        private int verifyIndex(int idx) {
            if(idx < 0 || idx >= list.size()) {
                return -1;
            }
            return idx;
        }

        /**
         * Adds a new Item to the CheckList
         *
         * @param description description for the Item
         * @return boolean value for successful add of Item
         */
        public boolean addItem(String description) {
            return list.add(new Item(description));
        }

        /**
         * Removes the Item from the CheckList
         *
         * @param idx index of CheckList
         * @return removed Item
         */
        public Item removeItem(int idx) {
            if(verifyIndex(idx) == -1) {
                throw new IllegalArgumentException("Invalid index for checklist");
            }
            return list.remove(idx);
        }

        /**
         * Shifts the Item in the CheckList
         *
         * @param idx index of Item
         * @param shiftIdx new index for Item
         */
        public void shiftItem(int idx, int shiftIdx) {
            if(verifyIndex(idx) == -1 || verifyIndex(shiftIdx) == -1) {
                throw new IllegalArgumentException("Invalid index for checklist");
            }
            list.add(shiftIdx, list.remove(idx));
        }

        /**
         * Gets the Item from the CheckList
         *
         * @param idx index of Item
         * @return Item at specified index
         */
        public Item getItem(int idx) {
            if(verifyIndex(idx) == -1) {
                throw new IllegalArgumentException("Invalid index for checklist");
            }
            return list.get(idx);
        }

        /**
         * Marks an Item as complete or incomplete
         *
         * @param idx index of Item
         * @param flag completion status flag
         */
        public void markItem(int idx, boolean flag) {
            if(verifyIndex(idx) == -1) {
                throw new IllegalArgumentException("Invalid index for checklist");
            }
            Item i1 = getItem(idx);
            if(flag && !i1.isComplete()) {
                completed++;
            } else if(!flag && i1.isComplete()) {
                completed--;
            }
            i1.setCompletionStatus(flag);
        }

        /**
         * Resets the CheckList
         */
        public void resetCheckList() {
            this.list = new ArrayList<>();
        }

        /**
         * Size of the CheckList
         *
         * @return size of CheckList
         */
        public int size() {
            return list.size();
        }

        /**
         * Gets the completion percentage of CheckList
         *
         * @return completion percentage
         */
        public int getPercentage() {
            return list.size() == 0 ? 0 : (int) (completed * 1.0f / list.size() * 100);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(name + " [" + getPercentage() + "%]:\n");
            for(Item i : list) {
                sb.append("* ").append(i.toString()).append("\n");
            }
            return sb.toString();
        }

        /**
         * Edits an Item from the CheckList
         *
         * @param description newly updated description
         */
        public void editItem(String description) {
            //TODO
        }

        /**
         * Represents a single Item in a CheckList
         *
         * @author Andrew Roe
         */
        public static class Item {

            /** Description for Item */
            private String description;
            /** Completion status of Item */
            private boolean status;

            /**
             * Primary constructor for Item
             *
             * @param description description for Item
             */
            private Item(String description) {
                setDescription(description);
            }

            /**
             * Sets the description for Item
             *
             * @param description description for Item
             */
            private void setDescription(String description) {
                this.description = description;
            }

            /**
             * Gets the description for Item
             *
             * @return description for Item
             */
            public String getDescription() {
                return description;
            }

            /**
             * Marks Item as incomplete
             *
             * @param flag boolean value for completion
             */
            private void setCompletionStatus(boolean flag) {
                status = flag;
            }

            /**
             * Gets the completion status of Item
             *
             * @return completion status
             */
            public boolean isComplete() {
                return status;
            }

            @Override
            public String toString() {
                if(status) {
                    return description + "✅";
                }
                return description;
            }

        }
    }
}
