package agile.planner.util;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

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
     * @param tasks all Tasks in System
     * @param cards all Cards in System
     * @param labels all Labels in System
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
                cardSB.append(title);
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
                        cardSB.append(", C").append(labelList.size() - 1);
                    } else {
                        cardSB.append(", C").append(labelList.indexOf(l));
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
                for(Label l : t.getLabel()) {
                    if(!labelList.contains(l)) {
                        labelList.add(l);
                        cardSB.append(", L").append(labelList.size() - 1);
                    } else {
                        cardSB.append(", L").append(labelList.indexOf(l));
                    }
                }
                CheckList cl = t.getCheckList();
                if(cl != null) {
                    checkListList.add(cl);
                    taskSB.append("CL").append(checkListList.size() - 1);
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
        }
        if(!checkListList.isEmpty()) {
            System.out.println("CHECKLIST {");
            for(CheckList cl : checkListList) {
                System.out.print("  " + cl.getName());
                for(CheckList.Item i : cl.getItems()) {
                    System.out.print(", " + i);
                }
            }
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

    }
}
