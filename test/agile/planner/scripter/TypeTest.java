package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import org.junit.Test;

import static org.junit.Assert.*;

public class TypeTest {

    @Test
    public void getLinkerData() {
        Card c1 = new Card("MA");
        Type t1 = new Type(c1, "c1", Type.TypeId.CARD);
        assertEquals(c1, t1.getLinkerData());

        t1 = new Type("Hello", null);
        assertNull(t1.getLinkerData());
    }

    @Test
    public void setLinkerData() {
        Card c1 = new Card("MA");
        Type t1 = new Type(c1, "c1", Type.TypeId.CARD);
        assertEquals(c1, t1.getLinkerData());
        Label label = new Label(0, "Math", 3);
        t1.setLinkerData(label, Type.TypeId.LABEL);
        assertEquals(label, t1.getLinkerData());
    }

    @Test
    public void getVariableName() {
        Card c1 = new Card("MA");
        Type t1 = new Type(c1, "c1", Type.TypeId.CARD);
        assertEquals("c1", t1.getVariableName());

        t1 = new Type("Hello", null);
        assertNull(t1.getVariableName());
    }

    @Test
    public void getVariabTypeId() {
        Card c1 = new Card("MA");
        Type t1 = new Type(c1, "c1", Type.TypeId.CARD);
        assertEquals(Type.TypeId.CARD, t1.getVariabTypeId());

        t1 = new Type("Hello", null);
        assertEquals(Type.TypeId.STRING, t1.getVariabTypeId());
    }

    @Test
    public void getIntConstant() {
    }

    @Test
    public void setIntConstant() {
    }

    @Test
    public void getBoolConstant() {
    }

    @Test
    public void setBoolConstant() {
    }

    @Test
    public void getStringConstant() {
    }

    @Test
    public void setStringConstant() {
    }

    @Test
    public void attrSet() {
        Card c1 = new Card("MA");
        Type t1 = new Type(c1, "c1", Type.TypeId.CARD);
        Type ret = t1.attrSet(Parser.AttrFunc.GET_TITLE, null);
        assertEquals("MA", ret.getStringConstant());
    }

    @Test
    public void compareTo() {
    }

    @Test
    public void testToString() {
    }
}