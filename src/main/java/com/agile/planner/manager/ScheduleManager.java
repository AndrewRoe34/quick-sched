package com.agile.planner.manager;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.Calendar;

import com.agile.planner.data.Card;
import com.agile.planner.data.Label;
import com.agile.planner.io.IOProcessing;
import com.agile.planner.schedule.CompactScheduler;
import com.agile.planner.schedule.DynamicScheduler;
import com.agile.planner.schedule.Scheduler;
import com.agile.planner.schedule.day.Day;
import com.agile.planner.data.Task;
import com.agile.planner.user.UserConfig;
import com.agile.planner.util.CheckList;
import com.agile.planner.util.EventLog;
import com.agile.planner.util.JBin;
import com.agile.planner.util.Time;

/**
 * Handles the generation and management of the overall schedule
 *
 * @author Andrew Roe
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
    private List<Label> labels;

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
        switch(userConfig.getSchedulingAlgorithm()) {
            case 0:
                scheduler = DynamicScheduler.getSingleton(userConfig, eventLog);
                break;
            case 1:
                scheduler = CompactScheduler.getSingleton(userConfig, eventLog);
                break;
            default:
                //do nothing
        }
        schedule = new LinkedList<>();
        customHours = new HashMap<>();
        taskMap = new HashMap<>();
        cards = new ArrayList<>();
        labels = new ArrayList<>();
        archivedTasks = new PriorityQueue<>();
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
            userConfig = IOProcessing.readCfg(null);
            eventLog.reportUserConfigAttr(userConfig);
        } catch (FileNotFoundException e) {
            eventLog.reportException(e);
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

    /**
     * Imports JBin file to generate cards and possible schedule
     *
     * @param filename jbin filename
     */
    public void importJBinFile(String filename) {
        String binStr = IOProcessing.readJBinFile(filename);
        if(binStr != null) {
            eventLog.reportReadJBinFile(filename);
            JBin.processJBin(binStr, taskManager, cards, labels, userConfig.getMaxDays());
            eventLog.reportProcessJBin();
            // remove past tasks from current PQ and archives them
            Calendar curr = Time.getFormattedCalendarInstance(0);
            int n = taskManager.size();
            for(int i = 0; i < n; i++) {
                if(taskManager.peek() != null && taskManager.peek().getDueDate().compareTo(curr) < 0) {
                    archivedTasks.add(taskManager.remove()); //todo need to test as well as add EventLog method to document this action (also, need to discard tasks from cards beyond a specific date)
                } else {
                    break;
                }
            }
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
        if(idx == 1 && scheduler instanceof CompactScheduler) {
            scheduler = DynamicScheduler.getSingleton(userConfig, eventLog);
        } else if(idx == 2 && scheduler instanceof DynamicScheduler) {
            scheduler = CompactScheduler.getSingleton(userConfig, eventLog);
        }
    }

    /**
     * Processes the Tasks from the given file
     *
     * @param filename file to be processed
     */
    @Deprecated
    public void processTaskInputFile(String filename) {
        try {
            int pqSize = taskManager.size();
            lastDueDate = IOProcessing.readTasks("data/" + filename, taskManager, taskMap, taskId);
            taskId += taskManager.size() - pqSize;
            eventLog.reportProcessTasks(filename);
        } catch (FileNotFoundException e) {
            eventLog.reportException(e);
            System.out.println("File could not be located");
        }
    }

    public List<Card> getCards() {
        return cards;
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
        return task;
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
        
        schedule = new ArrayList<>(14);
        Calendar today = Calendar.getInstance();
        int idx = today.get(Calendar.DAY_OF_WEEK) - 1;
        int dayCount = 0;
        Day currDay;

        while(taskManager.size() > 0 && dayId < userConfig.getMaxDays()) {
            currDay = new Day(dayId++, userConfig.getWeek()[idx++ % 7], dayCount++);
            schedule.add(currDay);
            //TODO don't need incomplete as argument (should be local to schedulers)
            errorCount = scheduler.assignDay(currDay, errorCount, complete, taskManager);
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
        while(taskManager.size() > 0) {
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
}
