package agile.planner.task.tool;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckListTest {

    private CheckList cl;

    @Before
    public void setup() {
        cl = new CheckList("CheckList");
    }

    @Test
    public void testSetName() {
        assertEquals("CheckList", cl.getName());
        cl.setName("B");
        assertEquals("B", cl.getName());
    }

    @Test
    public void testAddItem() {
        assertTrue(cl.addItem("A"));
        assertTrue(cl.addItem("A"));
    }

    @Test
    public void testRemoveItem() {
        cl.addItem("A");
        cl.addItem("B");
        CheckList.Item i1 = cl.getItem(0);
        assertEquals(i1, cl.removeItem(0));
        i1 = cl.getItem(0);
        assertEquals(i1, cl.removeItem(0));
    }

    @Test
    public void testShiftItem() {
        cl.addItem("A");
        cl.addItem("B");
        CheckList.Item i1 = cl.getItem(0);
        CheckList.Item i2 = cl.getItem(1);
        cl.shiftItem(0, 1);
        assertEquals(i1, cl.getItem(1));
        assertEquals(i2, cl.getItem(0));
        cl.shiftItem(0, 0);
        assertEquals(i2, cl.getItem(0));
    }

    @Test
    public void testGetItem() {
        cl.addItem("Pick up book");
        CheckList.Item i1 = cl.getItem(0);
        assertEquals(i1, cl.removeItem(0));
    }

    @Test
    public void testResetCheckList() {
        cl.addItem("Pick up book");
        cl.addItem("Study book");
        cl.addItem("Close book");
        cl.addItem("Return book");
        assertEquals(4, cl.size());
        cl.resetCheckList();
        assertEquals(0, cl.size());
        assertEquals("CheckList", cl.getName());
    }

    @Test
    public void testSize() {
        assertEquals(0, cl.size());
        cl.addItem("A");
        cl.addItem("B");
        assertEquals(2, cl.size());
        cl.removeItem(0);
        assertEquals(1, cl.size());
        cl.removeItem(0);
        assertEquals(0, cl.size());
    }

    @Test
    public void testGetPercentage() {
        cl.addItem("Pick up book");
        cl.addItem("Study book");
        cl.addItem("Close book");
        cl.addItem("Return book");

        assertEquals(0, cl.getPercentage());
        cl.getItem(0).markComplete();
        assertEquals(25, cl.getPercentage());
        cl.getItem(1).markComplete();
        assertEquals(50, cl.getPercentage());
        cl.getItem(2).markComplete();
        assertEquals(75, cl.getPercentage());
        cl.getItem(3).markComplete();
        assertEquals(100, cl.getPercentage());
        cl.getItem(0).markIncomplete();
        assertEquals(75, cl.getPercentage());
        cl.getItem(1).markIncomplete();
        assertEquals(50, cl.getPercentage());
        cl.getItem(2).markIncomplete();
        assertEquals(25, cl.getPercentage());
        cl.getItem(3).markIncomplete();
        assertEquals(0, cl.getPercentage());
    }

    @Test
    public void testToString() {
        cl.addItem("Pick up book");
        cl.addItem("Study book");
        cl.addItem("Close book");
        cl.addItem("Return book");

        cl.getItem(0).markComplete();
        cl.getItem(1).markComplete();
        String test = cl.toString();
        assertEquals("CheckList [50%]:\n" +
                "* Pick up book✅\n" +
                "* Study book✅\n" +
                "* Close book\n" +
                "* Return book\n", test);
    }
}