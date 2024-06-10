package com.planner.models;

/**
 * Handles the management of all user config settings and data
 *
 * @author Andrew Roe
 * @author Abah Olotuche Gabriel
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
    private boolean optimizeDay;
    private boolean defaultAtStart;
    private boolean localScheduleColors;
    private boolean formatPrettyTime;
    private boolean formatPrettyTable;
    private boolean formatAMPM;
    private boolean resetLog;


    /**
     * Primary constructor for UserConfig
     *
     * @param range               Daily range of hours for day
     * @param globalHr            Global data for week
     * @param maxDays             Maximum number of days to display
     * @param archiveDays         Maximum number of past days to display
     * @param priority            Whether to enable priority for tasks
     * @param overflow            Whether to display overflow
     * @param fitDay              Whether to fit schedule
     * @param schedulingAlgorithm Scheduling algorithm chosen
     * @param minHours            Minimum number of hours for a given day
     * @param optimizeDay         Whether to maximize the positioning of tasks in relation to each other
     * @param defaultAtStart      Whether the scheduling begins at the start of day
     * @param localScheduleColors Whether the local schedule shares the colors of its Google counterpart
     */
    public UserConfig(int[] range, int[] globalHr, int maxDays, int archiveDays, boolean priority,
                      boolean overflow, boolean fitDay, int schedulingAlgorithm, double minHours,
                      boolean optimizeDay, boolean defaultAtStart, boolean localScheduleColors,
                      boolean formatPrettyTime, boolean formatPrettyTable, boolean formatAMPM,
                      boolean resetLog) {
        this.range = range;
        this.week = globalHr;
        this.maxDays = maxDays;
        this.archiveDays = archiveDays;
        this.priority = priority;
        this.overflow = overflow;
        this.fitDay = fitDay;
        this.schedulingAlgorithm = schedulingAlgorithm;
        this.minHours = minHours;
        this.optimizeDay = optimizeDay;
        this.defaultAtStart = defaultAtStart;
        this.localScheduleColors = localScheduleColors;
        this.formatPrettyTime = formatPrettyTime;
        this.formatPrettyTable = formatPrettyTable;
        this.formatAMPM = formatAMPM;
        this.resetLog = resetLog;
    }

    /**
     * Constructor for UserConfig that utilizes default values
     */
    public UserConfig() {
        this.range = new int[]{8, 20};
        this.week = new int[]{8, 8, 8, 8, 8, 8, 8};
        this.maxDays = 14;
        this.archiveDays = 5;
        this.priority = false;
        this.overflow = true;
        this.fitDay = true;
        this.schedulingAlgorithm = 1;
        this.minHours = 1.0;
        this.optimizeDay = true;
        this.defaultAtStart = true;
        this.localScheduleColors = true;
        this.formatPrettyTime = true;
        this.formatPrettyTable = true;
        this.formatAMPM = true;
        this.resetLog = true;
    }

    public int[] getRange() {
        return range;
    }

    public void setRange(int[] range) {
        if (range == null || range.length != 2 || range[0] < 0 || range[0] > 23
                || range[1] < 0 || range[1] > 23) throw new IllegalArgumentException("Range was null, empty, or outside of valid set for UserConfig");
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
    public void setWeek(int[] week) {
        if (week == null) throw new IllegalArgumentException("Global hours array cannot be null for UserConfig");
        if (week.length == 7) {
            for (int hour : week) {
                if (hour < 0 || hour > 24) {
                    throw new IllegalArgumentException("Hours for week cannot be below 0 or above 24");
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid number of inputs provided for config option Global hours for week. Expected 7 integers");
        }

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
    public void setMaxDays(int maxDays) {
        if (maxDays <= 0 || maxDays > 30) throw new IllegalArgumentException("Max day is outside of valid set for UserConfig");
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
    public void setArchiveDays(int archiveDays) {
        if (archiveDays < 0 || archiveDays > 30) throw new IllegalArgumentException("Archive day is outside of valid set for UserConfig");
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
    public void setSchedulingAlgorithm(int schedulingAlgorithm) {
        if (schedulingAlgorithm < 0 || schedulingAlgorithm > 1) throw new IllegalArgumentException("Scheduling algorithm option is out of index for UserConfig");
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
        double decimal = minHours % 1;
        if (decimal != 0.0 && decimal != 0.5) throw new IllegalArgumentException("Invalid min hour was provided for UserConfig");
        this.minHours = minHours;
    }

    public boolean isOptimizeDay() {
        return optimizeDay;
    }

    public void setOptimizeDay(boolean optimizeDay) { this.optimizeDay = optimizeDay; }

    public boolean isDefaultAtStart() { return defaultAtStart; }

    public void setDefaultAtStart(boolean defaultAtStart) {
        this.defaultAtStart = defaultAtStart;
    }

    public boolean isLocalScheduleColors() { return localScheduleColors; }

    public void setLocalScheduleColors(boolean localScheduleColors) {
        this.localScheduleColors = localScheduleColors;
    }

    public boolean isFormatPrettyTime() {
        return formatPrettyTime;
    }

    public void setFormatPrettyTime(boolean formatPrettyTime) {
        this.formatPrettyTime = formatPrettyTime;
    }

    public boolean isFormatPrettyTable() {
        return formatPrettyTable;
    }

    public void setFormatPrettyTable(boolean formatPrettyTable) {
        this.formatPrettyTable = formatPrettyTable;
    }

    public boolean isFormatAMPM() {
        return formatAMPM;
    }

    public void setFormatAMPM(boolean formatAMPM) {
        this.formatAMPM = formatAMPM;
    }

    public boolean isResetLog() {
        return resetLog;
    }

    public void setResetLog(boolean resetLog) {
        this.resetLog = resetLog;
    }
}
