package com.planner.manager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

import com.planner.io.IOProcessing;
import com.planner.io.SpreadsheetIO;
import com.planner.models.*;
import com.planner.io.GoogleCalendarIO;
import com.planner.schedule.Scheduler;
import com.planner.schedule.day.Day;
import com.planner.ui.formatters.FormatType;
import com.planner.util.*;
import com.planner.ui.tables.TableFormatter;

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
    /** Performs all scheduling operations for each day */
    private Scheduler scheduler;
    /** Holds all user settings for scheduling purposes */
    private UserConfig userConfig;
    /** Logs all actions performed by user */
    private EventLog eventLog;
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
    public ScheduleManager() {
        try {
            eventLog = EventLog.getEventLog();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Could not locate logging file");
        }
        eventLog.reportUserLogin();
        processUserConfigFile();

        taskManager = new PriorityQueue<>();
//        spreadsheetIO = new SpreadsheetIO(eventLog);

        scheduler = Scheduler.getInstance(userConfig, eventLog, 1);
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

    public void setUserConfig(UserConfig userConfig) {
        if (userConfig == null) {
            throw new IllegalArgumentException("UserConfig cannot be null");
        }
        this.userConfig = userConfig;
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
     * @param cardId id for card
     * @param timeStamp event duration
     * @param recurring whether the event occurs only once or not
     * @param dates days of event occurrence, if recurring
     * @return newly generated Event
     */
    public Event addEvent(String name, Integer cardId, Time.TimeStamp timeStamp, boolean recurring, List<Calendar> dates) {
        Event e;

        Card card = null;
        if (cardId != null) {
            card = getCardById(cardId);
        }

        if (recurring) {
            Event.DayOfWeek[] days = new Event.DayOfWeek[dates.size()];
            Event.DayOfWeek[] dayOfWeekValues = Event.DayOfWeek.values();

            for (int i = 0; i < dates.size(); i++) {
                days[i] = dayOfWeekValues[dates.get(i).get(Calendar.DAY_OF_WEEK) - 1];
            }

            e = new Event(eventId, name, card, timeStamp, days);
            Event.DayOfWeek[] eventDays = e.getDays();

            for (Event.DayOfWeek eventDay : eventDays) {
                recurringEvents.get(eventDay.ordinal()).add(e);
            }
        } else {
            if (dates != null && dates.size() > 1) {
                throw new IllegalArgumentException("Event is non-recurring but has recurrent days");
            }

            if (dates != null) {
                Calendar start = timeStamp.getStart();
                Calendar end = timeStamp.getEnd();

                start.set(Calendar.DAY_OF_MONTH, dates.get(0).get(Calendar.DAY_OF_MONTH));
                start.set(Calendar.MONTH, dates.get(0).get(Calendar.MONTH));
                start.set(Calendar.YEAR, dates.get(0).get(Calendar.YEAR));

                end.set(Calendar.DAY_OF_MONTH, dates.get(0).get(Calendar.DAY_OF_MONTH));
                end.set(Calendar.MONTH, dates.get(0).get(Calendar.MONTH));
                end.set(Calendar.YEAR, dates.get(0).get(Calendar.YEAR));

                timeStamp = new Time.TimeStamp(start, end);
            }

            e = new Event(eventId, name, card, timeStamp);
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
     * @param cardId ID for card
     * @return The new task added
     */
    public Task addTask(String name, double hours, Calendar due, Integer cardId) {
        Card c = null;
        if (cardId != null) {
            c = getCardById(cardId);
        }
        Task task = new Task(taskId, name, hours, due, c);

        Calendar curr = Calendar.getInstance();
        if (task.getTotalHours() == 0 || (!Time.doDatesMatch(curr, task.getDueDate()) && task.getDueDate().compareTo(curr) < 0)) {
            archivedTasks.add(task);
        } else {
            taskManager.add(task);
        }

        taskMap.put(taskId, task);
        taskId++;

        eventLog.reportTaskAction(task, 0);

        return task;
    }

    /**
     * Modifies a task
     *
     * @param id ID of the task to be modified
     * @param name New name of the task
     * @param hours New number of hours
     * @param due New due date of the task
     * @param cardId ID for card
     * @return Task after it's modified
     */
    public Task modTask(int id, String name, Double hours, Calendar due, Integer cardId) {
        Task task = taskMap.get(id);

        if (task == null) {
            throw new IllegalArgumentException("Could not locate Task " + id + ".");
        }

        if (name != null) {
            task.setName(name);
        }
        if (hours != null) {
            task.setTotalHours(hours);
        }
        if (due != null) {
            task.setDueDate(due);
        }
        if (cardId != null) {
            Card c = getCardById(cardId);
            if (c != null) {
                task.setCard(c);
            }
        }

        Calendar curr = Calendar.getInstance();
        boolean isActive = taskManager.remove(task);
        if (task.getTotalHours() == 0 || (!Time.doDatesMatch(curr, task.getDueDate()) && task.getDueDate().compareTo(curr) < 0)) {
            // check if it's in archived or active
            if (isActive) {
                archivedTasks.add(task);
            }
        } else {
            if (!isActive) {
                archivedTasks.remove(task);
            }
            taskManager.add(task);
        }

        eventLog.reportTaskAction(task, 2);

        return task;
    }

    public Card modCard(int id, String name, Card.Color color) {
        Card card = getCardById(id);

        if (card == null) {
            throw new IllegalArgumentException("Could not locate Card " + id + ".");
        }

        if (name != null) {
            card.setName(name);
        }
        if (color != null) {
            card.setColor(color);
        }

        eventLog.reportCardAction(card, 2);

        return card;
    }

    public Event modEvent(int id, String name, Integer cardId, Calendar[] timeStamp, List<Calendar> dates) {
        Event event = findEvent(id);

        if (event == null) {
            return null;
        }

        if (dates != null && dates.size() > 1 && !event.isRecurring()) {
            throw new IllegalArgumentException("Individual event can't be assigned to multiple days");
        }

        if (dates != null && dates.size() == 1 && !event.isRecurring()) {
            Calendar start = event.getTimeStamp().getStart();
            Calendar end = event.getTimeStamp().getEnd();

            start.set(Calendar.DAY_OF_MONTH, dates.get(0).get(Calendar.DAY_OF_MONTH));
            start.set(Calendar.MONTH, dates.get(0).get(Calendar.MONTH));
            start.set(Calendar.YEAR, dates.get(0).get(Calendar.YEAR));

            end.set(Calendar.DAY_OF_MONTH, dates.get(0).get(Calendar.DAY_OF_MONTH));
            end.set(Calendar.MONTH, dates.get(0).get(Calendar.MONTH));
            end.set(Calendar.YEAR, dates.get(0).get(Calendar.YEAR));
        }

        if (name != null) {
            event.setName(name);
        }
        if (cardId != null) {
            Card card = getCardById(cardId);
            if (card != null) {
                event.setCard(card);
            }
        }
        if (timeStamp != null) {
            Calendar start = timeStamp[0];
            Calendar end = timeStamp[1];

            if (!event.isRecurring() && dates == null) {
                Calendar prevStart = event.getTimeStamp().getStart();
                Calendar prevEnd = event.getTimeStamp().getEnd();

                start.set(Calendar.DAY_OF_MONTH, prevStart.get(Calendar.DAY_OF_MONTH));
                start.set(Calendar.MONTH, prevStart.get(Calendar.MONTH));
                start.set(Calendar.YEAR, prevStart.get(Calendar.YEAR));

                end.set(Calendar.DAY_OF_MONTH, prevEnd.get(Calendar.DAY_OF_MONTH));
                end.set(Calendar.MONTH, prevEnd.get(Calendar.MONTH));
                end.set(Calendar.YEAR, prevEnd.get(Calendar.YEAR));
            }

            event.setTimeStamp(new Time.TimeStamp(start, end));
        }
        if (dates != null && event.isRecurring()) {
            Event.DayOfWeek[] days = new Event.DayOfWeek[dates.size()];
            Event.DayOfWeek[] dayOfWeekValues = Event.DayOfWeek.values();

            for (int i = 0; i < dates.size(); i++) {
                days[i] = dayOfWeekValues[dates.get(i).get(Calendar.DAY_OF_WEEK) - 1];
            }

            event.setDays(days);

            for (List<Event> recurringEvent : recurringEvents) {
                for (int i = 0; i < recurringEvent.size(); i++) {
                    Event e = recurringEvent.get(i);
                    if (e.getId() == event.getId()) {
                        recurringEvent.remove(i);
                        break;
                    }
                }
            }

            Event.DayOfWeek[] eventDays = event.getDays();

            for (Event.DayOfWeek eventDay : eventDays) {
                recurringEvents.get(eventDay.ordinal()).add(event);
            }
        }

        eventLog.reportEventAction(event, 2);

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

        eventLog.reportTaskAction(task, 1);

        return taskRemoved;
    }

    public boolean deleteCard(int id) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId() == id) {
                Card card = cards.get(i);

                cards.remove(i);

                removeTasksWithCard(card);
                removeEventsWithCard(card);

                eventLog.reportCardAction(card, 1);

                return true;
            }
        }

        return false;
    }

    public boolean deleteEvent(int id) {
        for (int i = 0; i < indivEvents.size(); i++) {
            if (indivEvents.get(i).getId() == id) {
                eventLog.reportEventAction(indivEvents.get(i), 2);
                indivEvents.remove(i);
                return true;
            }
        }

        boolean eventRemoved = false;

        Event event = null;
        for (List<Event> events : recurringEvents) {
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getId() == id) {
                    event = events.remove(i);
                    eventRemoved = true;
                }
            }
        }

        if (event != null) {
            eventLog.reportEventAction(event, 1);
        }

        return eventRemoved;
    }

    private void removeTasksWithCard(Card card) {
        for (Task task : taskManager) {
            if (task.getCard() != null && task.getCard().getId() == card.getId()) {
                task.setCard(null);
            }
        }

        for (Task task : archivedTasks) {
            if (task.getCard() != null && task.getCard().getId() == card.getId()) {
                task.setCard(null);
            }
        }
    }

    private void removeEventsWithCard(Card card) {
        for (List<Event> dayEvents : recurringEvents) {
            for (Event e : dayEvents) {
                if (e.getCard() != null && e.getCard().getId() == card.getId()) {
                    e.setCard(null);
                }
            }
        }

        for (Event e : indivEvents) {
            if (e.getCard() != null && e.getCard().getId() == card.getId()) {
                e.setCard(null);
            }
        }
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

    public int getNumActiveTasks() {
        return taskManager.size();
    }

    /**
     * Gets the Card by its ID
     *
     * @param id ID of Card
     * @return Card with matching ID
     */
    public Card getCardById(int id) {
        for (Card c : cards) {
            if (c.getId() == id) return c;
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
    public void buildSchedule() throws IOException {
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
            head = taskManager.peek();
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

    public void serializeScheduleToFile(String filename) throws IOException {
        String data = Serializer.serializeSchedule(cards, new ArrayList<>(taskMap.values()), indivEvents, getRecurEventsList(recurringEvents), schedule);

        IOProcessing.writeSerializationFile(filename, data);

        eventLog.reportSerializingSchedule(filename);
    }

    public void deserializeScheduleFromFile(Path path) throws IOException {
        Serializer.deserializeSchedule(Files.readString(path), this);

        eventLog.reportDeserializingSchedule(String.valueOf(path.getFileName()));
    }

    private List<Event> getRecurEventsList(List<List<Event>> recurringEvents) {
        Set<Event> recurEventsSet = new HashSet<Event>();

        for (List<Event> dayEvents : recurringEvents) {
            recurEventsSet.addAll(dayEvents);
        }

        return new ArrayList<>(recurEventsSet);
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
        scheduler.updateConfig(userConfig);
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
        return TableFormatter.formatEventSetTables(recurringEvents, indivEvents, true);
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
        return TableFormatter.formatSubTaskTable(schedule, true);
    }

    /**
     * Builds session log in String format
     *
     * @return Session log as a String
     */
    public String buildReportStr() {
        return ReportLog.buildReportLog(this);
    }

    public String buildFormatCard(int id) {
        Card card = getCardById(id);

        if (card == null) {
            throw new IllegalArgumentException("Could not locate Card " + id + ".");
        }

        return FormatType.formatCard(card);
    }

    public String buildFormatTask(int id) {
        Task task = taskMap.get(id);

        if (task == null) {
            throw new IllegalArgumentException("Could not locate Task " + id + ".");
        }

        return FormatType.formatTask(task);
    }

    public String buildFormatEvent(int id) {
        Event event = findEvent(id);

        if (event == null) {
            throw new IllegalArgumentException("Could not locate Event " + id + ".");
        }

        return FormatType.formatEvent(event);
    }

    /**
     * Shuts down the system
     */
    public void quit() throws IOException {
        eventLog.reportExitSession();
        IOProcessing.writeSesLogToFile(buildReportStr()); // todo this needs to use a bool to remove the coloring
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

    private void setupGoogle() {
        if (googleCalendarIO == null) {
            try {
                googleCalendarIO = new GoogleCalendarIO(eventLog);
            }
            catch (GeneralSecurityException | IOException e) {
                throw new IllegalArgumentException();
            }
        }
    }

    public void exportScheduleToGoogle() throws IOException {
        setupGoogle();
        googleCalendarIO.exportScheduleToGoogle(userConfig, schedule);
    }

    public void cleanGoogleSchedule() throws IOException {
        setupGoogle();
        googleCalendarIO.cleanGoogleSchedule();
    }

    public void importScheduleFromGoogle() throws IOException {
        setupGoogle();
        googleCalendarIO.importScheduleFromGoogle();
    }

    public static void main(String[] args) {
        ScheduleManager sm = new ScheduleManager();
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

        Parser.parseEvent(new String[]{"event", "true", "\"supply chain class\"", "@", "mon", "wed", "fri", "10-2"});
        Parser.parseEvent(new String[]{"event", "false", "+C4", "@", "3pm-5", "25-12-2024", "\"holiday meal\""});
        Parser.parseEvent(new String[]{"event", "false", "+C4", "@", "3pm-5", "25-12-2024", "3-5", "\"holiday meal\""});

//        sm.addEvent("holiday", Card.Color.BLUE, timeStamp, true, dates);
    }

    public void setSched(List<Day> days) {
        this.schedule = days;
    }
}
