package com.agile.planner.scripter;

public class TaskInstance extends ClassInstance {

    /** Name of the Task */
    private final String name;
    /** Total number of hours for the Task */
    private final int totalHours;
    /** Due date of the Task */
    private final int dueDate;

    public TaskInstance(String name, int totalHours, int dueDate) {
        this.name = name;
        this.totalHours = totalHours;
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public int getDueDate() {
        return dueDate;
    }
}
