package agile.planner.scripter;

import agile.planner.data.Board;
import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.scripter.exception.InvalidFunctionException;
import agile.planner.scripter.exception.UnknownClassException;
import agile.planner.util.CheckList;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FunctionState extends State {

    private static List<Task> tasks;
    private static List<CheckList> checkLists;
    private static List<Card> cards;
    private static List<Board> boards;
    private static List<Label> labels;
    private static boolean inFunction;

    @Override
    protected void processFunc(String line) {
        /*
        1. First line represents the parameters
        2. The remaining string is for the code being executed (or ignored if a comment)

        Example:
        Map<"foo:", "task, card\nadd: task, card">
        task, card      <-- Match with given input
        add: task, card <-- Call with given input
        ...

        Steps for how to solve:
        (ask me if you'd like a hint)
         */

        /*
         * 1. Process the arguments to ensure that they are valid
         * 2. Update a boolean value that resembles "inside_func" to allow ease of code calling
         *    -OR, we process it here
         * 3. Store the values inside the associated global function variables
         * 4. Call each function with the correct variables (will need boolean variable to make this easier)
         */


        //process arguments TODO (and ensure they match with parameters)
        String[] args = processArguments(line, 5, ",");
        for (String arg : args) {
            if (arg == null) {
                break;
            }
            String[] tokens = processTokens(arg, 2, "\\s");
            if ("_task".equals(tokens[0]) && tokens[1] == null) {
                tasks = new ArrayList<>();
                tasks.add(taskList.get(taskList.size() - 1));
            } else if ("_task".equals(tokens[0])) {
                int idx = Integer.parseInt(tokens[1]);
                tasks = new ArrayList<>();
                tasks.add(taskList.get(idx));
            } else if ("task".equals(tokens[0]) && tokens[1] == null) {
                tasks = taskList;
            } else if("_checklist".equals(tokens[0]) && tokens[1] == null) {
                checkLists = new ArrayList<>();
                checkLists.add(clList.get(clList.size() - 1));
            } else if("_checklist".equals(tokens[0])) {
                int idx = Integer.parseInt(tokens[1]);
                checkLists = new ArrayList<>();
                checkLists.add(clList.get(idx));
            } else if("checklist".equals(tokens[0]) && tokens[1] == null) {
                checkLists = clList;
            } else if("_card".equals(tokens[0]) && tokens[1] == null) {
                cards = new ArrayList<>();
                cards.add(cardList.get(cardList.size() - 1));
            } else if("_card".equals(tokens[0])) {
                int idx = Integer.parseInt(tokens[1]);
                cards = new ArrayList<>();
                cards.add(cardList.get(idx));
            } else if("card".equals(tokens[0]) && tokens[1] == null) {
                cards = cardList;
            } else if("_label".equals(tokens[0]) && tokens[1] == null) {
                labels = new ArrayList<>();
                labels.add(labelList.get(labelList.size() - 1));
            } else if("_label".equals(tokens[0])) {
                int idx = Integer.parseInt(tokens[1]);
                labels = new ArrayList<>();
                labels.add(labelList.get(idx));
            } else if("label".equals(tokens[0]) && tokens[1] == null) {
                labels = labelList;
            } else {
                throw new InvalidFunctionException();
            }
        } //TODO need to add "board" to the above argument processing

        Scanner strScanner = new Scanner(line);
        String script = funcMap.get(strScanner.next());
        Scanner funcScanner = new Scanner(script);
        funcScanner.nextLine(); //skips the function definition and parameters
        while(funcScanner.hasNextLine()) {
            String statement = funcScanner.nextLine();
            //process each line of code while making function calls here
            parseFunction(statement);
        }

        scriptLog.reportFunctionCall(line);

        //reset all the variables
        resetVariables();
    }

    protected static List<Task> getTasks() {
        return tasks;
    }

    protected static List<CheckList> getCheckLists() {
        return checkLists;
    }

    protected static List<Card> getCards() {
        return cards;
    }

    protected static List<Board> getBoards() {
        return boards;
    }

    protected static List<Label> getLabels() {
        return labels;
    }

    protected static boolean isInFunction() {
        return inFunction;
    }

    private void resetVariables() {
        tasks = null;
        checkLists = null;
        cards = null;
        boards = null;
        labels = null;
        inFunction = false;
    }

    private void parseFunction(String line) {
        Scanner tokenScanner = new Scanner(line);
        String type = tokenScanner.next();
        if("print:".equals(type)) {
            new PrintState().processFunc(line);
        } else if(type.charAt(0) == '#') {
            //do nothing
        } else if("add:".equals(type)) {
            new AddState().processFunc(line);
        } else if("edit:".equals(type)) {
            new EditState().processFunc(line);
        } else if("remove:".equals(type)) {
            new RemoveState().processFunc(line);
        } else if(funcMap.containsKey(type)) {
            new FunctionState().processFunc(line);
        } else if("task:".equals(type) || "label:".equals(type) || "day:".equals(type)
                || "checklist:".equals(type) || "card:".equals(type) || "board:".equals(type)) {
            throw new InvalidFunctionException();
        } else {
            throw new UnknownClassException();
        }
    }
}
