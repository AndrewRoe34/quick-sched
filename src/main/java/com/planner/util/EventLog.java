package com.planner.util;

import com.planner.models.*;
import com.planner.schedule.day.Day;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.InputMismatchException;

/**
 * Creates a cumulative log of all actions performed during each session
 *
 * @author Andrew Roe
 * @author Lucia Langaney
 * @author Abah Olotuche Gabriel
 */
public class EventLog {

    /** Singleton instance for EventLog */
    private static EventLog instance;

    private StringBuilder sb = new StringBuilder();

    /**
     * Primary private constructor for EventLog
     */
    private EventLog() {
        sb.append(new SimpleDateFormat("[dd-MM-yyyy]").format(Calendar.getInstance().getTime()))
                .append(" Log of all activities from current session: \n\n");
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
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        if(type == 0) {
            sb.append(" ADD(TASK):");
        } else if(type == 1) {
            sb.append(" REM(TASK):");
        } else if (type == 2) {
            sb.append(" MOD(TASK):");
        }
        sb.append(" ID=").append(task.getId());
        sb.append(", NAME=").append(task.getName());
        sb.append(", TAG=").append(task.getTag());
        sb.append(", HOURS=").append(task.getTotalHours());

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
        sb.append(", DUE=").append(sdf2.format(task.getDueDate().getTime()));
        sb.append(", ARCHIVED=");
        Calendar curr = Calendar.getInstance();
        if (task.getTotalHours() == 0 || (!Time.doDatesMatch(curr, task.getDueDate()) && task.getDueDate().compareTo(curr) < 0)) {
            sb.append("TRUE").append("\n");
        } else {
            sb.append("FALSE").append("\n");
        }
    }

    /**
     * Reports a given Event action
     *
     * @param event Event being reported
     * @param type Action type (0=Add, 1=Remove, 2=EDIT)
     */
    public void reportEventAction(Event event, int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");

        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");

        if(type == 0)
            sb.append(" ADD(EVENT):");
        else if(type == 1)
            sb.append(" REM(EVENT):");
        else if (type == 2)
            sb.append(" MOD(EVENT):");

        sb.append(" ID=").append(event.getId());
        sb.append(", NAME=").append(event.getName());
        sb.append(", TAG=").append(event.getCard() != null ? event.getCard().getName() : null);
        sb.append(", TIMESTAMP=").append(event.getTimeStamp().toString());

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
        sb.append(", DATE=").append(sdf2.format(event.getTimeStamp().getStart().getTime()));

        sb.append(", RECURRENCE=").append(event.isRecurring());

        if (event.isRecurring())
            sb.append(", DAYS=").append(Arrays.toString(event.getDays()));

        sb.append("\n");
    }

    public void reportCardAction(Card card, int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");

        if(type == 0) {
            sb.append(" ADD(CARD):");
        } else if(type == 1) {
            sb.append(" REM(CARD):");
        } else if(type == 2) {
            sb.append(" MOD(CARD):");
        }

        sb.append(" ID=").append(card.getId());
        sb.append(", TITLE=").append(card.getName());
        sb.append(", COLOR=").append(card.getColor());
        sb.append("\n");
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
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO] DAY:");
        sb.append(" ID=").append(day.getId());
        sb.append(", CAPACITY=").append(day.getCapacity());
        sb.append(", HOURS_REMAINING=").append(day.getSpareHours());
        sb.append(", HOURS_FILLED=").append(day.getHoursFilled());
        sb.append(", TASK ADDED=").append(task.getId());
        sb.append(", OVERFLOW=").append(!nonOverflow).append("\n");
    }

    /**
     * Reports the start of scheduling
     */
    public void reportSchedulingStart() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" SCHEDULING HAS BEGUN...\n");
    }

    /**
     * Reports the end of scheduling
     */
    public void reportSchedulingFinish() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" SCHEDULING HAS FINISHED...\n");
    }

    public void reportSerializingSchedule(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));

        sb.append(" [INFO]");
        sb.append(" SERIALIZING DATA TO ").append(filename);
        sb.append('\n');
    }

    public void reportDeserializingSchedule(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));

        sb.append(" [INFO]");
        sb.append(" DESERIALIZING DATA FROM ").append(filename);
        sb.append('\n');
    }

    /**
     * Reports the display of the schedule for the current day
     *
     * @param day Day being displayed to STDOUT
     */
    public void reportDisplayDaySchedule(Day day) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" Display Day_Schedule: ");
        sb.append("CAPACITY=").append(day.getCapacity());
        sb.append(", HOURS_FILLED=").append(day.getHoursFilled());
        sb.append(", NUM_TASKS=").append(day.getNumSubTasks()).append("\n");

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
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" Display Schedule: DAYS=").append(days);
        sb.append(", NUM_TASKS=").append(numTasks);
        sb.append(", STDOUT=").append(status).append("\n");
    }

    /**
     * Reports a given exception thrown
     *
     * @param e exception being reported
     */
    public void reportException(Exception e) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [ERROR] ").append(e.getMessage()).append("\n");
    }


    /**
     * Reports the processing of Config from a file
     *
     * @param filename name of Config file
     */
    public void reportProcessConfig(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" Reading Config: FILE=").append(filename).append("\n");
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
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        if(idx == 0) {
            if(value instanceof String) {
                sb.append("EDIT(USER_CONFIG): USER_NAME=").append(value).append("\n");
            } else {
                throw new InputMismatchException("Expected <String> for <USER_NAME>");
            }
        } else if(idx == 1) {
            if(value instanceof String) {
                sb.append("EDIT(USER_CONFIG): EMAIL=").append(value).append("\n");
            } else {
                throw new InputMismatchException("Expected <String> for <EMAIL>");
            }
        } else if(idx == 2) {
            if(value instanceof Integer) {
                sb.append("EDIT(USER_CONFIG): DAY_HOURS=").append(value).append("\n");
            } else {
                throw new InputMismatchException("Expected <Integer> for <DAY_HOURS>");
            }
        } else if(idx == 3) {
            if(value instanceof Integer) {
                sb.append("EDIT(USER_CONFIG): MAX_DAYS=").append(value).append("\n");
            } else {
                throw new InputMismatchException("Expected <Integer> for <MAX_DAYS>");
            }
        } else if(idx == 4) {
            if(value instanceof Integer) {
                sb.append("EDIT(USER_CONFIG): ARCHIVE_DAYS=").append(value).append("\n");
            } else {
                throw new InputMismatchException("Expected <Integer> for <ARCHIVE_DAYS>");
            }
        } else if(idx == 5) {
            if(value instanceof Boolean) {
                sb.append("EDIT(USER_CONFIG): PRIORITY=").append(value).append("\n");
            } else {
                throw new InputMismatchException("Expected <Boolean> for <PRIORITY>");
            }
        } else if(idx == 6) {
            if(value instanceof Boolean) {
                sb.append("EDIT(USER_CONFIG): OVERFLOW=").append(value).append("\n");
            } else {
                throw new InputMismatchException("Expected <Boolean> for <OVERFLOW>");
            }
        } else if(idx == 7) {
            if(value instanceof Boolean) {
                sb.append("EDIT(USER_CONFIG): FIT_SCHEDULE=").append(value).append("\n");
            } else {
                throw new InputMismatchException("Expected <Boolean> for <FIT_SCHEDULE>");
            }
        } else if(idx == 8) {
            if(value instanceof Integer) {
                sb.append("EDIT(USER_CONFIG): SCHEDULE_ALG=").append(value).append("\n");
            } else {
                throw new InputMismatchException("Expected <Integer> for <SCHEDULE_ALG>");
            }
        } else {
            if(value instanceof Integer) {
                sb.append("EDIT(USER_CONFIG): MIN_HOURS=").append(value).append("\n");
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
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" READ(JBIN): FILE=").append(filename).append("\n");
    }

    /**
     * Writes JBin contents to file
     *
     * @param filename name of output file
     */
    public void reportWriteJBinFile(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" WRITE(JBIN): FILE=").append(filename).append("\n");
    }

    /**
     * Reports the creation of a JBin file
     */
    public void reportCreateJBin() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" JBIN FILE CREATED...\n");
    }

    /**
     * Reports the processing of a JBin file
     */
    public void reportProcessJBin() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" JBIN FILE PROCESSED...\n");
    }

    /**
     * Reports the User's most recent login
     */
    public void reportUserLogin() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" CURRENT SESSION HAS BEGUN...\n");
    }

    /**
     * Reports the UserConfig attributes
     *
     * @param userConfig UserConfig attributes
     */
    public void reportUserConfigAttr(UserConfig userConfig) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" RANGE=").append(Arrays.toString(userConfig.getDailyHoursRange()));
        sb.append(", WEEK_HOURS=").append(Arrays.toString(userConfig.getHoursPerDayOfWeek()));
        sb.append(", MAX_DAYS=").append(userConfig.getMaxDays());
        sb.append(", ARCHIVE_DAYS=").append(userConfig.getArchiveDays());
        sb.append(", PRIORITY=").append(userConfig.isPriority());
        sb.append(", OVERFLOW=").append(userConfig.isOverflow());
        sb.append(", MIN_HOURS=").append(Arrays.toString(userConfig.getSubtaskRange()));
        sb.append(", OPTIMIZE_DAY=").append(userConfig.isOptimizeDay());
        sb.append(", DEFAULT_AT_START=").append(userConfig.isDefaultAtStart()).append("\n");
    }

    public void reportJsonActions() {
        //todo
    }

    public void reportExcelFileNameChange(String newName) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" EXCEL FILE NAME HAS BEEN CHANGED TO '").append(newName).append(".xlsx'...\n");
    }

    public void reportExcelFileCreation() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" EXCEL FILE CREATION PROCESSED...\n");
    }

    public void reportExcelExportSchedule() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" SCHEDULE EXPORTED TO EXCEL FILE...\n");
    }

    public void reportGoogleCalendarAuthorization() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" GOOGLE CALENDAR AUTHORIZATION PROCESSED...\n");
    }

    public void reportGoogleCalendarCleanSchedule(int numTasksDeleted) {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" ").append(numTasksDeleted).append(" TASKS REMOVED FROM GOOGLE CALENDAR...\n");
    }

    public void reportGoogleCalendarExportSchedule() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" SCHEDULE EXPORTED TO GOOGLE CALENDAR...\n");
    }

    public void reportGoogleCalendarImportSchedule() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" SCHEDULE IMPORTED FROM GOOGLE CALENDAR...\n");
    }

    /**
     * Reports that current session has ended
     */
    public void reportExitSession() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        sb.append(sdf.format(Calendar.getInstance().getTime()));
        sb.append(" [INFO]");
        sb.append(" CURRENT SESSION HAS ENDED...\n");
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}