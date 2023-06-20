package agile.planner.scripter;

import agile.planner.data.Board;
import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.util.CheckList;

import java.util.List;

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

        //process arguments (and ensure they match)

        //update variables depending on what's provided

        //process each line of code while making function calls here

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
}
