package agile.planner.scripter.functional;

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
            String[] tokens = processTokens(arg, 2, null);
            switch (tokens[0]) {
                case "_task":
                    if (tokens[1] == null) {
                        tasks = new ArrayList<>();
                        tasks.add(taskList.get(taskList.size() - 1));
                    } else {
                        int idx = Integer.parseInt(tokens[1]);
                        tasks = new ArrayList<>();
                        tasks.add(taskList.get(idx));
                    }
                    break;
                case "task":
                    if (tokens[1] == null) {
                        tasks = taskList;
                    } else {
                        throw new InvalidFunctionException();
                    }
                    break;
                case "_checklist":
                    if (tokens[1] == null) {
                        checkLists = new ArrayList<>();
                        checkLists.add(clList.get(clList.size() - 1));
                    } else {
                        int idx = Integer.parseInt(tokens[1]);
                        checkLists = new ArrayList<>();
                        checkLists.add(clList.get(idx));
                    }
                    break;
                case "checklist":
                    if (tokens[1] == null) {
                        checkLists = clList;
                    } else {
                        throw new InvalidFunctionException();
                    }
                    break;
                case "_card":
                    if (tokens[1] == null) {
                        cards = new ArrayList<>();
                        cards.add(cardList.get(cardList.size() - 1));
                    } else {
                        int idx = Integer.parseInt(tokens[1]);
                        cards = new ArrayList<>();
                        cards.add(cardList.get(idx));
                    }
                    break;
                case "card":
                    if (tokens[1] == null) {
                        cards = cardList;
                    } else {
                        throw new InvalidFunctionException();
                    }
                    break;
                case "_label":
                    if (tokens[1] == null) {
                        labels = new ArrayList<>();
                        labels.add(labelList.get(labelList.size() - 1));
                    } else {
                        int idx = Integer.parseInt(tokens[1]);
                        labels = new ArrayList<>();
                        labels.add(labelList.get(idx));
                    }
                    break;
                case "label":
                    if (tokens[1] == null) {
                        labels = labelList;
                    } else {
                        throw new InvalidFunctionException();
                    }
                    break;
                default:
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

//        scriptLog.reportFunctionCall(line);

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
        switch(type) {
            case "print:":
                new PrintState().processFunc(line);
                break;
            case "add:":
                new AddState().processFunc(line);
                break;
            case "edit:":
                new EditState().processFunc(line);
                break;
            case "remove:":
                new RemoveState().processFunc(line);
                break;
            case "task:":
            case "label:":
            case "day:":
            case "checklist:":
            case "card:":
            case "board:":
                throw new InvalidFunctionException();
            default:
                if(funcMap.containsKey(type)) {
                    new FunctionState().processFunc(line);
                } else if(type.charAt(0) != '#') {
                    throw new UnknownClassException();
                }
                break;
        }
    }
}
