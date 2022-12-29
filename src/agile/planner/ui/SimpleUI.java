package agile.planner.ui;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import agile.planner.manager.SystemManager;
import agile.planner.task.Task;

/**
 * Basic Command Line UI
 *
 * @author Andrew Roe
 */
public class SimpleUI {

    /** Singleton of SimpleUI */
    private static SimpleUI singleton;
    /** Manages all scheduling for AGILE Planner */
    private final SystemManager systemManager;

    private SimpleUI() throws FileNotFoundException {
        systemManager = SystemManager.getSystemManager();
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
            System.out.print("user$ ");
            String input = strScanner.next();

            if("list".equals(input)) {
                System.out.println("list: list of commands\n" +
                        "add: adds a task\n" +
                        "task: modifies task\n" +
                        "build: builds schedule\n" +
                        "v_week: outputs week schedule\n" +
                        "v_day: outputs day schedule\n" +
                        "write: writes schedule to file\n" +
                        "read: reads task from file\n" +
                        "quit: quits application");
            } else if("add".equals(input)) {
                System.out.print("Name, Hours, Days_Till_Due: ");
                String name = strScanner.next();
                int hours = strScanner.nextInt();
                int dueDate = strScanner.nextInt();
                systemManager.addTask(name, hours, dueDate);
            } else if("task".equals(input)) {
                System.out.print("Task ID: ");
                int id = strScanner.nextInt();
                Task t1 = systemManager.getTask(id);
                taskInputHandling(t1, strScanner);
            } else if("build".equals(input)) {
                systemManager.buildSchedule(); //TODO
            } else if("v_week".equals(input)) {
                systemManager.outputScheduleToConsole();
            } else if("v_day".equals(input)) {
                systemManager.outputCurrentDayToConsole();
            } else if("write".equals(input)) {
                System.out.print("Filename: ");
                String filename = strScanner.next();
                systemManager.outputScheduleToFile("output/" + filename);
            } else if("read".equals(input)) {
                System.out.print("Filename: ");
                String filename = strScanner.next();
                systemManager.processTaskInputFile(filename);
            } else if("quit".equals(input)) {
                systemManager.quit();
            } else {
                System.out.println("Invalid command");
            }
        }
    }

    public void taskInputHandling(Task t1, Scanner strScanner) {
        System.out.println("Task Options:\nedit\nremove\nchecklist\nexit\n");
        while(true) {
            System.out.print("Input: ");
            String input = strScanner.next();
            if("edit".equals(input)) {
                System.out.print("Hours, DueDate: ");
                int hours = strScanner.nextInt();
                int dueDate = strScanner.nextInt();
                systemManager.editTask(t1, hours, dueDate);
            } else if("remove".equals(input)) {
                systemManager.removeTask(t1);
                break;
            } else if("checklist".equals(input)) {
                taskCheckListHandling(t1, strScanner);
            } else if("exit".equals(input)) {
                break;
            } else {
                System.out.println("Invalid input");
            }
        }
    }

    public void taskCheckListHandling(Task t1, Scanner strScanner) {
        System.out.print("Please enter title: ");
        String title = strScanner.next();
        System.out.println(t1);
        systemManager.createTaskCheckList(t1, title);
        System.out.println("CheckList Options:\nadd\nedit\nremove\nmark\nview\nexit\n");
        while(true) {
            System.out.print("Input: ");
            String input = strScanner.next();
            if("add".equals(input)) {
                System.out.print("Description: ");
                String description = strScanner.next();
                systemManager.addTaskCheckListItem(t1, description);
            } else if("edit".equals(input)) {
                System.out.print("Description: ");
                String description = strScanner.next();
                //TODO need to write method for systemManager
            } else if("remove".equals(input)) {
                System.out.print("Item_ID: ");
                int itemIdx = strScanner.nextInt();
                systemManager.removeTaskCheckListItem(t1, itemIdx - 1);
            } else if("mark".equals(input)) {
                System.out.print("Item_ID, Marking: ");
                int index = strScanner.nextInt();
                String marking = strScanner.next();
                systemManager.markItem(t1, index, "true".equals(marking));
            } else if("view".equals(input)) {
                System.out.println(systemManager.getTaskStringCheckList(t1));
            } else if("exit".equals(input)) {
                break;
            } else {
                System.out.println("Invalid input");
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