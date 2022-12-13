package agile.planner.ui;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

import agile.planner.util.CommandManual;
import agile.planner.manager.ScheduleManager;

/**
 * Basic Command Line UI
 *
 * @author Andrew Roe
 */
public class SimpleUI {

    /** Singleton of SimpleUI */
    private static SimpleUI singleton;
    /** Manages all scheduling for AGILE Planner */
    private final ScheduleManager scheduleManager;
    /** Holds the manual for all possible commands */
    private final HashMap<String, String> commandManual;

    private SimpleUI() throws FileNotFoundException {
        scheduleManager = ScheduleManager.getSingleton(false);
        commandManual = CommandManual.getSingleton().getCommandManual();
    }

    public static SimpleUI getSingleton() throws FileNotFoundException {
        if(singleton == null) {
            singleton = new SimpleUI();
        }
        return singleton;
    }

    public void outputHeader() {
        System.out.println("Welcome to AGILE Planner 0.2.0\n"
                + "\nChangelog:\n"
                + "-Added overflow notification system for scheduling ease and efficiency\n"
                + "-Added updated command manual operations\n"
                + "-Added the ability to add a task during the current session\n"
                + "-Added the ability to remove a task during the current session\n"
                + "-Added the ability to view just the current day\n"
                + "-Added the ability to process whatever file in \"data\" directory\n"
                + "-Added the ability to output to a specified file\n"
                + "-Added temporary Client email address for terminal prompt (will be incorporating a Client configuration)\n"
                + "-Refactored codebase for ease of use\n"
                + "-Fixed unbalanced task bug that would produce overflow despite ample space\n"
                + "-Fixed overflow bug that prevented overflow from being reported\n"
                + "\n\n"
                + "To output all commands, enter: list\nTo access the manual for a command, enter: man <command>\n");
    }

    /**
     * Executes the primary source of the application with the command line loop
     */
    public void execute() {
        outputHeader();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        Scanner strScanner = new Scanner(System.in);

        while(true) {
            System.out.print("aproe$ ");
            String input = strScanner.next();

            if("list".equals(input)) {
                System.out.println("list\nschedule\ntime\nadd\nremove\nedit\nday\nlog\nprint\nread\nquit");
            } else if("time".equals(input)) {
                System.out.println(sdf.format(Calendar.getInstance().getTime()));
            } else if("v_week".equals(input)) {
                scheduleManager.outputScheduleToConsole();
            } else if("add".equals(input)) {
                String name = strScanner.next();
                int hours = strScanner.nextInt();
                int dueDate = strScanner.nextInt();
                scheduleManager.addTask(name, hours, dueDate);
            } else if("remove".equals(input)) {
                int dayIndex = strScanner.nextInt();
                int taskIndex = strScanner.nextInt();
                if(scheduleManager.removeTask(dayIndex, taskIndex) == null) {
                    System.out.println("Invalid command");
                }
            } else if("edit".equals(input)) {

            } else if("v_day".equals(input)) {
                scheduleManager.outputCurrentDayToConsole();
            } else if("build".equals(input)) {
                scheduleManager.generateSchedule();
            } else if("print".equals(input)) {
                String filename = strScanner.next();
                scheduleManager.outputScheduleToFile("output/" + filename);
            } else if("read".equals(input)) {
                String filename = strScanner.next();
                scheduleManager.processTasks(filename);
            } else if("quit".equals(input)) {
                scheduleManager.quit();
            } else if("man".equals(input)) {
                String command = strScanner.next();
                if(commandManual.containsKey(command)) {
                    System.out.println(commandManual.get(command));
                }
            } else if("custom".equals(input)) {
                scheduleManager.setCustomHours(0, 8);
            } else if("global".equals(input)) {
                scheduleManager.setGlobalHours(0, 8);
            } else {
                System.out.println("Invalid command");
            }
        }
    }

    /**r
     * Starting point of the application from a terminal perspective
     *
     * @param args command line arguments (none used)
     */
    public static void main(String[] args) {

        SimpleUI commandLine = null;
        try {
            commandLine = SimpleUI.getSingleton();
            commandLine.execute();
        } catch (FileNotFoundException e) {
            System.out.println("Could not create ScheduleManager");
        }
    }

}