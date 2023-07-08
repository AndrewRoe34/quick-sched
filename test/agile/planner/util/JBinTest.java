package agile.planner.util;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JBinTest {

    @Test
    public void createJBin() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card("HW"));
        cardList.get(0).addTask(new Task(0, "Read", 4, 0));
        cardList.get(0).addTask(new Task(1, "Write", 2, 1));
        cardList.get(0).getTask().get(0).addLabel(new Label(0, "MA", 3));
        JBin.createJBin(cardList);
    }

    @Test
    public void processJBin() {
    }
}