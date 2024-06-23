package com.planner.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

import com.planner.io.SpreadsheetIO;
import com.planner.models.*;
import com.planner.io.GoogleCalendarIO;
import com.planner.io.IOProcessing;
import com.planner.schedule.Scheduler;
import com.planner.schedule.day.Day;
import com.planner.ui.tables.TableFormatter;
import com.planner.util.EventLog;
import com.planner.util.JBin;
import com.planner.util.JsonHandler;
import com.planner.util.Time;

/**
 * Handles the generation and management of the overall schedule
 *
 * @author Andrew Roe
 * @author Abah Olotuche Gabriel
 */
public class ScheduleManager {

    /** List of Cards holding Tasks */
    private List<Card> cards;
    /** LinkedList of Days representing a single schedule */
    private List<Day> schedule;
    /** PriorityQueue of all archived Tasks in sorted order */
    private PriorityQueue<Task> archivedTasks;
    /** PriorityQueue of all Tasks in sorted order */
    private PriorityQueue<Task> taskManager;
    /** Mapping of all Tasks via their unique IDs */
    private Map<Integer, Task> taskMap;
    /** Singleton for ScheduleManager */
    private static ScheduleManager singleton;
    /** Performs all scheduling operations for each day */
    private Scheduler scheduler;
    /** Holds all user settings for scheduling purposes */
    private static UserConfig userConfig;
    /** Logs all actions performed by user */
    private static EventLog eventLog;
    /** Stores custom hours for future days */
    private Map<Integer, Integer> customHours;
    /** Total count for the number of errors that occurred in schedule generation */
    private int errorCount;
    /** ID counter for Tasks */
    private int taskId;
    /** ID specifier for each Day */
    private int dayId;
    /** ID specifier for each Label */
    private int labelId;
    /** ID specifier ro each CheckList */
    private int checklistId;
    private int cardId;
    /** Last day Task is due */
    private int lastDueDate;
    private final GoogleCalendarIO googleCalendarIO;
    private final SpreadsheetIO spreadsheetIO;
    private Calendar scheduleTime;
    private final List<Event> indivEvents;
    private final List<List<Event>> recurringEvents;
    private int eventId;

    /**
     * Private constructor of ScheduleManager
     * Initially performs task processing as well as schedule generation
     */
    private ScheduleManager() {
        try {
            eventLog = EventLog.getEventLog();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Could not locate logging file");
        }
        eventLog.reportUserLogin();
        processUserConfigFile();

        taskManager = new PriorityQueue<>();
        try {
            googleCalendarIO = new GoogleCalendarIO(eventLog);
            spreadsheetIO = new SpreadsheetIO(eventLog);
        }
        catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException();
        }

        scheduler = Scheduler.getInstance(userConfig, eventLog, userConfig.getSchedulingAlgorithm());
        // in situations where ScheduleManager is run multiple times after updates to config, this ensures options are set up properly
        scheduler.updateConfig(userConfig);

        schedule = new LinkedList<>();
        customHours = new HashMap<>();
        taskMap = new HashMap<>();
        cards = new ArrayList<>();
        cards.add(new Card(0, "Default", Card.Colors.LIGHT_BLUE));
        archivedTasks = new PriorityQueue<>();
        indivEvents = new ArrayList<>();

        // Gotta initialize all the lists lol
        recurringEvents = new ArrayList<>(7);
        for (int i = 0; i < 7; i++)
            recurringEvents.add(new ArrayList<>());

        eventId = 0;

        //processSettingsCfg(filename);
        //processJBinFile("data/week.jbin");

    }

    /**
     * Gets a singleton of ScheduleManager
     *
     * @return singleton of ScheduleManager
     */
    public static ScheduleManager getScheduleManager() {
        if(singleton == null) {
            singleton = new ScheduleManager();
        }
        return singleton;
    }

    /**
     * Processes all settings configurations to be used
     */
    private void processUserConfigFile() {
        try {
            eventLog.reportProcessConfig("profile.cfg");
            String configStr = Files.readString(Paths.get("settings/profile.cfg"));
            userConfig = JsonHandler.readUserConfig(configStr);
            eventLog.reportUserConfigAttr(userConfig);
        } catch (FileNotFoundException e) {
            eventLog.reportException(e);
            throw new IllegalArgumentException("Could not locate settings file");
        } catch (IOException e) {
            eventLog.reportException(e);
            throw new IllegalArgumentException("Was unable to process settings file");
        }
    }

    /**
     * Imports JBin file to generate cards and possible schedule
     *
     * @param filename jbin filename
     */
    public void importJBinFile(String filename) {
        String binStr = IOProcessing.readJBinFile(filename);
        if(binStr != null) {
            eventLog.reportReadJBinFile(filename);
            List<Event> eventList = new ArrayList<>();
            JBin.processJBin(binStr, taskManager, eventList, eventId, cards, schedule, userConfig.getArchiveDays());
            eventLog.reportProcessJBin();

            Calendar currDate = Time.getFormattedCalendarInstance(0);
            while (!taskManager.isEmpty()) {
                Task task = taskManager.remove();
                if (task.getDueDate().compareTo(currDate) < 0) {
                    archivedTasks.add(task);
                    // eventlog needs to report the archiving of a task
                    eventLog.reportTaskAction(task, 3);
                }
                else {
                    taskManager.add(task);
                    break;
                }
            }
            taskId = taskManager.size();

            for (Event e : eventList) {
                if (e.isRecurring()) {
                    for (Event.DayOfWeek dayOfWeek : e.getDays()) {
                        recurringEvents.get(dayOfWeek.ordinal()).add(e);
                    }
                } else indivEvents.add(e);
            }
            eventId = eventList.get(eventList.size() - 1).getId() + 1;
        }
    }

//    public void exportJBinFile(String filename, List<Card> cards2) {
//        eventLog.reportCreateJBin();
//        List<Card> cardSet = new ArrayList<>(cards);
//        cardSet.addAll(cards2);
//        IOProcessing.writeJBinFile(filename, JBin.createJBin(cardSet));
//        eventLog.reportWriteJBinFile(filename);
//    }

    public void exportJBinFile(String filename) {
        eventLog.reportCreateJBin();
        IOProcessing.writeJBinFile(
                filename,
                JBin.createJBin(
                        cards,
                        schedule,
                        indivEvents,
                        recurringEvents
                )
        );
        eventLog.reportWriteJBinFile(filename);
    }

    public void setScheduleOption(int idx) {
        scheduler = Scheduler.getInstance(userConfig, eventLog, idx);
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Event> getIndivEvents() {
        return indivEvents;
    }

    public List<List<Event>> getRecurringEvents() {
        return recurringEvents;
    }

    /**
     * Gets last ID for Task
     *
     * @return last Task ID
     */
    public int getLastTaskId() {
        return taskId;
    }

    /**
     * Gets last ID for Day
     *
     * @return last Day ID
     */
    public int getLastDayId() {
        return dayId;
    }

    public void addTaskList(List<Task> list) {
        for(Task t : list) {
            taskMap.put(taskId, t);
            taskManager.add(t);
            eventLog.reportTaskAction(t, 0);
        }
    }

    public void addCardList(List<Card> list) {
        for(Card c : list) {
            cards.add(c);
            eventLog.reportCardAction(c, 0);
        }
    }

    /**
     * Adds an event to the manager
     *
     * @param name name of event
     * @param color color for event classification
     * @param timeStamp event duration
     * @param recurring whether the event occurs only once or not
     * @param days days of event occurrence, if recurring
     * @return newly generated Event
     */
    public Event addEvent(String name, Card.Colors color, Time.TimeStamp timeStamp,
                          boolean recurring, Event.DayOfWeek[] days) {

        Event e;
        if (recurring) {
            e = new Event(
                    eventId,
                    name,
                    color,
                    timeStamp,
                    days
            );
        } else {
            if (days != null) throw new IllegalArgumentException("Event is non-recurring but has recurrent days");
            e = new Event(eventId, name, color, timeStamp);
        }

        if (recurring) {
            for (int i = 0; i < e.getDays().length; i++) {
                recurringEvents.get(e.getDays()[i].ordinal()).add(e);
            }
        } else indivEvents.add(e);

        eventLog.reportEventAction(e, 0);
        return e;
    }

    /**
     * Adds a task to the schedule
     *
     * @param name name of Task
     * @param hours number of hours for Task
     * @param incrementation number of days till due date for Task
     * @return newly generated Task
     */
    public Task addTask(String name, int hours, int incrementation) {
        Task task = new Task(taskId++, name, hours, incrementation);

        taskManager.add(task);
        taskMap.put(taskId - 1, task);

        eventLog.reportTaskAction(task, 0);
        cards.get(0).addTask(task);
        return task;
    }

    public void addTask(Task createdTask) {
        taskManager.add(createdTask);
        taskMap.put(taskId - 1, createdTask);
        cards.get(0).addTask(createdTask);
    }

    public boolean addTaskToCard(Task task, Card card) {
        if (card.getTask().contains(task)) return false;
        card.addTask(task);
        if (cards.get(0).getTask().contains(task)) {
            cards.get(0).removeTask(task);
        }
        return true;
    }

    /**
     * Removes a task from the schedule given the day and task indices
     *
     * @param t1 task being removed
     * @return boolean status for successful removal
     */
    public boolean removeTask(Task t1) {
        if(taskManager.contains(t1)) {
            taskManager.remove(t1);
            taskMap.remove(t1.getId(), t1);
            eventLog.reportTaskAction(t1, 1);
            return true;
        }

        return false;
    }

    /**
     * Edits a task from the schedule given the day and task indices
     *
     * @param t1 task being edited
     * @param hours number of hours to be assigned
     * @param incrementation number of days till due date
     * @return newly edited Task
     */
    public Task editTask(Task t1, int hours, int incrementation) {
        if(!taskManager.contains(t1) || hours <= 0 || incrementation < 0) {
            return null;
        }
        removeTask(t1);
        Task t2 = addTask(t1.getName(), hours, incrementation);
        eventLog.reportTaskAction(t2, 2);
        return t2;
    }

    /**
     * Gets a Task from the schedule
     *
     * @param taskId ID for task
     * @return Task from schedule
     */
    public Task getTask(int taskId) {
        if(!taskMap.containsKey(taskId)) {
            return null;
        }
        return taskMap.get(taskId);
    }

    /**
     * Generates an entire schedule following a distributive approach
     */
    public void buildSchedule() {
        eventLog.reportSchedulingStart();
        resetSchedule();
        //Tasks that are "finished scheduling" are added here
        PriorityQueue<Task> complete = new PriorityQueue<>();

        schedule = new ArrayList<>(userConfig.getMaxDays());
        scheduleTime = Calendar.getInstance();
        int idx = scheduleTime.get(Calendar.DAY_OF_WEEK) - 1;
        int dayCount = 0;
        Day currDay;

        // need to archive tasks that are 'past due' (this is to handle edge case where we started at 11PM, and now it's 1AM)
        Task head = taskManager.peek();
        // note: while incredibly unlikely, if the user let the program run for a month nonstop, it would result in some archived tasks being scheduled
        // the scheduler assumes it is being given valid data to schedule (due today or later)
        while (head != null && head.getDueDate().compareTo(scheduleTime) < 0
                && head.getDueDate().get(Calendar.DAY_OF_MONTH) != scheduleTime.get(Calendar.DAY_OF_MONTH)) {
            archivedTasks.add(taskManager.remove());
        }

        int eventIdx = 0;
        while(!taskManager.isEmpty() && dayId < userConfig.getMaxDays()) {
            currDay = new Day(dayId++, userConfig.getWeek()[idx++ % 7], dayCount++);
            schedule.add(currDay);

            if (!recurringEvents.get(currDay.getDate().get(Calendar.DAY_OF_WEEK) - 1).isEmpty()) {
                for (Event e1 : recurringEvents.get(currDay.getDate().get(Calendar.DAY_OF_WEEK) - 1)) {
                    currDay.addEvent(e1);
                }
            }

            while (eventIdx < indivEvents.size()) {
                Calendar eventDate = indivEvents.get(eventIdx).getTimeStamp().getStart();
                Calendar dayDate = currDay.getDate();
                if (eventDate.get(Calendar.YEAR) == dayDate.get(Calendar.YEAR) && eventDate.get(Calendar.MONTH) == dayDate.get(Calendar.MONTH)
                        && eventDate.get(Calendar.DAY_OF_MONTH) == dayDate.get(Calendar.DAY_OF_MONTH)) {
                    currDay.addEvent(indivEvents.get(eventIdx));
                    eventIdx++;
                } else break;
            }

            // don't need incomplete as argument (should be local to schedulers)
            errorCount = scheduler.assignDay(currDay, errorCount, complete, taskManager, scheduleTime);
        }
        this.taskManager = complete;
        eventLog.reportSchedulingFinish();
    }

    /**
     * Resets all the tasks as well as the entire schedule for it to be regenerated
     */
    private void resetSchedule() {
        schedule = new LinkedList<>();
        PriorityQueue<Task> copy = new PriorityQueue<>();
        while(!taskManager.isEmpty()) {
            Task task = taskManager.remove();
            task.reset();
            copy.add(task);
        }
        taskManager = copy;
        errorCount = 0;
        dayId = 0;
        Collections.sort(indivEvents);
    }

    /**
     * Creates a CheckList for a particular Task
     *
     * @param t1 task being utilized
     * @param title title for the Item
     * @return newly created CheckList
     */
    public CheckList createTaskCheckList(Task t1, String title) {
        CheckList cl = t1.addCheckList(0, title);
        eventLog.reportCheckListCreation(cl);
        return cl;
    }

    public CheckList removeTaskCheckList(Task t1) {
        CheckList cl = t1.removeCheckList();
        if(cl != null) {
            eventLog.reportCheckListRemoval(cl);
        }
        return cl;
    }

    /**
     * Adds a CheckList Item for a Task
     *
     * @param t1 task being utilized
     * @param description description info for Item
     * @return boolean status for successful add
     */
    public boolean addTaskCheckListItem(Task t1, String description) {
        boolean status = t1.addItem(description);
        if(status) {
            eventLog.reportCheckListAction(t1.getCheckList(), t1.getCheckList().size() - 1, 1);
        }
        return status;
    }

    /**
     * Removes a CheckList Item from a Task
     *
     * @param t1 task being utilized
     * @param itemIdx index for Item
     * @return Item removed from CheckList
     */
    public CheckList.Item removeTaskCheckListItem(Task t1, int itemIdx) {
        eventLog.reportCheckListAction(t1.getCheckList(), itemIdx, 0);
        return t1.removeItem(itemIdx);
    }

    /**
     * Shifts an Item in the CheckList
     *
     * @param t1 task being utilized
     * @param itemIdx index for Item
     * @param shiftIdx index for updated position
     * @return boolean status for successful shift
     */
    public boolean shiftTaskItem(Task t1, int itemIdx, int shiftIdx) {
        eventLog.reportCheckListAction(t1.getCheckList(), itemIdx, 4);
        return t1.shiftItem(itemIdx, shiftIdx);
    }

    /**
     * Marks a Task item as complete or incomplete
     *
     * @param t1 task being utilized
     * @param itemIdx index for Item
     * @param flag boolean status for completion
     */
    public void markTaskItem(Task t1, int itemIdx, boolean flag) {
        t1.markItem(itemIdx, flag);
        if(flag) {
            eventLog.reportCheckListAction(t1.getCheckList(), itemIdx, 2);
        } else {
            eventLog.reportCheckListAction(t1.getCheckList(), itemIdx, 2);
        }
    }

    /**
     * Gets a Task Item
     *
     * @param t1      task being utilized
     * @param itemIdx index for Item
     * @return Task Item
     */
    public CheckList.Item getTaskItem(Task t1, int itemIdx) {
        return t1.getItem(itemIdx);
    }

    /**
     * Gets Task CheckList in String format
     *
     * @param t1 task being utilized
     * @return String formatted CheckList
     */
    public String getTaskStringCheckList(Task t1) {
        return t1.getStringCheckList();
    }

    /**
     * Resets Task CheckList
     *
     * @param t1 task being utilized
     * @return boolean status for successful reset
     */
    public boolean resetTaskCheckList(Task t1) {
        eventLog.reportCheckListReset(t1.getCheckList());
        return t1.resetCheckList();
    }

    public String buildBoardString() {
        return TableFormatter.formatBoardTable(cards, userConfig, archivedTasks, userConfig.isFormatPrettyTable());
    }

    public String buildScheduleStr() {
        return TableFormatter.formatScheduleTable(schedule, userConfig, userConfig.isFormatPrettyTable());
    }

    public String buildEventStr() {
        return TableFormatter.formatEventSetTables(recurringEvents, indivEvents, userConfig.isFormatPrettyTable());
    }

    public String buildSubTaskStr() {
        return TableFormatter.formatSubTaskTable(schedule, userConfig.isFormatPrettyTable());
    }

    /**
     * Determines whether the schedule is empty
     *
     * @return boolean value for whether schedule is empty
     */
    public boolean scheduleIsEmpty() {
        return schedule.isEmpty();
    }

    /**
     * Shuts down the system
     */
    public void quit() {
        eventLog.reportExitSession();
        System.exit(0);
    }

    public boolean createCard(String title) {
        eventLog.reportCardAction(null, 0);
        return false;
    }

    public int getLastLabelId() {
        return labelId;
    }

    public int getLastCLId() {
        return checklistId;
    }

    public int getLastCardId() {
        return cardId;
    }

    public List<Day> getSchedule() {
        return schedule;
    }

    public EventLog getEventLog() {
        return eventLog;
    }

    public void exportScheduleToExcel(String filename) throws IOException {
        spreadsheetIO.setFilename(filename);
        spreadsheetIO.exportScheduleToExcel(schedule, cards, archivedTasks, userConfig);
    }

    public void exportScheduleToGoogle() throws IOException {
        googleCalendarIO.exportScheduleToGoogle(userConfig, schedule);
    }

    public void cleanGoogleSchedule() throws IOException {
        googleCalendarIO.cleanGoogleSchedule();
    }

    public void importScheduleFromGoogle() throws IOException {
        googleCalendarIO.importScheduleFromGoogle();
    }

    public void resetData() {
        singleton = new ScheduleManager();
    }
}
