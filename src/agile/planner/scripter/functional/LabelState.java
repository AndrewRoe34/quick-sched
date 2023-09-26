package agile.planner.scripter.functional;

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
            int id = scheduleManager.getLastLabelId() + labelList.size();
            String name = tokens[0];
            int color = Integer.parseInt(tokens[1]);
            labelList.add(new Label(id, name, color));
//            scriptLog.reportLabelCreation(id, name, color);
            System.out.println("Label added.. [L" + (scheduleManager.getLastLabelId() + labelList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[label: <name: string>, <color: int>]");
        }
    }
}
