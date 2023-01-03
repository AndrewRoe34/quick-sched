package agile.planner.user;

/**
 * Handles the management of all user config settings and data
 *
 * @author Andrew Roe
 */
public class UserConfig {

    /** Name for user */
    private String userName;
    /** Email for user */
    private String email;
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
    private boolean fitSchedule;
    /** Scheduling algorithm chosen */
    private int schedulingAlgorithm;

    /**
     * Primary constructor for UserConfig
     * @param userName Name for user
     * @param userEmail Email for user
     * @param globalHr Global data for week
     * @param maxDays Maximum number of days to display
     * @param archiveDays Maximum number of past days to display
     * @param priority Whether to enable priority for tasks
     * @param overflow Whether to display overflow
     * @param fitSchedule Whether to fit schedule
     * @param schedulingAlgorithm Scheduling algorithm chosen
     */
    public UserConfig(String userName, String userEmail, int[] globalHr, int maxDays, int archiveDays, boolean priority, boolean overflow, boolean fitSchedule, int schedulingAlgorithm) {
        this.userName = userName;
        this.email = userEmail;
        this.week = globalHr;
        this.maxDays = maxDays;
        this.archiveDays = archiveDays;
        this.priority = priority;
        this.overflow = overflow;
        this.fitSchedule = fitSchedule;
        this.schedulingAlgorithm = schedulingAlgorithm;
    }

    /**
     * Gets name for user
     *
     * @return name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets name for user
     *
     * @param userName name
     */
    public void setUserName(String userName) {
        if(userName == null || userName.isEmpty()) {
            //TODO exception
        }
        this.userName = userName;
    }

    /**
     * Gets email for user
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email for user
     *
     * @param email email
     */
    public void setEmail(String email) {
        if(email == null || email.isEmpty()) {
            //TODO exception
        }
        this.email = email;
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
    public boolean isFitSchedule() {
        return fitSchedule;
    }

    /**
     * Sets fitted status for scheduler
     *
     * @param fitSchedule fitted status for scheduler
     */
    public void setFitSchedule(boolean fitSchedule) {
        this.fitSchedule = fitSchedule;
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
}
