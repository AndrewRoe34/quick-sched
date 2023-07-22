package agile.planner.scripter;

import agile.planner.data.Card;
import org.junit.Test;

import static org.junit.Assert.*;

public class TypeTest {

    @Test
    public void getDatatype() {
        Type t1 = new Type(new Card("HW"), "c1", 3);
        System.out.println(t1.getVariableName());
        Card c1 = (Card) t1.getDatatype();
        System.out.println(c1);
    }

    @Test
    public void getVariableName() {
    }

    @Test
    public void getVariableIdx() {
    }

    @Test
    public void compareTo() {
    }

    @Test
    public void testToString() {
    }
}