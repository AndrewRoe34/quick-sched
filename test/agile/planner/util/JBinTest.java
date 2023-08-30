package agile.planner.util;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class JBinTest {

    @Test
    public void createJBin() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card("HW"));
        cardList.get(0).addTask(new Task(0, "Read", 4, 0));
        cardList.get(0).addTask(new Task(1, "Write", 2, 1));
        cardList.get(0).getTask().get(0).addLabel(new Label(0, "MA", 3));
        cardList.get(0).getTask().get(1).addCheckList(0, "To Do");
        cardList.get(0).getTask().get(1).getCheckList().addItem("Item 1");
        cardList.get(0).getTask().get(1).getCheckList().addItem("Item 2");
        cardList.get(0).getTask().get(1).getCheckList().markItemById(0, true);
        Label l = cardList.get(0).getTask().get(0).getLabel().get(0);
        cardList.get(0).addLabel(l);
        cardList.get(0).addLabel(new Label(1, "Party", 4));
        JBin.createJBin(cardList);
        String jbin = "LABEL {\n" +
                "  MA, 3\n" +
                "  Party, 4\n" +
                "}\n" +
                "\n" +
                "CHECKLIST {\n" +
                "  To Do, Item 1✅, Item 2\n" +
                "}\n" +
                "\n" +
                "TASK {\n" +
                "  Read, 4, 4, L0\n" +
                "  Write, 2, 2, CL0\n" +
                "}\n" +
                "\n" +
                "CARD {\n" +
                "  HW, T0, T1, L0, L1\n" +
                "}";
        PriorityQueue<Task> tasks = new PriorityQueue<>();
        List<Label> labels = new ArrayList<>();
        List<Card> cards = new ArrayList<>();
        JBin.processJBin(jbin, tasks, cards, labels);
        for(Task t : tasks)
            System.out.println(t);
        for(Card c : cards)
            System.out.println(c);
        for(Label ll : labels)
            System.out.println(ll);
    }

    @Test
    public void processJBin() {
    }
}