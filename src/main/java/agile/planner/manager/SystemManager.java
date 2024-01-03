package agile.planner.manager;

import agile.planner.data.Task;
import agile.planner.util.CheckList;

import java.io.FileNotFoundException;

/**
 * Primary controller for all the interacting parts of AGILE Planner
 *
 * @author Andrew Roe
 */
public class SystemManager {

    /** Single static instance of ScheduleManager */
    private static SystemManager singleton;
    /** Single static instance of ScheduleManager */ //TODO possibly allow multiple schedules
    private static ScheduleManager scheduleManager;

    /**
     * Primary constructor for SystemManager
     *
     * @throws FileNotFoundException if EventLog file cannot be found
     */
    private SystemManager() throws FileNotFoundException {
        scheduleManager = ScheduleManager.getScheduleManager();
    }

    /**
     * Gets singleton instance of SystemManager
     *
     * @return static instance of SystemManager
     * @throws FileNotFoundException if EventLog file cannot be found
     */
    public static SystemManager getSystemManager() throws FileNotFoundException {
        if(singleton == null) {
            singleton = new SystemManager();
        }
        return singleton;
    }

//    private UserConfig readConfigFile() {
//        return null;
//    }

//    private void writeConfigFile() { TODO as I just said
//
//    }

//    public void readScheduleData(String filename) { TODO will do later...
//
//    }
//
//    public void writeScheduleData(String filename) {
//
//    }

    /**
     * Adds a task to the schedule
     *
     * @param name name of task
     * @param hours number of hours for Task
     * @param incrementation number of days till due date for Task
     * @return newly generated Task
     */
    public Task addTask(String name, int hours, int incrementation) {
        return scheduleManager.addTask(name, hours, incrementation);
    }

    /**
     * Removes a task from the schedule given the day and task indices
     *
     * @param t1 task being removed
     * @return boolean status for successful removal
     */
    public boolean removeTask(Task t1) {
        return scheduleManager.removeTask(t1);
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
        return scheduleManager.editTask(t1, hours, incrementation);
    }

    /**
     * Gets a Task from the schedule
     *
     * @param taskId ID of Task
     * @return Task from schedule
     */
    public Task getTask(int taskId) {
        return scheduleManager.getTask(taskId);
    }

    /**
     * Generates an entire schedule following a distributive approach
     */
    public void buildSchedule() {
        scheduleManager.buildSchedule();
    }

    /**
     * Outputs the current day's schedule to console
     */
    public void outputCurrentDayToConsole() {
        scheduleManager.outputCurrentDayToConsole();
    }

    /**
     * Outputs the total schedule to console
     */
    public void outputScheduleToConsole() {
        scheduleManager.outputScheduleToConsole();
    }

    /**
     * Outputs the schedule to the specified file
     *
     * @param filename file to be outputted to
     */
    public void outputScheduleToFile(String filename) {
        scheduleManager.outputScheduleToFile(filename);
    }

    /**
     * Creates a CheckList for a particular Task
     *
     * @param t1    task being utilized
     * @param title title for the Item
     * @return newly created CheckList
     */
    public CheckList createTaskCheckList(Task t1, String title) {
        return scheduleManager.createTaskCheckList(t1, title);
    }

    /**
     * Adds a CheckList Item for a Task
     *
     * @param t1 task being utilized
     * @param description description info for Item
     * @return boolean status for successful add
     */
    public boolean addTaskCheckListItem(Task t1, String description) {
        return scheduleManager.addTaskCheckListItem(t1, description);
    }

    /**
     * Removes a CheckList Item from a Task
     *
     * @param t1      task being utilized
     * @param itemIdx index for Item
     * @return Item removed from CheckList
     */
    public CheckList.Item removeTaskCheckListItem(Task t1, int itemIdx) {
        return scheduleManager.removeTaskCheckListItem(t1, itemIdx);
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
        return scheduleManager.shiftTaskItem(t1, itemIdx, shiftIdx);
    }

    /**
     * Gets a Task Item
     *
     * @param t1 task being utilized
     * @param itemIdx index for Item
     * @param checkmark boolean value to indicate checkmark
     */
    public void markItem(Task t1, int itemIdx, boolean checkmark) {
        t1.markItem(itemIdx, checkmark);
    }

    /**
     * Gets Task CheckList in String format
     *
     * @param t1 task being utilized
     * @return String formatted CheckList
     */
    public String getTaskStringCheckList(Task t1) {
        return scheduleManager.getTaskStringCheckList(t1);
    }

    /**
     * Resets Task CheckList
     *
     * @param t1 task being utilized
     * @return boolean status for successful reset
     */
    public boolean resetTaskCheckList(Task t1) {
        return scheduleManager.resetTaskCheckList(t1);
    }

    /**
     * Shuts down the system
     */
    public void quit() {
        scheduleManager.quit();
    }
}
