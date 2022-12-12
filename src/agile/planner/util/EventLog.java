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
            output = new PrintStream(new FileOutputStream(FileDescriptor.out));
        } else {
            output = new PrintStream("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\AGILE Systems\\log.txt");
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
        output.println(", DUE_DATE=" + task.getDueDate());
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
        output.print(", HOURS=" + hours);
    }

    /**
     * Reports actions for given Day
     *
     * @param day Day being utilized
     * @param task Task being added
     * @param overflow overflow status for Day
     */
    public void reportDayAction(Day day, Task task, boolean overflow) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" CAPACITY=" + day.getCapacity());
        output.print(", HOURS REMAINING=" + day.getSpareHours());
        output.print(", HOURS FILLED=" + day.getHoursFilled());
        output.print(", TASK ADDED=" + task.getId());
        output.println(", OVERFLOW=" + overflow);
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