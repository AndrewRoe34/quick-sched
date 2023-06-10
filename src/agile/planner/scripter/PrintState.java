package agile.planner.scripter;

import agile.planner.scripter.exception.InvalidGrammarException;

/**
 * Function --> print: [class] <br>
 * Provides output based on whether it is by class (i.e. all class items) or by
 * variable (which can be dynamic or specified)
 *
 * @author Andrew Roe
 */
public class PrintState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 2, "\\s");
        if("task".equals(tokens[0])) {
            System.out.println(taskList);
        } else if("_task".equals(tokens[0])) {
            if(tokens[1] == null) {
                System.out.println(taskList.get(taskList.size()-1));
            } else {
                System.out.println(taskList.get(Integer.parseInt(tokens[1])));
            }
        } else if("checklist".equals(tokens[0])) {
            System.out.println(clList);
        } else if("_checklist".equals(tokens[0])) {
            if(tokens[1] == null) {
                System.out.println(clList.get(clList.size()-1));
            } else {
                System.out.println(clList.get(Integer.parseInt(tokens[1])));
            }
        } else if("card".equals(tokens[0])) {
            System.out.println(cardList);
        } else if("_card".equals(tokens[0])) {
            if(tokens[1] == null) {
                System.out.println(cardList.get(cardList.size()-1));
            } else {
                System.out.println(cardList.get(Integer.parseInt(tokens[1])));
            }
        } else if("board".equals(tokens[0])) {

        } else if("_board".equals(tokens[0])) {
            if(tokens[1] == null) {

            } else {

            }
        } else if("label".equals(tokens[0])) {
            System.out.println(labelList);
        } else if("_label".equals(tokens[0])) {
            if(tokens[1] == null) {
                System.out.println(labelList.get(labelList.size()-1));
            } else {
                System.out.println(labelList.get(Integer.parseInt(tokens[1])));
            }
        } else if("day".equals(tokens[0])) {
            System.out.println(dayList);
        } else if("_day".equals(tokens[0])) {
            if(tokens[1] == null) {
                System.out.println(dayList.get(dayList.size()-1));
            } else {
                System.out.println(dayList.get(Integer.parseInt(tokens[1])));
            }
        } else {
            throw new InvalidGrammarException("Invalid input. Expected[print: <class>]");
        }
    }
}
