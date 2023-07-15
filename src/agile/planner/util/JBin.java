package agile.planner.util;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;

import java.util.*;

/**
 * Java Binary Serialization is modeled after JSON for data storage/retrieval.
 * This will provide greater efficiency, structure, security, as well as ease of passage
 * of data with the Front-End component
 *
 * @author Andrew Roe
 */
public class JBin {

    /**
     * Creates a Java Binary Serialization string to be later passed or stored
     *
     * @param cards all Cards in System
     * @return JBin String
     */
    public static String createJBin(List<Card> cards) {
        /* NOTES:
        1. Create Label section (with ID, not associated with system)
        2. Create Task section (with ID, not associated with system)
            --> Could hold a Label, which will be identified as "L#"
        3. Create Card section
            --> Could hold a Label, which will be identified as "L#"
            --> Could hold a Task, which will be identified as "T#"

        HashMaps will be very helpful for decoding JBin later on
        Need to account for possibility that file is read two times in a row
            --> This would mean tasks, cards, and labels are the same/duplicates (will need to verify via the equals method, i.e. HashSet)
            --> Otherwise, it simply gets added to the system

        FORMATTING OF DATA:
        LABEL {
          <Title>, <COLOR>
          ...
        }
        CHECKLIST {
          <ITEM0>, <ITEM1>, ...
          ...
        }
        TASK {
          <NAME>, <DUE_DATE>, <TOTAL_HR>, <USED_HR>, CL#, L#
          ...
        }
        CARD {
          <Title>, T#, L#
          ...
        }

        NOTE: Try working from bottom to top (might be able to save on efficiency and storage)
         */

        StringBuilder cardSB = new StringBuilder();
        List<Task> taskList = new ArrayList<>();
        List<Label> labelList = new ArrayList<>();
        List<CheckList> checkListList = new ArrayList<>();
        if(!cards.isEmpty()) {
            cardSB.append("CARD {\n");
            for(Card c : cards) {
                String title = c.getTitle();
                cardSB.append("  ").append(title);
                for(Task t : c.getTask()) {
                    if(!taskList.contains(t)) {
                        taskList.add(t);
                        cardSB.append(", T").append(taskList.size() - 1);
                    } else {
                        cardSB.append(", T").append(taskList.indexOf(t));
                    }
                }
                for(Label l : c.getLabel()) {
                    if(!labelList.contains(l)) {
                        labelList.add(l);
                        cardSB.append(", L").append(labelList.size() - 1);
                    } else {
                        cardSB.append(", L").append(labelList.indexOf(l));
                    }
                }
                cardSB.append("\n");
            }
            cardSB.append("}\n");
        }
        StringBuilder taskSB = new StringBuilder();
        if(!taskList.isEmpty()) {
            taskSB.append("TASK {\n");
            for(Task t : taskList) {
                String name = t.getName();
                int totalHours = t.getTotalHours();
                int remainingHours = t.getSubTotalHoursRemaining(); //TODO need to verify this
                taskSB.append("  ").append(name).append(", ").append(totalHours).append(", ").append(remainingHours);
                for(Label l : t.getLabel()) {
                    if(!labelList.contains(l)) {
                        labelList.add(l);
                        taskSB.append(", L").append(labelList.size() - 1);
                    } else {
                        taskSB.append(", L").append(labelList.indexOf(l));
                    }
                }
                CheckList cl = t.getCheckList();
                if(cl != null) {
                    checkListList.add(cl);
                    taskSB.append(", CL").append(checkListList.size() - 1);
                }
                taskSB.append("\n");
            }
            taskSB.append("}\n");
        }
        if(!labelList.isEmpty()) {
            System.out.println("LABEL {");
            for(Label l : labelList) {
                System.out.println("  " + l.getName() + ", " + l.getColor());
            }
            System.out.println("}\n");
        }
        if(!checkListList.isEmpty()) {
            System.out.println("CHECKLIST {");
            for(CheckList cl : checkListList) {
                System.out.print("  " + cl.getName());
                for(CheckList.Item i : cl.getItems()) {
                    System.out.print(", " + i);
                }
                System.out.println();
            }
            System.out.println("}\n");
        }
        System.out.println(taskSB.toString());
        System.out.println(cardSB.toString());
        //now go from top to bottom with all the data you now have

        return null;
    }

    /**
     * Processes the Java Binary Serialization string to update the current system
     *
     * @param data JBin string being processed
     * @param tasks Tasks holder
     * @param cards Cards holder
     * @param labels Labels holder
     */
    public static void processJBin(String data, PriorityQueue<Task> tasks, List<Card> cards, List<Label> labels) {
        //NOTE: When processing, you should work from top to bottom (use ArrayLists to easily locate data by index value)
        Scanner jbinScanner = new Scanner(data);
        boolean label = false;
        boolean checklist = false;
        boolean task = false;
        boolean card = false;
        List<CheckList> checkLists = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();
        while(jbinScanner.hasNextLine()) {
            String type = jbinScanner.nextLine();
            String[] tokens = type.split("\\s");
            if(!label && tokens.length == 2 && "LABEL".equals(tokens[0]) && "{".equals(tokens[1])) {
                label = true;
                while(jbinScanner.hasNextLine()) {
                    type = jbinScanner.nextLine();
                    tokens = type.split(",");
                    if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        break;
                    } else if(tokens.length == 2) {
                        labels.add(new Label(labels.size(), tokens[0].trim(), Integer.parseInt(tokens[1])));
                    } else {
                        throw new InputMismatchException();
                    }
                }
            } else if(!checklist && tokens.length == 2 && "CHECKLIST".equals(tokens[0]) && "{".equals(tokens[1])) {
                checklist = true;
                while(jbinScanner.hasNextLine()) {
                    type = jbinScanner.nextLine();
                    tokens = type.split(",");
                    if(tokens.length == 0) {
                        throw new InputMismatchException();
                    } else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        break;
                    } else if(tokens.length == 1) {
                        checkLists.add(new CheckList(checkLists.size(), tokens[0].trim()));
                    } else {
                        checkLists.add(new CheckList(checkLists.size(), tokens[0].trim()));
                        int itemId = 0;
                        for(int i = 1; i < tokens.length; i++) {
                            String item = tokens[i];
                            boolean complete = false;
                            if(item.charAt(item.length() - 1) == '✅') {
                                complete = true;
                                item = item.substring(0, item.length() - 1).trim();
                            }
                            checkLists.get(checkLists.size() - 1).addItem(item);
                            checkLists.get(checkLists.size() - 1).markItem(itemId++, complete);
                        }
                    }
                }
            } else if(!task && tokens.length == 2 && "TASK".equals(tokens[0]) && "{".equals(tokens[1])) {
                type = jbinScanner.nextLine();
                tokens = type.split(",");
                if(tokens.length == 0) {
                    throw new InputMismatchException();
                } else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                    break;
                } else if(tokens.length == 3) {
                    taskList.add(new Task(taskList.size(), tokens[0].trim(), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
                } else if(tokens.length > 3) {
                    taskList.add(new Task(taskList.size(), tokens[0].trim(), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
                    for(int i = 3; i < tokens.length; i++) {
                        String item = tokens[i].trim();
                        if(item.length() > 2 && item.charAt(0) == 'C' && item.charAt(1) == 'L') {
                            taskList.get(taskList.size() - 1).addCheckList(checkLists.get(Integer.parseInt(item.substring(2))));
                        } else if(item.length() > 1 && item.charAt(0) == 'L') {
                            taskList.get(taskList.size() - 1).addLabel(labels.get(Integer.parseInt(item.substring(2))));
                        } else {
                            throw new InputMismatchException();
                        }
                    }
                } else {
                    throw new InputMismatchException();
                }
            } else if(!card && tokens.length == 2 && "CARD".equals(tokens[0]) && "{".equals(tokens[1])) {
                //todo
            } else {
                throw new InputMismatchException();
            }

        }
    }
}
