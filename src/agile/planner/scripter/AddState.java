package agile.planner.scripter;

import agile.planner.scripter.exception.InvalidPairingException;

public class AddState extends State {
    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 2, ",");
        String[] arg1 = processTokens(tokens[0], 2, "\\s");
        String[] arg2 = processTokens(tokens[1], 2, "\\s");
        //TODO will need to check for duplicates (not sure how to handle it for time being)
        switch(arg1[0]) {
            case "_task":
                processTaskAddition(arg1, arg2);
                break;
            case "_checklist":
                processCheckListAddition(arg1, arg2);
                break;
            case "_label":
                processLabelAddition(arg1, arg2);
                break;
            case "_card":
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
        if(arg1[1] == null) {
            if("_card".equals(arg2[0]) && arg2[1] == null) {
                cardList.get(cardList.size() - 1).addTask(taskList.get(taskList.size() - 1));
                System.out.println("Adds [T" + (taskList.size() - 1) + "] --> [C" + (cardList.size() - 1) + "]");
            } else if("_card".equals(arg2[0])) {
                cardList.get(Integer.parseInt(arg2[1])).addTask(taskList.get(taskList.size() - 1));
                System.out.println("Adds [T" + (taskList.size() - 1) + "] --> [C" + Integer.parseInt(arg2[1]) + "]");
            } else {
                throw new InvalidPairingException("Expected Card but was a different class");
            }
        } else {
            if("_card".equals(arg2[0]) && arg2[1] == null) {
                cardList.get(cardList.size() - 1).addTask(taskList.get(Integer.parseInt(arg1[1])));
                System.out.println("Adds [T" + Integer.parseInt(arg1[1]) + "] --> [C" + (cardList.size() - 1) + "]");
            } else if("_card".equals(arg2[0])) {
                cardList.get(Integer.parseInt(arg2[1])).addTask(taskList.get(Integer.parseInt(arg1[1])));
                System.out.println("Adds [T" + Integer.parseInt(arg1[1]) + "] --> [C" + Integer.parseInt(arg2[1]) + "]");
            } else {
                throw new InvalidPairingException("Expected Card but was a different class");
            }
        }
    }

    /**
     * Adds a specific Checklist to an instance of Task
     *
     * @param arg1 tokens of argument 1
     * @param arg2 tokens of argument 2
     */
    private void processCheckListAddition(String[] arg1, String[] arg2) {
        if(arg1[1] == null) {
            if("_task".equals(arg2[0]) && arg2[1] == null) {
                taskList.get(taskList.size() - 1).addCheckList(clList.get(clList.size() - 1));
                System.out.println("Adds [CL" + (clList.size() - 1) + "] --> [T" + (taskList.size() - 1) + "]");
            } else if("_task".equals(arg2[0])) {
                taskList.get(Integer.parseInt(arg2[1])).addCheckList(clList.get(clList.size() - 1));
                System.out.println("Adds [CL" + (clList.size() - 1) + "] --> [T" + Integer.parseInt(arg2[1]) + "]");
            } else {
                throw new InvalidPairingException("Expected Task but was a different class");
            }
        } else {
            if("_task".equals(arg2[0]) && arg2[1] == null) {
                taskList.get(taskList.size() - 1).addCheckList(clList.get(Integer.parseInt(arg1[1])));
                System.out.println("Adds [CL" + Integer.parseInt(arg1[1]) + "] --> [T" + (taskList.size() - 1) + "]");
            } else if("_task".equals(arg2[0])) {
                taskList.get(Integer.parseInt(arg2[1])).addCheckList(clList.get(Integer.parseInt(arg1[1])));
                System.out.println("Adds [CL" + Integer.parseInt(arg1[1]) + "] --> [T" + Integer.parseInt(arg2[1]) + "]");
            } else {
                throw new InvalidPairingException("Expected Task but was a different class");
            }
        }
    }

    /**
     * Adds a specific Label to an instance of Task or Card
     *
     * @param arg1 tokens of argument 1
     * @param arg2 tokens of argument 2
     */
    private void processLabelAddition(String[] arg1, String[] arg2) {
        if(arg1[1] == null) {
            if("_task".equals(arg2[0]) && arg2[1] == null) {
                taskList.get(taskList.size() - 1).addLabel(labelList.get(labelList.size() - 1));
            } else if("_task".equals(arg2[0])) {
                taskList.get(Integer.parseInt(arg2[1])).addLabel(labelList.get(labelList.size() - 1));
            } else if("_card".equals(arg2[0]) && arg2[1] == null) {
                cardList.get(cardList.size() - 1).addLabel(labelList.get(labelList.size() - 1));
            } else if("_card".equals(arg2[0])) {
                cardList.get(Integer.parseInt(arg2[1])).addLabel(labelList.get(labelList.size() - 1));
            } else {
                throw new InvalidPairingException("Expected Task or Card but was a different class");
            }
        } else {

        }
    }
}
