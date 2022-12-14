package agile.planner.util;

import agile.planner.manager.day.Day;
import agile.planner.task.Task;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.PriorityQueue;

/**
 * Creates a cumulative log of all actions performed during each session
 *
 * @author Andrew Roe
 */
public class EventLog {

    /** Singleton instance for EventLog */
    private static EventLog instance;
    /** PrintStream for outputting to log */
    private final PrintStream output;

    /**
     * Primary private constructor for EventLog
     *
     * @throws FileNotFoundException thrown if invalid file
     */
    private EventLog(boolean debug) throws FileNotFoundException {
        if(debug) {
            output = new PrintStream("system.log");
        } else {
            output = new PrintStream("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\AGILE Systems\\system.log");
        }
        output.print(new SimpleDateFormat("[dd-MM-yyyy]").format(Calendar.getInstance().getTime()));
        output.println(" Log of all activities from current session: \n");
    }

    /**
     * Gets a singleton of EventLog with passed file
     *
     * @param debug boolean value for whether test is running
     * @return singleton of EventLog
     * @throws FileNotFoundException thrown if invalid file
     */
    public static EventLog getEventLog(boolean debug) throws FileNotFoundException {
        if(instance == null) {
            instance = new EventLog(debug);
        }
        return instance;
    }

    /**
     * Reports a given Task action
     *
     * @param task Task being reported
     * @param type Action type (0=Add, 1=Remove)
     */
    public void reportTaskAction(Task task, int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));

        if(type == 0) {
            output.print(" ADD(TASK): ");
        } else {
            output.print(" REMOVE(TASK): ");
        }
        output.print(" ID=" + task.getId());
        output.print(", NAME=" + task.getName());
        output.print(", HOURS=" + task.getTotalHours());
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy");
        output.println(", DUE_DATE=" + sdf2.format(task.getDueDate().getTime())); //TODO need to format it with sdf
    }

    /**
     * Reports a given Day edit
     *
     * @param date Calendar date being reported
     * @param global whether edit is standard or not
     */
    public void reportWeekEdit(Calendar date, int hours, boolean global) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" EDIT(DAY): GLOBAL=" + global);
        output.println(", HOURS=" + hours);
    }

    /**
     * Reports actions for given Day
     *
     * @param day Day being utilized
     * @param task Task being added
     * @param nonOverflow overflow status for Day
     */
    public void reportDayAction(Day day, Task task, boolean nonOverflow) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" DAY_ID=" + day.getId());
        output.print(", CAPACITY=" + day.getCapacity());
        output.print(", HOURS_REMAINING=" + day.getSpareHours());
        output.print(", HOURS_FILLED=" + day.getHoursFilled());
        output.print(", TASK ADDED=" + task.getId());
        output.println(", OVERFLOW=" + !nonOverflow);
    }

    /**
     * Reports the start of scheduling
     */
    public void reportSchedulingStart() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.println(" Scheduling has begun...");
    }

    /**
     * Reports the end of scheduling
     */
    public void reportSchedulingFinish() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.println(" Scheduling has finished...");
    }

    /**
     * Reports the display of a schedule either to a file or to standard output
     *
     * @param days number of Days in schedule
     * @param numTasks number of Tasks in schedule
     * @param status whether output is directed to STDOUT or not
     */
    public void reportDisplaySchedule(int days, int numTasks, boolean status) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" Display Schedule: DAYS=" + days);
        output.print(", NUM_TASKS=" + numTasks);
        output.println(", STDOUT=" + status);
    }

    /**
     * Reports a given exception thrown
     *
     * @param e exception being reported
     */
    public void reportException(Exception e) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.println(" ERROR: " + e.getMessage());
    }

    /**
     * Reports the processing of tasks from a file
     *
     * @param filename name of file that contains list of Tasks
     */
    public void reportProcessTasks(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.println(" Reading Tasks: FILE=" + filename);
    }

    /**
     * Reports the User's most recent login
     */
    public void reportUserLogin() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.println(" Current session has begun...");
    }

    /**
     * Reports that current session has ended
     */
    public void reportExitSession() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.println(" Current session has ended...");
        output.close();
    }
}