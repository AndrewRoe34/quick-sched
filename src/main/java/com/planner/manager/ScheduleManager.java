package com.planner.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.planner.io.SpreadsheetIO;
import com.planner.models.*;
import com.planner.io.GoogleCalendarIO;
import com.planner.schedule.Scheduler;
import com.planner.schedule.day.Day;
import com.planner.util.SessionLog;
import com.planner.ui.tables.TableFormatter;
import com.planner.util.EventLog;
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
    private int cardId;
    /** Last day Task is due */
    private int lastDueDate;
    private GoogleCalendarIO googleCalendarIO;
    private SpreadsheetIO spreadsheetIO;
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
//        try {
//            googleCalendarIO = new GoogleCalendarIO(eventLog);
//            spreadsheetIO = new SpreadsheetIO(eventLog);
//        }
//        catch (GeneralSecurityException | IOException e) {
//            throw new IllegalArgumentException();
//        }

        scheduler = Scheduler.getInstance(userConfig, eventLog, userConfig.getSchedulingAlgorithm());
        // in situations where ScheduleManager is run multiple times after updates to config, this ensures options are set up properly
        scheduler.updateConfig(userConfig);

        schedule = new LinkedList<>();
        customHours = new HashMap<>();
        taskMap = new HashMap<>();
        cards = new ArrayList<>();
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
            eventLog.reportProcessConfig("profile.json");
            String configStr = Files.readString(Paths.get("settings/profile.json"));
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
//        String binStr = IOProcessing.readJBinFile(filename);
//        if(binStr != null) {
//            eventLog.reportReadJBinFile(filename);
//            List<Event> eventList = new ArrayList<>();
//            JBin.processJBin(binStr, taskManager, eventList, eventId, cards, schedule, userConfig.getArchiveDays());
//            eventLog.reportProcessJBin();
//
//            Calendar currDate = Time.getFormattedCalendarInstance(0);
//            while (!taskManager.isEmpty()) {
//                Task task = taskManager.remove();
//                if (task.getDueDate().compareTo(currDate) < 0) {
//                    archivedTasks.add(task);
//                    // eventlog needs to report the archiving of a task
//                    eventLog.reportTaskAction(task, 3);
//                }
//                else {
//                    taskManager.add(task);
//                    break;
//                }
//            }
//            taskId = taskManager.size();
//
//            for (Event e : eventList) {
//                if (e.isRecurring()) {
//                    for (Event.DayOfWeek dayOfWeek : e.getDays()) {
//                        recurringEvents.get(dayOfWeek.ordinal()).add(e);
//                    }
//                } else indivEvents.add(e);
//            }
//            eventId = eventList.get(eventList.size() - 1).getId() + 1;
//        }
    }

    public void exportJBinFile(String filename) {
//        eventLog.reportCreateJBin();
//        IOProcessing.writeJBinFile(
//                filename,
//                JBin.createJBin(
//                        cards,
//                        schedule,
//                        indivEvents,
//                        recurringEvents
//                )
//        );
//        eventLog.reportWriteJBinFile(filename);
    }

//    public void setScheduleOption(int idx) {
//        scheduler = Scheduler.getInstance(userConfig, eventLog, idx);
//    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Event> getIndivEvents() {
        return indivEvents;
    }

    public List<List<Event>> getRecurEvents() {
        return recurringEvents;
    }

    public UserConfig getUserConfig() {
        return userConfig;
    }

    public int getNumTasks() {
        return archivedTasks.size() + taskManager.size();
    }

    public PriorityQueue<Task> getTaskManager() {
        return taskManager;
    }

    public PriorityQueue<Task> getArchivedTasks() {
        return archivedTasks;
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
     * Adds an event to the manager
     *
     * @param name name of event
     * @param color color for event classification
     * @param timeStamp event duration
     * @param recurring whether the event occurs only once or not
     * @param days days of event occurrence, if recurring
     * @return newly generated Event
     */
    public Event addEvent(String name, Card.Color color, Time.TimeStamp timeStamp,
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

    public Card addCard(String title, Card.Color color) {
        Card card = new Card(cards.size(), title, color);

        cards.add(card);
        eventLog.reportCardAction(card, 0);
        return card;
    }

    public Task addTask(String name, double hours, Calendar due, Card card) {
        Task task = new Task(taskManager.size() + 1, name, hours, due, card);
        // todo check whether task is past due (if so, add to archive list)
        //  will need to add to taskMap
        taskManager.add(task);
        return task;
    }

    public Task modTask(int id, String name, double hours, Calendar due, Card card) {
        // todo will need error handling here
        Task task = taskMap.get(id);
        if (name != null) {
            task.setName(name);
        }
        if (hours != -1) {
            task.setTotalHours(hours);
        }
        if (due != null) {
            task.setDueDate(due);
        }
        if (card != null) {
            task.setCard(card);
        }
        return task;
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
     * Determines whether the schedule is empty
     *
     * @return boolean value for whether schedule is empty
     */
    public boolean scheduleIsEmpty() {
        return schedule.isEmpty();
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

    public String buildScheduleStr() {
        return TableFormatter.formatScheduleTable(schedule, true);
    }

    public String buildEventStr() {
        return TableFormatter.formatEventSetTables(recurringEvents, indivEvents, userConfig);
    }

    public String buildCardStr() {
        return TableFormatter.formatCardTable(cards, true);
    }

    public String buildTaskStr() {
        return TableFormatter.formatTaskTable(taskManager, archivedTasks, true);
    }

    public String buildSubTaskStr() {
        return TableFormatter.formatSubTaskTable(schedule, userConfig);
    }

    public String buildReportStr() {
        return SessionLog.buildSessionLog(this);
    }

    /**
     * Shuts down the system
     */
    public void quit() {
        eventLog.reportExitSession();
        System.exit(0);
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
}
