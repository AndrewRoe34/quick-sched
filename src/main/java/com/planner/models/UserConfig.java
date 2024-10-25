package com.planner.models;

/**
 * Handles the management of all user config settings and data
 *
 * @author Andrew Roe
 * @author Abah Olotuche Gabriel
 */
public class UserConfig {

    /** Daily range of hours for day */
    private int[] dailyHoursRange;
    /** Global data for hours per day of week */
    private int[] hoursPerDayOfWeek;
    /** Minimum number of hours for a given day */
    private double[] subtaskRange;
    /** Maximum number of days to display */
    private int maxDays;
    /** Maximum number of past days to display */
    private int archiveDays;
    /** Whether to enable priority for tasks */
    private boolean priority;
    /** Whether to display overflow */
    private boolean overflow;
    /** Whether to maximize the positioning of tasks in relation to each other */
    private boolean optimizeDay;
    /** Whether the scheduling begins at the start of day */
    private boolean defaultAtStart;
    /** Whether to format pretty time */
    private boolean formatPrettyTime;

    /**
     * Primary constructor for UserConfig
     *
     * @param dailyHoursRange     Daily range of hours for day
     * @param globalHr            Global data for week
     * @param subtaskRange        Minimum number of hours for a given day
     * @param maxDays             Maximum number of days to display
     * @param archiveDays         Maximum number of past days to display
     * @param priority            Whether to enable priority for tasks
     * @param overflow            Whether to display overflow
     * @param optimizeDay         Whether to maximize the positioning of tasks in relation to each other
     * @param defaultAtStart      Whether the scheduling begins at the start of day
     * @param formatPrettyTime    Whether to format pretty time
     */
    public UserConfig(int[] dailyHoursRange, int[] globalHr, double[] subtaskRange, int maxDays, int archiveDays, boolean priority,
                      boolean overflow, boolean optimizeDay, boolean defaultAtStart,
                      boolean formatPrettyTime) {
        setDailyHoursRange(dailyHoursRange);
        setHoursPerDayOfWeek(globalHr);
        setSubtaskRange(subtaskRange);
        setMaxDays(maxDays);
        setArchiveDays(archiveDays);
        setPriority(priority);
        setOverflow(overflow);
        setOverflow(overflow);
        setOptimizeDay(optimizeDay);
        setDefaultAtStart(defaultAtStart);
        setFormatPrettyTime(formatPrettyTime);
    }

    /**
     * Constructor for UserConfig that utilizes default values
     */
    public UserConfig() {
        this(new int[]{8, 20}, new int[]{8, 8, 8, 8, 8, 8, 8},
                new double[]{1.0}, 14, 5,
                false, true, true,
                true, true);
    }

    /**
     * Gets daily hours range
     *
     * @return Array of daily hours range
     */
    public int[] getDailyHoursRange() {
        return dailyHoursRange;
    }

    /**
     * Sets daily hours range
     *
     * @param dailyHoursRange Array of daily hours range
     */
    public void setDailyHoursRange(int[] dailyHoursRange) {
        if (isRangeValid(dailyHoursRange))
            throw new IllegalArgumentException("Range was null, empty, or outside of valid set for UserConfig");
        this.dailyHoursRange = dailyHoursRange;
    }

    /**
     * Gets global week hours
     *
     * @return array of week hours
     */
    public int[] getHoursPerDayOfWeek() {
        return hoursPerDayOfWeek;
    }

    /**
     * Sets global week hours
     *
     * @param hoursPerDayOfWeek array of week hours
     */
    public void setHoursPerDayOfWeek(int[] hoursPerDayOfWeek) {
        if (hoursPerDayOfWeek == null)
            throw new IllegalArgumentException("Global hours array cannot be null for UserConfig");

        if (hoursPerDayOfWeek.length == 7) {
            for (int hour : hoursPerDayOfWeek) {
                validateRange(hour, 0, 24, "Hours for week cannot be below 0 or above 24");
            }
        } else {
            throw new IllegalArgumentException("Invalid number of inputs provided for config option Global hours for week. Expected 7 integers");
        }

        this.hoursPerDayOfWeek = hoursPerDayOfWeek;
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
        validateRange(maxDays, 1, 30, "Max day is outside of valid set for UserConfig");
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
        validateRange(archiveDays, 0, 30, "Archive day is outside of valid set for UserConfig");
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
     * Gets minimum number of hours per given day
     *
     * @return minimum number of hours
     */
    public double[] getSubtaskRange() {
        return subtaskRange;
    }

    /**
     * Sets minimum number of hours per given day
     *
     * @param subtaskRange minimum number of hours
     */
    public void setSubtaskRange(double[] subtaskRange) {
        if (subtaskRange.length > 2) {
            throw new IllegalArgumentException("Subtask Range must contain 2 values");
        }
        for (double v : subtaskRange) {
            double decimal = v % 1;
            if (decimal != 0.0 && decimal != 0.5) throw new IllegalArgumentException("Invalid min hour was provided for UserConfig");

        }
        this.subtaskRange = subtaskRange;
    }

    /**
     * Checks if optimization for days is enabled
     *
     * @return <code>true</code> if days are being optimized; <code>false</code> otherwise
     */
    public boolean isOptimizeDay() {
        return optimizeDay;
    }

    /**
     * Enables or disables optimization for days
     *
     * @param optimizeDay Option
     */
    public void setOptimizeDay(boolean optimizeDay) { this.optimizeDay = optimizeDay; }

    /**
     * Checks if the scheduling begins at the start of day
     *
     * @return <code>true</code> if scheduling begins at the start of day; <code>false</code> otherwise
     */
    public boolean isDefaultAtStart() { return defaultAtStart; }

    /**
     * Enables or disables scheduling at the start of day
     *
     * @param defaultAtStart Option
     */
    public void setDefaultAtStart(boolean defaultAtStart) {
        this.defaultAtStart = defaultAtStart;
    }

    /**
     * Checks if formatPrettyTime option is enabled
     *
     * @return <code>true</code> if formatPrettyTime option is enabled; <code>false</code> otherwise
     */
    public boolean isFormatPrettyTime() {
        return formatPrettyTime;
    }

    /**
     * Enables or disables formatPrettyTime option
     *
     * @param formatPrettyTime Option
     */
    public void setFormatPrettyTime(boolean formatPrettyTime) {
        this.formatPrettyTime = formatPrettyTime;
    }


    private boolean isRangeValid(int[] range)
    {
        return range == null || range.length != 2
                || range[0] < 0 || range[0] > 23
                || range[1] < 0 || range[1] > 23;
    }

    private void validateRange(int n, int start, int end, String errorMessage)
    {
        if (n < start || n > end)
            throw new IllegalArgumentException(errorMessage);
    }
}
