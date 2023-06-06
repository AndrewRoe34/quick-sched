package agile.planner.scripter;

import agile.planner.data.Task;
import agile.planner.exception.InvalidGrammarException;
import agile.planner.util.CheckList;

public class CheckListState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 1, ",");
        try {
            clList.add(new CheckList(scheduleManager.getLastCLId() + clList.size(), tokens[0]));
            System.out.println("Task added.. [T" + (scheduleManager.getLastCLId() + clList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[checklist: <name: string>]");
        }
    }
}
