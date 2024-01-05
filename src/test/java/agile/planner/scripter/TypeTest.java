package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.scripter.Parser;
import agile.planner.scripter.Type;
import agile.planner.scripter.exception.InvalidGrammarException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        Type[] args = new Type[0];
        Type ret = t1.attrSet(Parser.AttrFunc.GET_TITLE, args);
        assertEquals("MA", ret.getStringConstant());

        //String attr functions
        Type t2 = new Type("hello world", null);
        args = new Type[0];
        ret = t2.attrSet(Parser.AttrFunc.LENGTH, args);
        assertEquals(11, (int) ret.getIntConstant());

        args = new Type[]{new Type(", i can read", null)};
        ret = t2.attrSet(Parser.AttrFunc.ADD, args);
        assertEquals("hello world", t2.getStringConstant());
        assertEquals("hello world, i can read", ret.getStringConstant());

        t2 = new Type("34", null);
        args = new Type[0];
        ret = t2.attrSet(Parser.AttrFunc.PARSE_INT, args);
        assertEquals(34, (int) ret.getIntConstant());

        t2 = new Type("true", null);
        ret = t2.attrSet(Parser.AttrFunc.PARSE_BOOL, args);
        assertTrue(ret.getBoolConstant());

        t2 = new Type("false", null);
        ret = t2.attrSet(Parser.AttrFunc.PARSE_BOOL, args);
        assertFalse(ret.getBoolConstant());

        args = new Type[1];
        args[0] = new Type(1, null);
        ret = t2.attrSet(Parser.AttrFunc.SUB_STRING, args);
        assertEquals("alse", ret.getStringConstant());

        //String exception checks todo

        //Integer attr functions
        args = new Type[1];
        args[0] = new Type(32, null);
        Type t3 = new Type(2, null);
        ret = t3.attrSet(Parser.AttrFunc.ADD, args);
        assertEquals(34, (int) t3.getIntConstant());
        assertNull(ret);

        args = new Type[1];
        args[0] = new Type(2, null);
        t3 = new Type(2, null);
        ret = t3.attrSet(Parser.AttrFunc.SUBTRACT, args);
        assertEquals(0, (int) t3.getIntConstant());
        assertNull(ret);

        args = new Type[2];
        args[0] = new Type(2, null);
        args[1] = new Type(3, null);
        t3 = new Type(2, null);
        ret = t3.attrSet(Parser.AttrFunc.MULTIPLY_ONE, args);
        assertEquals(12, (int) t3.getIntConstant());
        assertNull(ret);

        args = new Type[1];
        args[0] = new Type(2, null);
        t3 = new Type(2, null);
        ret = t3.attrSet(Parser.AttrFunc.DIVIDE_ONE, args);
        assertEquals(1, (int) t3.getIntConstant());
        assertNull(ret);

        args = new Type[1];
        args[0] = new Type(3, null);
        t3 = new Type(5, null);
        ret = t3.attrSet(Parser.AttrFunc.MOD_ONE, args);
        assertEquals(2, (int) t3.getIntConstant());
        assertNull(ret);

        //Integer exception checks
        args = new Type[1];
        args[0] = new Type(true, null);
        t3 = new Type(2, null);
        try {
            ret = t3.attrSet(Parser.AttrFunc.ADD, args);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InvalidGrammarException);
        }

        try {
            ret = t3.attrSet(Parser.AttrFunc.SUBTRACT, args);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InvalidGrammarException);
        }

        try {
            ret = t3.attrSet(Parser.AttrFunc.MULTIPLY_ONE, args);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InvalidGrammarException);
        }

        try {
            ret = t3.attrSet(Parser.AttrFunc.DIVIDE_ONE, args);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InvalidGrammarException);
        }

        try {
            ret = t3.attrSet(Parser.AttrFunc.MOD_ONE, args);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InvalidGrammarException);
        }

        //Boolean attr func
        Type t4 = new Type(true, null);
        try {
            args = new Type[0];
            ret = t4.attrSet(Parser.AttrFunc.ADD, args);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InvalidGrammarException);
        }
    }

    @Test
    public void compareTo() {
    }

    @Test
    public void testToString() {
    }
}