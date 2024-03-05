package com.agile.planner.util;

import com.agile.planner.data.Card;
import com.agile.planner.schedule.day.Day;
import com.agile.planner.data.Task;

import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.InputMismatchException;

/**
 * Creates a cumulative log of all actions performed during each session
 *
 * @author Andrew Roe
 * @author Lucia Langaney
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
        output = new PrintStream("logs/system.log");
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
     * @param type Action type (0=Add, 1=Remove, 2=EDIT)
     */
    public void reportTaskAction(Task task, int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        if(type == 0) {
            output.print(" ADD(TASK): ");
        } else if(type == 1) {
            output.print(" REMOVE(TASK): ");
        } else {
            output.print(" EDIT(TASK): ");
        }
        output.print(" ID=" + task.getId());
        output.print(", NAME=" + task.getName());
        output.print(", HOURS=" + task.getTotalHours());
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy");
        output.println(", DUE_DATE=" + sdf2.format(task.getDueDate().getTime()));
    }

    /**
     * Reports a given Card action
     *
     * @param card Card being reported
     * @param type Action type (0=Card Created, 1=Task Added, 2=Task Removed, 3=Label Added, 4=Label Removed)
     */
    public void reportCardAction(Card card, int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");

        if(type == 0) {
            output.print(" CREATE(CARD): ");
        } else if(type == 1) {
            output.print(" ADD(TASK): ");
        } else if(type == 2) {
            output.print(" REMOVE(TASK): ");
        } else if(type == 3) {
            output.print(" ADD(LABEL): ");
        } else {
            output.print(" REMOVE(LABEL): ");
        }

        //output.print(" ID=" + card.getId()); //TODO: need to finish getId()
        output.print(", TITLE=" + card.getLabel());
    }


    /**
     * Reports the creation of a CheckList
     *
     * @param cl CheckList created
     */
    public void reportCheckListCreation(CheckList cl) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.print(" CheckList ID=" + cl.getId());
        output.println(", CREATED");
    }

    /**
     * Reports the removal of a CheckList
     *
     * @param cl CheckList removed
     */
    public void reportCheckListRemoval(CheckList cl) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.print(" CheckList ID=" + cl.getId());
        output.println(", REMOVED");
    }

    /**
     * Reports the reset of a CheckList
     *
     * @param cl CheckList being reset
     */
    public void reportCheckListReset(CheckList cl) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.print(" CheckList ID=" + cl.getId());
        output.println(", RESET");
    }

    /**
     * Reports CheckList action performed
     *
     * @param cl CheckList being utilized
     * @param itemIdx index of Item
     * @param action representing which action was performed on CheckList
     */
    public void reportCheckListAction(CheckList cl, int itemIdx, int action) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.print(" CheckList ID=" + cl.getId());
        if(action == 0) {
            output.print(", ITEM_REMOVED=" + cl.getItem(itemIdx));
        } else if(action == 1) {
            output.print(", ITEM_ADDED=" + cl.getItem(itemIdx));
        } else if(action == 2) {
            output.print(", ITEM=" + cl.getItem(itemIdx) + ", MARKED=COMPLETE");
        } else if(action == 3) {
            output.print(", ITEM=" + cl.getItem(itemIdx) + ", MARKED=INCOMPLETE");
        } else if(action == 4) {
            output.print(", ITEM=" + cl.getItem(itemIdx) + ", SHIFTED");
        }
        output.println(", PERCENTAGE_COMPLETE=" + cl.getPercentage());
    }

    /**
     * Reports a given Day edit
     *
     * @param date Calendar date being reported
     * @param hours number of hours assigned for the day
     * @param global whether edit is standard or not
     */
    public void reportWeekEdit(Calendar date, int hours, boolean global) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
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
        output.print(" [INFO]");
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
        output.print(" [INFO]");
        output.println(" Scheduling has begun...");
    }

    /**
     * Reports the end of scheduling
     */
    public void reportSchedulingFinish() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.println(" Scheduling has finished...");
    }

    /**
     * Reports the display of the schedule for the current day
     *
     * @param day Day being displayed to STDOUT
     */
    public void reportDisplayDaySchedule(Day day) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.print(" Display Day_Schedule: ");
        output.print("CAPACITY=" + day.getCapacity());
        output.print(", HOURS_FILLED=" + day.getHoursFilled());
        output.println(", NUM_TASKS=" + day.getNumSubTasks());

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
        output.print(" [INFO]");
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
        output.print(" [ERROR] " + e.getMessage());
    }

    /**
     * Reports the processing of tasks from a file
     *
     * @param filename name of file that contains list of Tasks
     */
    public void reportProcessTasks(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.println(" Reading Tasks: FILE=" + filename);
    }


    /**
     * Reports the processing of Config from a file
     *
     * @param filename name of Config file
     */
    public void reportProcessConfig(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.println(" Reading Config: FILE=" + filename);
    }

    /**
     * Reports User Configuration modifications <p>
     * Types of operations:<ul>
     * <li> Index 0: user_name    (String)
     * <li> Index 1: email        (String)
     * <li> Index 2: day_hours    (int)
     * <li> Index 3: max_days     (int)
     * <li> Index 4: archive_days (int)
     * <li> Index 5: priority     (boolean)
     * <li> Index 6: overflow     (boolean)
     * <li> Index 7: fit_schedule (boolean)
     * <li> Index 8: schedule_alg (int)
     * <li> Index 9: min_hours    (int)
     * </ul>
     *
     * @param idx type of action being performed
     * @param value Object value now being utilized
     */
    public void reportConfigAction(int idx, Object value) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        if(idx == 0) {
            if(value instanceof String) {
                output.println("EDIT(USER_CONFIG): USER_NAME=" + value);
            } else {
                throw new InputMismatchException("Expected <String> for <USER_NAME>");
            }
        } else if(idx == 1) {
            if(value instanceof String) {
                output.println("EDIT(USER_CONFIG): EMAIL=" + value);
            } else {
                throw new InputMismatchException("Expected <String> for <EMAIL>");
            }
        } else if(idx == 2) {
            if(value instanceof Integer) {
                output.println("EDIT(USER_CONFIG): DAY_HOURS=" + value);
            } else {
                throw new InputMismatchException("Expected <Integer> for <DAY_HOURS>");
            }
        } else if(idx == 3) {
            if(value instanceof Integer) {
                output.println("EDIT(USER_CONFIG): MAX_DAYS=" + value);
            } else {
                throw new InputMismatchException("Expected <Integer> for <MAX_DAYS>");
            }
        } else if(idx == 4) {
            if(value instanceof Integer) {
                output.println("EDIT(USER_CONFIG): ARCHIVE_DAYS=" + value);
            } else {
                throw new InputMismatchException("Expected <Integer> for <ARCHIVE_DAYS>");
            }
        } else if(idx == 5) {
            if(value instanceof Boolean) {
                output.println("EDIT(USER_CONFIG): PRIORITY=" + value);
            } else {
                throw new InputMismatchException("Expected <Boolean> for <PRIORITY>");
            }
        } else if(idx == 6) {
            if(value instanceof Boolean) {
                output.println("EDIT(USER_CONFIG): OVERFLOW=" + value);
            } else {
                throw new InputMismatchException("Expected <Boolean> for <OVERFLOW>");
            }
        } else if(idx == 7) {
            if(value instanceof Boolean) {
                output.println("EDIT(USER_CONFIG): FIT_SCHEDULE=" + value);
            } else {
                throw new InputMismatchException("Expected <Boolean> for <FIT_SCHEDULE>");
            }
        } else if(idx == 8) {
            if(value instanceof Integer) {
                output.println("EDIT(USER_CONFIG): SCHEDULE_ALG=" + value);
            } else {
                throw new InputMismatchException("Expected <Integer> for <SCHEDULE_ALG>");
            }
        } else {
            if(value instanceof Integer) {
                output.println("EDIT(USER_CONFIG): MIN_HOURS=" + value);
            } else {
                throw new InputMismatchException("Expected <Integer> for <MIN_HOURS>");
            }
        }
    }

    /**
     * Reads JBin contents from file
     *
     * @param filename name of input file
     */
    public void reportReadJBinFile(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.println(" READE(JBIN): FILE=" + filename);
    }

    /**
     * Writes JBin contents to file
     *
     * @param filename name of output file
     */
    public void reportWriteJBinFile(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.println(" WRITE(JBIN): FILE=" + filename);
    }

    /**
     * Reports the creation of a JBin file
     */
    public void reportCreateJBin() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.println(" JBIN FILE CREATED");
    }

    /**
     * Reports the processing of a JBin file
     */
    public void reportProcessJBin() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.println(" JBIN FILE PROCESSED");
    }

    /**
     * Reports the User's most recent login
     */
    public void reportUserLogin() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.println(" Current session has begun...");
    }

    /*
    todo
        -report all user_config attr
        -report the actions performed when scheduling
        -report the actions performed when processing jbin
        -report of scripting language beginning and ending
        -report of json actions
        -report of google calendar actions
        -report of time actions
        -report of html_report actions
     */

    /**
     * Reports that current session has ended
     */
    public void reportExitSession() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        output.print(sdf.format(Calendar.getInstance().getTime()));
        output.print(" [INFO]");
        output.println(" Current session has ended...");
        output.close();
    }
}