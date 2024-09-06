package com.planner.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.planner.io.IOProcessing;
import com.planner.io.SpreadsheetIO;
import com.planner.models.*;
import com.planner.io.GoogleCalendarIO;
import com.planner.schedule.Scheduler;
import com.planner.schedule.day.Day;
import com.planner.util.ReportLog;
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
    /** ID for card */
    private int cardId;
    /** Last day Task is due */
    private int lastDueDate;
    private GoogleCalendarIO googleCalendarIO;
    private SpreadsheetIO spreadsheetIO;
    private Calendar scheduleTime;
    /** List for storing individual events */
    private final List<Event> indivEvents;
    /** List of Lists for storing recurring events.
     * The outer List is of size 7 representing each day of the week.
     * Each element inside the outer List represents a list of events that occur on that day every week.
     * For example, to access the List of events that reoccur on the first day of the week we can use <code>recurringEvents.get(0)</code>*/
    private final List<List<Event>> recurringEvents;
    /** ID for event */
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
        taskId = 0;
        cardId = 0;

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

    /**
     * Gets cards
     *
     * @return List of cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Gets individual events
     *
     * @return List of individual events
     */
    public List<Event> getIndivEvents() {
        return indivEvents;
    }

    /**
     * Gets recurring events
     *
     * @return List of recurring events
     */
    public List<List<Event>> getRecurEvents() {
        return recurringEvents;
    }

    /**
     * Gets user configuration
     *
     * @return <code>UserConfig</code> object that holds all user settings
     */
    public UserConfig getUserConfig() {
        return userConfig;
    }

    /**
     * Gets the number of tasks
     *
     * @return Number of all tasks, including archived ones
     */
    public int getNumTasks() {
        return archivedTasks.size() + taskManager.size();
    }

    /**
     * Gets a priority queue off all non-archived tasks
     *
     * @return Priority queue storing all non-archived tasks in sorted order
     */
    public PriorityQueue<Task> getTaskManager() {
        return taskManager;
    }

    /**
     * Gets a priority queue off all archived tasks
     *
     * @return Priority queue storing all archived tasks in sorted order
     */
    public PriorityQueue<Task> getArchivedTasks() { return archivedTasks; }

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
     * @param dates days of event occurrence, if recurring
     * @return newly generated Event
     */
    public Event addEvent(String name, Card.Color color, Time.TimeStamp timeStamp,
                          boolean recurring, List<Calendar> dates) {
        Event e;

        if (recurring) {
            Event.DayOfWeek[] days = new Event.DayOfWeek[dates.size()];
            Event.DayOfWeek[] dayOfWeekValues = Event.DayOfWeek.values();

            for (int i = 0; i < dates.size(); i++) {
                days[i] = dayOfWeekValues[dates.get(i).get(Calendar.DAY_OF_WEEK) - 1];
            }

            e = new Event(eventId, name, color, timeStamp, days);
            Event.DayOfWeek[] eventDays = e.getDays();

            for (int i = 0; i < eventDays.length; i++) {
                recurringEvents.get(eventDays[i].ordinal()).add(e);
            }
        } else {
            if (dates != null) throw new IllegalArgumentException("Event is non-recurring but has recurrent days");
            e = new Event(eventId, name, color, timeStamp);
            indivEvents.add(e);
        }

        eventLog.reportEventAction(e, 0);

        eventId++;

        return e;
    }

    /**
     * Adds a card to the cards List
     *
     * @param title Title of the card
     * @param color Color of the card
     * @return The new card added
     */
    public Card addCard(String title, Card.Color color) {
        Card card = new Card(cardId, title, color);

        cards.add(card);
        eventLog.reportCardAction(card, 0);

        cardId++;

        return card;
    }

    /**
     * Adds a task to the task manager List
     *
     * @param name Name of the task
     * @param hours Number of hours of the task
     * @param due Due date of the task
     * @param card Card of the taks
     * @return The new task added
     */
    public Task addTask(String name, double hours, Calendar due, Card card) {
        Task task = new Task(taskId, name, hours, due, card);
        // todo check whether task is past due (if so, add to archive list)
        //  will need to add to taskMap
        taskManager.add(task);
        taskId++;

        return task;
    }

    /**
     * Modifies a task
     *
     * @param id ID of the task to be modified
     * @param name New name of the task
     * @param hours New number of hours
     * @param due New due date of the task
     * @param card New Card of the task
     * @return Task after it's modified
     */
    public Task modTask(int id, String name, double hours, Calendar due, Card card) {
        // todo will need error handling here
        Task task = taskMap.get(id);

        if (task == null) {
            return null;
        }

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

    public Card modCard(int id, String name, Card.Color colorId)
    {
        Card card;

        if (id >= 0 && id < cards.size()) {
            card = cards.get(id);
        } else {
            return null;
        }

        if (name != null) {
            card.setName(name);
        }
        if (colorId != null) {
            card.setColorId(colorId);
        }

        return card;
    }

    public Event modEvent(int id, String name, Card.Color color, Time.TimeStamp timeStamp,
                          List<Calendar> dates)
    {
        Event event = findEvent(id);

        if (event == null) {
            return null;
        }

        if (dates != null && !event.isRecurring()) {
            throw new IllegalArgumentException("Error: Individual event can't be assigned to multiple days");
        }

        if (name != null) {
            event.setName(name);
        }
        if (color != null) {
            event.setColor(color);
        }
        if (timeStamp != null) {
            event.setTimeStamp(timeStamp);
        }
        if (dates != null) {
            Event.DayOfWeek[] days = new Event.DayOfWeek[dates.size()];
            Event.DayOfWeek[] dayOfWeekValues = Event.DayOfWeek.values();

            for (int i = 0; i < dates.size(); i++) {
                days[i] = dayOfWeekValues[dates.get(i).get(Calendar.DAY_OF_WEEK) - 1];
            }

            event.setDays(days);
        }

        return event;
    }

    public boolean deleteTask(int id) {
        Task task;

        if (taskMap.containsKey(id)) {
            task = taskMap.get(id);
            taskMap.remove(id);
        } else {
            return false;
        }

        boolean taskRemoved = taskManager.remove(task);

        if (!taskRemoved) {
            taskRemoved = archivedTasks.remove(task);
        }

        return taskRemoved;
    }

    public boolean deleteCard(int id) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId() == id) {
                Card card = cards.get(i);

                cards.remove(i);

                removeTasksWithCard(card);

                return true;
            }
        }

        return false;
    }

    public boolean deleteEvent(int id) {
        for (int i = 0; i < indivEvents.size(); i++) {
            if (indivEvents.get(i).getId() == id) {
                indivEvents.remove(i);
                return true;
            }
        }

        boolean eventRemoved = false;

        for (List<Event> events : recurringEvents)
        {
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getId() == id) {
                    events.remove(i);
                    eventRemoved = true;
                }
            }
        }

        return eventRemoved;
    }

    private void removeTasksWithCard(Card card) {
        taskManager.forEach(task -> {
            if (task.getCard().getId() == card.getId()) {
                task.setCard(null);
            }
        });

        archivedTasks.forEach(task -> {
            if (task.getCard().getId() == card.getId()) {
                task.setCard(null);
            }
        });
    }

    private Event findEvent(int id)
    {
        Event event = null;

        for (Event indivEvent : indivEvents) {
            if (indivEvent.getId() == id) {
                return indivEvent;
            }
        }

        for (List<Event> events : recurringEvents)
        {
            for (Event recurringEvent : events) {
                if (recurringEvent.getId() == id) {
                    return recurringEvent;
                }
            }
        }

        return null;
    }

    /**
     * Gets a Task from the schedule
     *
     * @param taskId ID for task
     * @return Task from schedule
     */
    public Task getTask(int taskId) {
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
        while (head != null && !Time.doDatesMatch(head.getDueDate(), scheduleTime)
                && head.getDueDate().compareTo(scheduleTime) < 0) {
            archivedTasks.add(taskManager.remove());
        }

        int eventIdx = 0;
        while(!taskManager.isEmpty() && dayId < userConfig.getMaxDays()) {
            currDay = new Day(dayId++, userConfig.getHoursPerDayOfWeek()[idx++ % 7], dayCount++);
            schedule.add(currDay);

            if (!recurringEvents.get(currDay.getDate().get(Calendar.DAY_OF_WEEK) - 1).isEmpty()) {
                for (Event e1 : recurringEvents.get(currDay.getDate().get(Calendar.DAY_OF_WEEK) - 1)) {
                    currDay.addEvent(e1);
                }
            }

            while (eventIdx < indivEvents.size()) {
                Calendar eventDate = indivEvents.get(eventIdx).getTimeStamp().getStart();
                Calendar dayDate = currDay.getDate();
                if (Time.doDatesMatch(eventDate, dayDate)) {
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

    /**
     * Builds a schedule in String format
     *
     * @return Schedule table as a String
     */
    public String buildScheduleStr() {
        return TableFormatter.formatScheduleTable(schedule, true);
    }

    /**
     * Builds events in String format
     *
     * @return Events table as a String
     */
    public String buildEventStr() {
        return TableFormatter.formatEventSetTables(recurringEvents, indivEvents, userConfig);
    }

    /**
     * Builds cards in String format
     *
     * @return Cards table as a String
     */
    public String buildCardStr() {
        return TableFormatter.formatCardTable(cards, true);
    }

    /**
     * Builds tasks in String format
     *
     * @return Tasks table as a String
     */
    public String buildTaskStr() {
        return TableFormatter.formatTaskTable(taskManager, archivedTasks, true);
    }

    /**
     * Builds subtasks in String format
     *
     * @return Subtasks table as a String
     */
    public String buildSubTaskStr() {
        return TableFormatter.formatSubTaskTable(schedule, userConfig);
    }

    /**
     * Builds session log in String format
     *
     * @return Session log as a String
     */
    public String buildReportStr() {
        return ReportLog.buildReportLog(this);
    }

    /**
     * Shuts down the system
     */
    public void quit() throws IOException {
        eventLog.reportExitSession();
        IOProcessing.writeSysLogToFile(eventLog.toString());
        System.exit(0);
    }

    /**
     * Gets schedule
     *
     * @return List of days representing a schedule
     */
    public List<Day> getSchedule() {
        return schedule;
    }

    /**
     * Gets event log
     *
     * @return EventLog object that log actions performed by user
     */
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

    public static void main(String[] args) {
        ScheduleManager sm = getScheduleManager();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR, end.get(Calendar.HOUR) + 1);
        Time.TimeStamp timeStamp = new Time.TimeStamp(start, end);

        List<Calendar> dates = new ArrayList<Calendar>();
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        date2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        dates.add(date1);
        dates.add(date2);
        Event.DayOfWeek[] eventDays = new Event.DayOfWeek[]{Event.DayOfWeek.FRI, Event.DayOfWeek.SUN};


        sm.addEvent("holiday", Card.Color.BLUE, timeStamp, true, dates);
    }
}
