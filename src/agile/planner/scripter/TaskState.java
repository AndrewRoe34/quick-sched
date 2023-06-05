package agile.planner.scripter;

import agile.planner.data.Task;

import java.util.InputMismatchException;

/**
 * Class --> task: [name:string], [hours:int], [days:int] <br>
 * Creates a new instance of a Task while utilizing dynamic variable usage
 *
 * @author Andrew Roe
 */
public class TaskState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 3, ",");
        try {
            taskList.add(new Task(scheduleManager.getLastTaskId() + taskList.size(), tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
            System.out.println("Task added.. [T" + (scheduleManager.getLastTaskId() + taskList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InputMismatchException("Invalid input. Expected[task: <name: string>, <hours: int>, <num_days: int>]");
        }
    }
}
