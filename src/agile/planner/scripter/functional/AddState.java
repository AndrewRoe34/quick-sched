package agile.planner.scripter.functional;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.scripter.exception.InvalidPairingException;
import agile.planner.util.CheckList;

import java.util.List;

public class AddState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 2, ",");
        String[] arg1 = processTokens(tokens[0], 2, "\\s");
        String[] arg2 = processTokens(tokens[1], 2, "\\s");
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
    private void processTaskAddition(String[] arg1, String[] arg2) { //NOTE: this function is done!!!
        List<Card> cards = FunctionState.getCards() != null ? FunctionState.getCards() : cardList;
        List<Task> tasks = FunctionState.getTasks() != null ? FunctionState.getTasks() : taskList;
        Card singleCard = null;
        int cardIdx = -1;
        if("_card".equals(arg2[0])) {
            if(arg2[1] == null) {
                singleCard = cards.get(cards.size() - 1);
                cardIdx = cards.size() - 1;
            } else {
                cardIdx = Integer.parseInt(arg2[1]);
                singleCard = cards.get(cardIdx);
            }
        } else if(!"card".equals(arg2[0]) || arg2[1] != null) {
            throw new InvalidPairingException("Expected Card but was a different class");
        }

        switch(arg1[0]) {
            case "task":
                if(singleCard != null) {
                    int i = 0;
                    for(Task t : tasks) {
                        cardList.get(0).removeTask(t);
                        singleCard.addTask(t);
                        System.out.println("[T" + i + "] --> [C" + cardIdx + "]");
                        i++;
                    }
                } else {
                    int i = 0;
                    for(Card c : cards) {
                        int j = 0;
                        for (Task t : tasks) {
                            cardList.get(0).removeTask(t);
                            c.addTask(t);
                            System.out.println("[T" + j + "] --> [C" + i + "]");
                            j++;
                        }
                        i++;
                    }
                }
                break;
            case "_task":
                int taskIdx = arg1[1] == null ? tasks.size() - 1
                        : Integer.parseInt(arg1[1]);
                Task singleTask = tasks.get(taskIdx);
                if(singleCard != null) {
                    cardList.get(0).removeTask(singleTask);
                    singleCard.addTask(singleTask);
                    System.out.println("[T" + taskIdx + "] --> [C" + cardIdx + "]");
                } else {
                    int i = 0;
                    for(Card c : cards) {
                        cardList.get(0).removeTask(singleTask);
                        c.addTask(singleTask);
                        System.out.println("[T" + taskIdx + "] --> [C" + i + "]");
                        i++;
                    }
                }
                break;
            default:
                throw new InvalidPairingException("Expected Task but was something else");
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
                    throw new InvalidGrammarException("A Task cannot have multiple CheckLists");
//                    for(Task t : tasks) {
//                        for (CheckList cl : checkLists) {
//                            t.addCheckList(cl);
//                        }
//                    }
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
        //two possible options for a pairing class
        List<Task> tasks = FunctionState.getTasks() != null ? FunctionState.getTasks() : taskList;
        List<Card> cards = FunctionState.getCards() != null ? FunctionState.getCards() : cardList;

        //Class being added to one of the above
        List<Label> labels = FunctionState.getLabels() != null ? FunctionState.getLabels() : labelList;

        //Single class instance of task or card if possible
        Task singleTask = null;
        Card singleCard = null;

        //flags for type of class
        boolean isTask = true;

        switch(arg2[0]) {
            case "task":
                if(arg2[1] != null) {
                    throw new InvalidGrammarException("Provides 'task' but also includes index value");
                }
                break;
            case "_task":
                if(arg2[1] == null) {
                    singleTask = tasks.get(tasks.size() - 1);
                } else {
                    singleTask = tasks.get(Integer.parseInt(arg2[1]));
                }
                break;
            case "card":
                if(arg2[1] != null) {
                    throw new InvalidGrammarException("Provides 'card' but also includes index value");
                }
                isTask = false;
                break;
            case "_card":
                if(arg2[1] == null) {
                    singleCard = cards.get(cards.size() - 1);
                } else {
                    singleCard = cards.get(Integer.parseInt(arg2[1]));
                }
                isTask = false;
                break;
            default:
                throw new InvalidPairingException("Expected Task or Card but was a different class");
        }

        switch(arg1[0]) {
            case "label":
                if(isTask && singleTask != null) {
                    for(Label l : labels) {
                        singleTask.addLabel(l);
                    }
                } else if(isTask) {
                    for(Task t : tasks) {
                        for (Label l : labels) {
                            t.addLabel(l);
                        }
                    }
                } else if(singleCard != null) {
                    for(Label l : labels) {
                        singleCard.addLabel(l);
                    }
                } else {
                    for(Card c : cards) {
                        for (Label l : labels) {
                            c.addLabel(l);
                        }
                    }
                }
                break;
            case "_label":
                Label singleLabel = arg2[1] == null ? labels.get(labels.size() - 1)
                        : labels.get(Integer.parseInt(arg2[1]));
                if(isTask && singleTask != null) {
                    singleTask.addLabel(singleLabel);
                } else if(isTask) {
                    for(Task t : tasks) {
                        t.addLabel(singleLabel);
                    }
                } else if(singleCard != null) {
                    singleCard.addLabel(singleLabel);
                } else {
                    for(Card c : cards) {
                        c.addLabel(singleLabel);
                    }
                }
                break;
        }
    }
}
