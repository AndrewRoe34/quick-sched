package agile.planner.scripter;

import agile.planner.data.Label;
import agile.planner.scripter.exception.InvalidGrammarException;

/**
 * Class --> label: [name:string], [color:int] <br>
 * Creates a new instance of a Label while utilizing dynamic variable usage
 *
 * @author Andrew Roe
 */
public class LabelState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 2, ",");
        try {
            labelList.add(new Label(scheduleManager.getLastLabelId() + labelList.size(), tokens[0], Integer.parseInt(tokens[1])));
            System.out.println("Label added.. [L" + (scheduleManager.getLastLabelId() + labelList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[label: <name: string>, <color: int>]");
        }
    }
}
