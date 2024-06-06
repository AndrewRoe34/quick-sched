package com.planner.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.planner.models.*;
import com.planner.io.GoogleCalendarIO;
import com.planner.io.IOProcessing;
import com.planner.schedule.Scheduler;
import com.planner.schedule.day.Day;
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
    private Calendar scheduleTime;
    private List<Event> events;
    private List<Event> indivEvents;
    private List<List<Event>> recurringEvents;

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
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException();
        }
        scheduler = Scheduler.getInstance(userConfig, eventLog, userConfig.getSchedulingAlgorithm());

        schedule = new LinkedList<>();
        customHours = new HashMap<>();
        taskMap = new HashMap<>();
        cards = new ArrayList<>();
        cards.add(new Card(0, "Default", Card.Colors.LIGHT_BLUE));
        archivedTasks = new PriorityQueue<>();
        events = new ArrayList<>();
        indivEvents = new ArrayList<>();

        // Gotta initialize all the lists lol
        recurringEvents = new ArrayList<>(7);
        for (int i = 0; i < 7; i++)
            recurringEvents.add(new ArrayList<>());

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

//    /**
//     * Sets standard hours for a day
//     *
//     * @param day a Day to be modified
//     * @param hours number of hours to be set
//     */
//    public void setGlobalHours(int day, int hours) {
//        if(day >= 0 && day < 7 && hours >= 0 && hours <= 24) {
//            week[day] = hours;
//            eventLog.reportWeekEdit(Time.getFormattedCalendarInstance(day), hours, true);
//        } else {
//            throw new IllegalArgumentException("Invalid data for standard days of week");
//        }
//    }
//
//    /**
//     * Sets custom hours for a single instance of a day
//     *
//     * @param day a Day to be modified
//     * @param hours number of hours to be set
//     */
//    public void setCustomHours(int day, int hours) {
//        if(day >= 0 && hours >= 0 && hours <= 24) {
//            customHours.put(day, hours);
//            eventLog.reportWeekEdit(Time.getFormattedCalendarInstance(day), hours, false);
//        } else {
//            throw new IllegalArgumentException("Invalid data for custom days of week");
//        }
//    }

    private void addEventsToEventList() {
        for (Event e : events) {
            addEventToEventList(e);
        }
    }

    private void addEventToEventList(Event event) {
        if (!event.isRecurring()) {
            indivEvents.add(event);
            return;
        }

        for (String day : event.getDays())
            switch (day.toLowerCase()){
                case "sun":
                    recurringEvents.get(0).add(event);
                    break;
                case "mon":
                    recurringEvents.get(1).add(event);
                    break;
                case "tue":
                    recurringEvents.get(2).add(event);
                    break;
                case "wed":
                    recurringEvents.get(3).add(event);
                    break;
                case "thu":
                    recurringEvents.get(4).add(event);
                    break;
                case "fri":
                    recurringEvents.get(5).add(event);
                    break;
                case "sat":
                    recurringEvents.get(6).add(event);
                    break;
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
            JBin.processJBin(binStr, taskManager, events, cards, schedule, userConfig.getArchiveDays());
            eventLog.reportProcessJBin();
            // remove past tasks from current PQ and archives them
//            Set<Task> set = new HashSet<>();
//            for (Day day : schedule) {
//                for (Task.SubTask subTask : day.getSubTasks()) {
//                    set.add(subTask.getParentTask());
//                }
//            }
//            PriorityQueue<Task> copy = new PriorityQueue<>();
//            int n = taskManager.size();
//            for (int i = 0; i < n; i++) {
//                Task task = taskManager.remove();
//                if (!set.contains(task)) {
//                    archivedTasks.add(task);
//                } else {
//                    copy.add(task);
//                }
//            }
//            taskManager = copy;

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

            addEventsToEventList();
        }
    }

    public void exportJBinFile(String filename, List<Card> cards2) {
        eventLog.reportCreateJBin();
        List<Card> cardSet = new ArrayList<>(cards);
        cardSet.addAll(cards2);
        IOProcessing.writeJBinFile(filename, JBin.createJBin(cardSet));
        eventLog.reportWriteJBinFile(filename);
    }

    public void exportJBinFile(String filename) {
        eventLog.reportCreateJBin();
        IOProcessing.writeJBinFile(filename, JBin.createJBin(cards));
        eventLog.reportWriteJBinFile(filename);
    }

    public void setScheduleOption(int idx) {
        scheduler = Scheduler.getInstance(userConfig, eventLog, idx);
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Event> getEvents() {
        return events;
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
     * Adds an event to the schedule
     *
     * @param name name of event
     * @param color color for event classification
     * @param timeStamp event duration
     * @param recurring whether the event occurs only once or not
     * @param days days of event occurrence, if recurring
     * @return newly generated Task
     */
    public Event addEvent(String name, Card.Colors color, Time.TimeStamp timeStamp,
                          boolean recurring, String[] days) {
        Event e = new Event(
                events.size(),
                name,
                color,
                timeStamp,
                recurring,
                days
        );

        events.add(e);
        addEventToEventList(e);

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

        while(!taskManager.isEmpty() && dayId < userConfig.getMaxDays()) {
            currDay = new Day(dayId++, userConfig.getWeek()[idx++ % 7], dayCount++);
            schedule.add(currDay);
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

    private String getColorANSICode(Card.Colors color) {
        if (color == null) return "\u001B[38;2;3;155;229m";
        // Color Code - \001B[38;2;<r>;<g>;<b>m
        switch (color) {
            case RED:
                return "\u001B[38;2;213;0;0m";
            case ORANGE:
                return "\u001B[38;2;244;81;30m";
            case YELLOW:
                return "\u001B[38;2;246;191;38m";
            case GREEN:
                return "\u001B[38;2;11;128;67m";
            case LIGHT_BLUE:
                return "\u001B[38;2;3;155;229m";
            case BLUE:
                return "\u001B[38;2;63;81;181m";
            case INDIGO:
                return "\u001B[38;2;142;36;170m";
            case VIOLET:
                return "\u001B[38;2;121;134;203m";
            case BLACK:
                return "\u001B[38;2;97;97;97m";
            case LIGHT_GREEN:
                return "\u001B[38;2;51;182;121m";
            case LIGHT_CORAL:
                return "\u001B[38;2;230;124;115m";
            default:
                return "\u001B[38;2;207;211;203m";
        }
    }

    public String buildBoardString() {
        StringBuilder sb = new StringBuilder();

        // use foreach loop to determine max number of tasks while printing out the first line of Cards
        int maxTasks = 0;
        boolean defaultCardIsEmpty = false;
        int cardIdx = 0;
        for(Card c1 : cards) {
            if (cardIdx == 0 && c1.getTask().isEmpty()) {
                cardIdx++;
                defaultCardIsEmpty = true;
                continue;
            }
            if (userConfig.isLocalScheduleColors()) {
                String colorANSICode = getColorANSICode((c1.getColorId()));
                sb.append(colorANSICode);
            }

            maxTasks = Math.max(c1.getTask().size(), maxTasks);
            if(c1.toString().length() > 40)
                sb.append(c1.toString(), 0, 40);
            else {
                sb.append(c1);

                for(int i = c1.toString().length(); i < 40; i++) {
                    sb.append(" ");
                }
            }

            if (userConfig.isLocalScheduleColors())
                sb.append("\u001B[0m");

            sb.append("|");
        }

        sb.append("\n");
        int cardCount = defaultCardIsEmpty ? 1 : 0;
        for(; cardCount < cards.size(); cardCount++) {
            sb.append("-----------------------------------------");
        }

        // use foreach loop inside a for loop to output the tasks
        for(int i = 0; i < maxTasks; i++) {
            sb.append("\n");

            cardIdx = 0;
            for(Card c1 : cards) {
                if (cardIdx++ == 0 && c1.getTask().isEmpty()) continue;

                if(i < c1.getTask().size()) {
                    // print out the task (up to 18 characters)
                    Task t1 = c1.getTask().get(i);
                    String outputTask = "";
//                    if (Time.differenceOfDays(t1.getDueDate(), currDate) < 0) {
//                        outputTask = "*";
//                    }

                    if (isArchivedTask(t1))
                        outputTask = "*";

                    outputTask += t1.toString();

                    if(outputTask.length() > 40)
                        sb.append(outputTask, 0, 40);
                    else {
                        sb.append(outputTask);
                        for(int j = outputTask.length(); j < 40; j++) {
                            sb.append(" ");
                        }
                    }

                    sb.append("|");
                }
                else
                    sb.append("                                        |");
            }
        }
        sb.append("\n");

        return sb.toString();
    }

    public String buildScheduleStr() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar date = Time.getFormattedCalendarInstance(0);

        int maxTasks = 0;
        for (int i = 0; i < Math.min(schedule.size(), 6); i++) {
            sb.append(sdf.format(date.getTime()));
            sb.append("                              |");
            maxTasks = Math.max(maxTasks, schedule.get(i).getNumSubTasks());
            date = Time.getFormattedCalendarInstance(date, 1);
        }

        sb.append("\n");
        for (int i = 0; i < Math.min(schedule.size(), 6); i++) {
            sb.append("-----------------------------------------");
        }

        for (int i = 0; i < maxTasks; i++) {
            sb.append("\n");

            for (int d = 0; d < Math.min(schedule.size(), 6); d++) {
                Day day = schedule.get(d);

                if (i < day.getNumSubTasks()) {
                    Task.SubTask subTask = day.getSubTask(i);

                    if (userConfig.isLocalScheduleColors()) {
                        String colorANSICode = getColorANSICode((subTask.getParentTask().getColor()));
                        sb.append(colorANSICode);
                    }

                    sb.append(day.getTaskTimeStamps().get(i)).append(" - "); // 18 char
                    String outputSubTask = subTask.getParentTask().getName();

                    if (outputSubTask.length() > 21) {
                        sb.append(outputSubTask, 0, 21);
                    } else {
                        sb.append(outputSubTask);
                        sb.append(" ".repeat(22 - outputSubTask.length()));
                    }

                    if (userConfig.isLocalScheduleColors())
                        sb.append("\u001B[0m");

                    sb.append("|");
                } else {
                    sb.append("                                        |");
                }
            }
        }
        sb.append("\n");

        return sb.toString();
    }

    public String buildEventStr() {
        StringBuilder sb = new StringBuilder();

        boolean recurringEventsExist = false;
        for (List<Event> events : recurringEvents) {
            if (!events.isEmpty()) {
                recurringEventsExist = true;
                break;
            }
        }

        if (recurringEventsExist) {
            sb.append("RECURRING:").append("\n");
            sb.append("ID").append(" ".repeat(5)).append("|");
            sb.append("NAME").append(" ".repeat(16)).append("|");
            sb.append("COLOR").append(" ".repeat(10)).append("|");
            sb.append("TIME").append(" ".repeat(16)).append("|");
            sb.append("DAYS").append(" ".repeat(29)).append("|");

            sb.append("\n");
            sb.append("-".repeat(100));
            sb.append("\n");
        }

        HashSet<Event> uniqueRecurringEvents = new HashSet<>();
        for (List<Event> events : recurringEvents) {
            uniqueRecurringEvents.addAll(events);
        }

        for (Event e : uniqueRecurringEvents) {
            sb.append(e.getId()).append(" ".repeat(7 - String.valueOf(e.getId()).length())).append("|");

            if (e.getName().length() > 20)
                sb.append(e.getName(), 0, 20).append("|");
            else
                sb.append(e.getName()).append(" ".repeat(20 - e.getName().length())).append("|");

            sb.append(e.getColor()).append(" ".repeat(15 - String.valueOf(e.getColor()).length())).append("|");

            sb.append(e.getTimeStamp().toString()).append(" ".repeat(
                    20 - e.getTimeStamp().toString().length())
            ).append("|");

            sb.append(Arrays.toString(
                            e.getDays()),
                    1,
                    Arrays.toString(e.getDays()).length() - 1
            ).append(" ".repeat(33 - Arrays.toString(e.getDays()).length() + 2)).append("|");

            sb.append("\n");
        }

        sb.append("\n");

        if (!indivEvents.isEmpty()) {
            sb.append("INDIVIDUAL:").append("\n");
            sb.append("ID").append(" ".repeat(5)).append("|");
            sb.append("NAME").append(" ".repeat(16)).append("|");
            sb.append("COLOR").append(" ".repeat(10)).append("|");
            sb.append("TIME").append(" ".repeat(16)).append("|");
            sb.append("DATE").append(" ".repeat(8)).append("|");

            sb.append("\n");
            sb.append("-".repeat(79));
            sb.append("\n");
        }

        for (Event e : indivEvents) {
            sb.append(e.getId()).append(" ".repeat(7 - String.valueOf(e.getId()).length())).append("|");

            if (e.getName().length() > 20)
                sb.append(e.getName(), 0, 20).append("|");
            else
                sb.append(e.getName()).append(" ".repeat(20 - e.getName().length())).append("|");

            sb.append(e.getColor()).append(" ".repeat(15 - String.valueOf(e.getColor()).length())).append("|");
            sb.append(e.getTimeStamp().toString()).append(" ".repeat(20 - e.getTimeStamp().toString().length())).append("|");
            sb.append(e.getDateString()).append(" ".repeat(12 - e.getDateString().length())).append("|");

            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Outputs the current day's schedule to console
     */
    public void outputCurrentDayToConsole() {
        if(schedule.isEmpty()) {
            System.out.println("Schedule is empty");
        } else {
            IOProcessing.outputDay(schedule.get(0), errorCount, null);
            eventLog.reportDisplayDaySchedule(schedule.get(0));
        }
    }

    /**
     * Outputs the total schedule to console
     */
    public void outputScheduleToConsole() {
        if(schedule.isEmpty()) {
            System.out.println("Schedule is empty");
        } else {
            IOProcessing.outputSchedule(schedule, errorCount, null);
            eventLog.reportDisplaySchedule(schedule.size(), taskManager.size(), true);
        }
    }

    /**
     * Outputs the schedule to the specified file
     *
     * @param filename file to be outputted to
     */
    public void outputScheduleToFile(String filename) {
        try {
            PrintStream output = new PrintStream(filename);
            IOProcessing.outputSchedule(schedule, errorCount, output);
            output.close();
            eventLog.reportDisplaySchedule(schedule.size(), taskManager.size(), false);
        } catch (FileNotFoundException e) {
            eventLog.reportException(e);
            System.out.println("Error with processing file");
        }
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

    public void exportScheduleToGoogle() throws IOException {
        googleCalendarIO.cleanGoogleSchedule();
        googleCalendarIO.exportScheduleToGoogle(userConfig, schedule);
    }

    public void importScheduleFromGoogle() throws IOException {
        googleCalendarIO.importScheduleFromGoogle();
    }

    public boolean isArchivedTask(Task t1) {
        return archivedTasks.contains(t1);
    }
}
