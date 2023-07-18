package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Task;
import agile.planner.scripter.exception.InvalidPairingException;
import agile.planner.util.CheckList;

import java.util.List;

public class AddState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 2, ",");
        String[] arg1 = processTokens(tokens[0], 2, "\\s");
        String[] arg2 = processTokens(tokens[1], 2, "\\s");
        //TODO will need to check for duplicates (not sure how to handle it for time being)
        switch(arg1[0]) {
            case "_task":
            case "task":
                processTaskAddition(arg1, arg2);
                break;
            case "_checklist":
            case "checklist":
                processCheckListAddition(arg1, arg2);
                break;
            case "_label":
            case "label":
                processLabelAddition(arg1, arg2);
                break;
            case "_card":
            case "card":
                break;
            default:
                throw new InvalidPairingException();
        }
    }

    /**
     * Adds a specific Task to an instance of Card
     *
     * @param arg1 tokens of argument 1
     * @param arg2 tokens of argument 2
     */
    private void processTaskAddition(String[] arg1, String[] arg2) {
        List<Card> cards = FunctionState.getCards() != null ? FunctionState.getCards() : cardList;
        List<Task> tasks = FunctionState.getTasks() != null ? FunctionState.getTasks() : taskList;
        Card singleCard = null;
        if("_card".equals(arg2[0])) {
            if(arg2[1] == null) {
                singleCard = cards.get(cards.size() - 1);
            } else {
                singleCard = cards.get(Integer.parseInt(arg2[1]));
            }
        } else if(!"card".equals(arg2[0]) || arg2[1] != null) {
            throw new InvalidPairingException("Expected Card but was a different class");
        }

        switch(arg1[0]) {
            case "task":
                if(singleCard != null) {
                    for(Task t : tasks) {
                        singleCard.addTask(t);
                    }
                } else {
                    for(Card c : cards) {
                        for (Task t : tasks) {
                            c.addTask(t);
                        }
                    }
                }
                break;
            case "_task":
                Task singleTask = arg2[1] == null ? tasks.get(tasks.size() - 1)
                        : tasks.get(Integer.parseInt(arg2[1]));
                if(singleCard != null) {
                    singleCard.addTask(singleTask);
                } else {
                    for(Card c : cards) {
                        c.addTask(singleTask);
                    }
                }
                break;
        }
    }

    /**
     * Adds a specific Checklist to an instance of Task
     *
     * @param arg1 tokens of argument 1
     * @param arg2 tokens of argument 2
     */
    private void processCheckListAddition(String[] arg1, String[] arg2) {
        List<Task> tasks = FunctionState.getTasks() != null ? FunctionState.getTasks() : taskList;
        List<CheckList> checkLists = FunctionState.getCheckLists() != null ? FunctionState.getCheckLists() : clList;
        Task singleTask = null;
        if("_task".equals(arg2[0])) {
            if(arg2[1] == null) {
                singleTask = tasks.get(tasks.size() - 1);
            } else {
                singleTask = tasks.get(Integer.parseInt(arg2[1]));
            }
        } else if(!"task".equals(arg2[0]) || arg2[1] != null) {
            throw new InvalidPairingException("Expected Task but was a different class");
        }

        switch(arg1[0]) {
            case "checklist":
                if(singleTask != null) {
                    for(CheckList cl : checkLists) {
                        singleTask.addCheckList(cl);
                    }
                } else {
                    for(Task t : tasks) {
                        for (CheckList cl : checkLists) {
                            t.addCheckList(cl);
                        }
                    }
                }
                break;
            case "_checklist":
                CheckList singleCheckList = arg2[1] == null ? checkLists.get(checkLists.size() - 1)
                        : checkLists.get(Integer.parseInt(arg2[1]));
                if(singleTask != null) {
                    singleTask.addCheckList(singleCheckList);
                } else {
                    for(Task t : tasks) {
                        t.addCheckList(singleCheckList);
                    }
                }
                break;
        }
    }

    /**
     * Adds a specific Label to an instance of Task or Card
     *
     * @param arg1 tokens of argument 1
     * @param arg2 tokens of argument 2
     */
    private void processLabelAddition(String[] arg1, String[] arg2) {
        //todo need to add refactored version here (will need to account for two types, task and card)
    }
}
