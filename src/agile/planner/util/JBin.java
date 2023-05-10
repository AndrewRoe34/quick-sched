package agile.planner.util;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;

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

    public static String createJBinFile(PriorityQueue<Task> tasks, List<Card> cards, List<Label> labels) {
        return null;
    }

    public static void processJBinFile(String data, PriorityQueue<Task> tasks, List<Card> cards, List<Label> labels) {

    }
}
