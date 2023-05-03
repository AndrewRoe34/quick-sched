package agile.planner.scripter;

public class TaskState extends BaseState {

    /**
     * Processes the Task function:
     * task: [name: String], [number of hours: int], [number of days: int]
     *
     * @param line code line being processed
     */
    public void processFunction(String line) {
        processToken(line);
        processToken(line);
        processToken(line);
        //this should be null
        processToken(line);
        //create the task

        //TODO switch back to BaseState
    }
}
