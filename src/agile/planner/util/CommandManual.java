package agile.planner.util;

import java.util.HashMap;

/**
 * Handles all the command manual data and its retrieval by the client
 *
 * @author Andrew Roe
 */
public class CommandManual {

    /** Singleton of Commands */
    private static CommandManual singleton;
    /** HashMap of the command sheet manual */
    private HashMap<String, String> commandManual;

    private CommandManual() {
        buildCommandManual();
    }

    /**
     * Builds the commandManual HashMap
     */
    private void buildCommandManual() {
        commandManual = new HashMap<>();
        commandManual.put("list", "NAME\n\tlist - lists all possible commands\n\nSYNOPSIS\n\tlist\n\nDESCRIPTION\n\tLists all possible"
                + " commands available within AGILE Planner command line interface");
        commandManual.put("schedule", "NAME\n\tschedule - displays entire schedule\n\nSYNOPSIS\n\tschedule\n\nDESCRIPTION\n\tDisplays"
                + " all days and their respective tasks from the schedule. Items in each day are sorted by priority.");
        commandManual.put("time", "NAME\n\ttime - gets current time\n\nSYNOPSIS\n\ttime\n\nDESCRIPTION\n\tGets the"
                + " actual time within the system clock in the format of yyyy-mm-dd HH-MM-SS");
        commandManual.put("add", "NAME\n\tadd - adds a task to schedule\n\nSYNOPSIS\n\tadd [name] [hours] [due_date]\n\nDESCRIPTION\n\tAdds any particular"
                + " task to the schedule assuming no time conflict has occurred");
        commandManual.put("remove", "NAME\n\tremove - removes task from schedule\n\nSYNOPSIS\n\tremove [day_index] [task_index]\n\nDESCRIPTION\n\tRemoves"
                + " any particular task from schedule assuming it exists and is not archived");
        commandManual.put("edit", "NAME\n\tedit - edits a task within the schedule\n\nSYNOPSIS\n\tedit [day_index] [task_index] [hours] [incrementation]\n\nDESCRIPTION\n\tEdits"
                + " a given task within the schedule to possess a valid modification such as hours or due date");
        commandManual.put("day", "NAME\n\tday - views current day schedule\n\nSYNOPSIS\n\tday\n\nDESCRIPTION\n\tViews the"
                + " entire day's schedule with all tasks in sorted order based on priority");
        commandManual.put("log", "NAME\n\tlog - prints a log of all recent commands\n\nSYNOPSIS\n\tlog\n\nDESCRIPTION\n\tViews"
                + " the entire day's logging commands history");
        commandManual.put("print", "NAME\n\tprint - prints the entire schedule\n\nSYNOPSIS\n\tprint [file_name]\n\nDESCRIPTION\n\tPrints the"
                + " entire schedule to the default schedule folder with the title format: schedule-yyyy-mm-dd.txt");
        commandManual.put("read", "NAME\n\tread - reads in the schedule\n\nSYNOPSIS\n\tread [file_name]\n\nDESCRIPTION\n\tReads in the schedule"
                + " from the default folder and overrites the current schedule");
    }

    /**
     * Gets a singleton of Commands
     *
     * @return singleton of Commands
     */
    public static CommandManual getSingleton() {
        if(singleton == null) {
            singleton = new CommandManual();
        }
        return singleton;
    }

    /**
     * Gets the command manual HashMap
     *
     * @return HashMap of command manual
     */
    public HashMap<String, String> getCommandManual() {
        return commandManual;
    }

}