package agile.planner.util;

import agile.planner.manager.day.Day;
import agile.planner.task.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private EventLog() throws FileNotFoundException {
        File directory = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\AGILE Systems");
        if(!directory.exists()) {
            directory.mkdir();
        }
        output = new PrintStream("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\AGILE Systems\\log.txt");
        output.print(new SimpleDateFormat("[dd-MM-yyyy]").format(Calendar.getInstance().getTime()));
        output.println(" Log of all activities from current session: \n");
    }

    /**
     * Gets a singleton of EventLog with passed file
     *
     * @return singleton of EventLog
     * @throws FileNotFoundException thrown if invalid file
     */
    public static EventLog getEventLog() throws FileNotFoundException {
        if(instance == null) {
            instance = new EventLog();
        }
        return instance;
    }

    /**
     * Reports a given Task action
     *
     * @param task Task being reported
     * @param type Action type (0=Add, 1=Edit, 2=Remove)
     */
    public void reportTaskAction(Task task, int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));

        if(type == 0) {
            output.print(" ADD(TASK): ");
        } else if(type == 1) {
            output.print(" EDIT(TASK): ");
        } else {
            output.print(" REMOVE(TASK): ");
        }
        output.print(" NAME=");
        output.print(task.getName());
        output.print(", HOURS=");
        output.print(task.getTotalHours() - task.getSubTotalHoursRemaining());
        output.print(", DUE_DATE=");
        output.println(task.getDueDate());
    }

    /**
     * Reports a given Day edit
     *
     * @param day Day being reported
     */
    public void reportWeekEdit(Day day) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));

        output.print(" EDIT(DAY): CAPACITY=");
        output.print(day.getCapacity());
        output.print(", HOURS SCHEDULED=");
        output.println(day.);
    }

    /**
     * Reports a given exception thrown
     *
     * @param e exception being reported
     */
    public void reportException(Exception e) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" ERROR: ");
        output.println(e.getMessage());
    }

    /**
     * Reports the User's most recent login
     *
     * @param user User being reported
     */
    public void reportUserLogin(Client user) {
        output.print(user.getLasLoginTime());
        output.print(": Most recent login by ");
        output.println(user.getName());
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