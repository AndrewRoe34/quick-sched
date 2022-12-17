package agile.planner.ui;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

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

    private SimpleUI() throws FileNotFoundException {
        scheduleManager = ScheduleManager.getSingleton(null,false);
    }

    public static SimpleUI getSingleton() throws FileNotFoundException {
        if(singleton == null) {
            singleton = new SimpleUI();
        }
        return singleton;
    }

    public void outputHeader() {
        System.out.println("Welcome to AGILE Planner 0.2.0\n\n"
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
                System.out.println("list: list of commands\nbuild: builds schedule\ntime: current time\nadd: adds a task\n" +
                        "remove: removes a task\nedit: edits a task\nv_day: outputs day\nv_week: outputs week\nwrite: writes to file\n" +
                        "read: reads data\nquit: exits system");
            } else if("time".equals(input)) {
                System.out.println(sdf.format(Calendar.getInstance().getTime()));
            } else if("v_week".equals(input)) {
                scheduleManager.outputScheduleToConsole();
            } else if("add".equals(input)) {
                System.out.print("Name, Hours, Days_Till_Due: ");
                String name = strScanner.next();
                int hours = strScanner.nextInt();
                int dueDate = strScanner.nextInt();
                scheduleManager.addTask(name, hours, dueDate);
            } else if("remove".equals(input)) {
                System.out.print("Day_Number, Task_Number: ");
                int dayIndex = strScanner.nextInt();
                int taskIndex = strScanner.nextInt();
                if(scheduleManager.removeTask(dayIndex, taskIndex) == null) {
                    System.out.println("Invalid command");
                }
            } else if("edit".equals(input)) {
                System.out.print("Day_Number, Task_Number, Hours, Days_Till_Due: ");
                int dayIdx = strScanner.nextInt();
                int taskIdx = strScanner.nextInt();
                int hours = strScanner.nextInt();
                int incrementation = strScanner.nextInt();
                scheduleManager.editTask(dayIdx, taskIdx, hours, incrementation);
            } else if("v_day".equals(input)) {
                scheduleManager.outputCurrentDayToConsole();
            } else if("build".equals(input)) {
                scheduleManager.buildSchedule();
            } else if("write".equals(input)) {
                System.out.print("Filename: ");
                String filename = strScanner.next();
                scheduleManager.outputScheduleToFile("output/" + filename);
            } else if("read".equals(input)) {
                System.out.print("Filename: ");
                String filename = strScanner.next();
                scheduleManager.processTasks(filename);
            } else if("quit".equals(input)) {
                scheduleManager.quit();
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