package agile.planner.user;

import java.util.List;

public class UserConfig {

    private String userName;

    private String email;

    private int[] week;

    private int maxDays;

    private int archiveDays;

    private boolean priority;

    private boolean overflow;

    private boolean fitSchedule;

    private int schedulingAlgorithm;

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
}
