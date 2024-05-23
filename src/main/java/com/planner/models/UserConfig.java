package com.planner.models;

/**
 * Handles the management of all user config settings and data
 *
 * @author Andrew Roe
 */
public class UserConfig {

    /** Daily range of hours for day */
    private int[] range;
    /** Global data for week */
    private int[] week;
    /** Maximum number of days to display */
    private int maxDays;
    /** Maximum number of past days to display */
    private int archiveDays;
    /** Whether to enable priority for tasks */
    private boolean priority;
    /** Whether to display overflow */
    private boolean overflow;
    /** Whether to fit schedule */
    private boolean fitDay;
    /** Scheduling algorithm chosen */
    private int schedulingAlgorithm;
    /** Minimum number of hours for a given day */
    private double minHours;


    /**
     * Primary constructor for UserConfig
     *
     * @param range               Daily range of hours for day
     * @param globalHr            Global data for week
     * @param maxDays             Maximum number of days to display
     * @param archiveDays         Maximum number of past days to display
     * @param priority            Whether to enable priority for tasks
     * @param overflow            Whether to display overflow
     * @param fitDay         Whether to fit schedule
     * @param schedulingAlgorithm Scheduling algorithm chosen
     * @param minHours            Minimum number of hours for a given day
     */
    public UserConfig(int[] range, int[] globalHr, int maxDays, int archiveDays, boolean priority, boolean overflow, boolean fitDay, int schedulingAlgorithm, double minHours) {
        this.range = range;
        this.week = globalHr;
        this.maxDays = maxDays;
        this.archiveDays = archiveDays;
        this.priority = priority;
        this.overflow = overflow;
        this.fitDay = fitDay;
        this.schedulingAlgorithm = schedulingAlgorithm;
        this.minHours = minHours;
    }

    public int[] getRange() {
        return range;
    }

    public void setRange(int[] range) {
        this.range = range;
    }

    /**
     * Gets global week hours
     *
     * @return array of week hours
     */
    public int[] getWeek() {
        return week;
    }

    /**
     * Sets global week hours
     *
     * @param week array of week hours
     */
    public void setWeek(int[] week) { //TODO need to check hours
        this.week = week;
    }

    /**
     * Gets maximum number of days to display
     *
     * @return max days to display
     */
    public int getMaxDays() {
        return maxDays;
    }

    /**
     * Sets maximum number of days to display
     *
     * @param maxDays max days to display
     */
    public void setMaxDays(int maxDays) { //TODO need to set max days
        this.maxDays = maxDays;
    }

    /**
     * Gets number of archive days to display
     *
     * @return archive days to display
     */
    public int getArchiveDays() {
        return archiveDays;
    }

    /**
     * Sets number of archive days to display
     *
     * @param archiveDays archive days to display
     */
    public void setArchiveDays(int archiveDays) { ////TODO need to set max archive days
        this.archiveDays = archiveDays;
    }

    /**
     * Gets priority status scheduler
     *
     * @return priority status for scheduler
     */
    public boolean isPriority() {
        return priority;
    }

    /**
     * Sets priority status for scheduler
     *
     * @param priority priority status for scheduler
     */
    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    /**
     * Gets overflow status for scheduler
     *
     * @return overflow status for scheduler
     */
    public boolean isOverflow() {
        return overflow;
    }

    /**
     * Sets overflow status for scheduler
     *
     * @param overflow overflow status for scheduler
     */
    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }

    /**
     * Gets fitted status for scheduler
     *
     * @return fitted status for scheduler
     */
    public boolean isFitDay() {
        return fitDay;
    }

    /**
     * Sets fitted status for scheduler
     *
     * @param fitDay fitted status for scheduler
     */
    public void setFitDay(boolean fitDay) {
        this.fitDay = fitDay;
    }

    /**
     * Gets scheduling algorithm option
     *
     * @return algorithm option
     */
    public int getSchedulingAlgorithm() {
        return schedulingAlgorithm;
    }

    /**
     * Sets scheduling algorithm option
     *
     * @param schedulingAlgorithm algorithm option
     */
    public void setSchedulingAlgorithm(int schedulingAlgorithm) { //TODO need to provide max of 4 (currently only 1)
        this.schedulingAlgorithm = schedulingAlgorithm;
    }

    /**
     * Gets minimum number of hours per given day
     *
     * @return minimum number of hours
     */
    public double getMinHours() {
        return minHours;
    }

    /**
     * Sets minimum number of hours per given day
     *
     * @param minHours minimum number of hours
     */
    public void setMinHours(double minHours) {
        this.minHours = minHours;
    }
}
