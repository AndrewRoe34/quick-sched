package agile.planner.util;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;

import java.util.HashMap;
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
    public static String createJBin(PriorityQueue<Task> tasks, List<Card> cards, List<Label> labels) {
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
