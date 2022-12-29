package agile.planner.task;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import agile.planner.task.Task.SubTask;
import agile.planner.util.Time;

/**
 * Tests Task core functionality
 *
 * @author Andrew Roe
 */
public class TaskTest {

    /**
     * Tests AddSubTask() functionality
     */
    @Test
    public void testAddSubTask() {
        Task t1 = new Task(0, "CSC116", 4, 2);
        assertNull(t1.addSubTask(0, false));
        SubTask st1 = t1.addSubTask(2, false);
        assertEquals(2, st1.getSubTaskHours());
        assertNull(t1.addSubTask(3, false));
        t1.addSubTask(2, false);
        assertEquals(0, t1.getSubTotalHoursRemaining());
    }

    /**
     * Tests getSubTotalRemaining() functionality
     */
    @Test
    public void testGetSubTotalRemaining() {
        Task t1 = new Task(0, "CSC116", 5, 2);
        SubTask st1 = t1.addSubTask(2, false);
        assertEquals(2, st1.getSubTaskHours());
        assertEquals(3, t1.getSubTotalHoursRemaining());
        assertNull(t1.addSubTask(4, false));
        t1.addSubTask(3, false);
        assertEquals(0, t1.getSubTotalHoursRemaining());
    }

    /**
     * Tests compareTo() functionality
     */
    @Test
    public void testCompareTo() {
        Task t1 = new Task(0, "CSC116", 5, 2);
        Task t2 = new Task(1, "CSC216", 1, 1);
        Task t3 = new Task(2, "CSC316", 6, 2);
        Task t4 = new Task(3, "Test", 1, 1);
        assertEquals(-1, t3.compareTo(t1));
        assertEquals(0, t1.compareTo(t1));
        assertEquals(1, t1.compareTo(t3));
        assertEquals(-1, t2.compareTo(t3));
        assertEquals(0, t2.compareTo(t4));
        assertEquals(1, t1.compareTo(t2));
    }

    /**
     * Tests toString() functionality
     */
    @Test
    public void testToString() {
        Task t1 = new Task(0, "A", 2, 0);
        assertEquals("Task [name=A, total=2]", t1.toString());
    }


    /**
     * Tests getters & setters for Task
     */
    @Test
    public void testGettersAndSetters() {
        Task t1 = new Task(0, "CSC316", 8, 3);
        assertEquals("CSC316", t1.getName());
        assertEquals(8, t1.getTotalHours());
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, 3);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        assertEquals(date, t1.getDueDate());
    }

    /**
     * Tests SubTask functionality
     */
    @Test
    public void testSubTask() {
        Task t1 = new Task(0, "CSC116", 5, 2);
        SubTask st1 = t1.addSubTask(2, false);
        assertEquals(t1, st1.getParentTask());
        assertEquals(2, st1.getSubTaskHours());
        assertEquals("SubTask [name=CSC116, hours=2]", st1.toString());
        assertEquals(0, t1.getAverageNumHours());

        Task t2 = new Task(1, "CSC216", 4, 1);
        SubTask st2 = t2.addSubTask(3, false);

        assertEquals(-1, st2.compareTo(st1));
        assertEquals(0, st1.compareTo(st1));
        assertEquals(1, st1.compareTo(st2));

        assertEquals(1, t2.getSubTotalHoursRemaining());

        t2.reset();
        assertEquals(4, t2.getSubTotalHoursRemaining());
        assertEquals(0, t2.getAverageNumHours());

        assertFalse(st2.getOverflowStatus());
        assertTrue(t2.addSubTask(1, true).getOverflowStatus());

        Task t3 = new Task(2, "Z", 17, 1);
        Calendar current = Time.getFormattedCalendarInstance(0);
        t3.setAverageNumHours(current);
        assertEquals(9, t3.getAverageNumHours());
    }

    @Test
    public void testCheckList() {
        Task t1 = new Task(0, "CSC116", 5, 2);
        Task.CheckList cl = t1.addCheckList("Hello World");
        assertEquals("Hello World", cl.getName());
        assertEquals(0, cl.size());
    }

    @Test
    public void testCheckListSetName() {
        Task t1 = new Task(0, "CSC116", 5, 2);
        Task.CheckList cl = t1.addCheckList("Hello World");
        //TODO
    }

   @Test
   public void testCheckListAddItem() {
       Task t1 = new Task(0, "CSC116", 5, 2);
       Task.CheckList cl = t1.addCheckList("Hello World");
       cl.addItem("Work to do");
       assertEquals("Work to do", cl.getItem(0).getDescription()); //TODO will need to update
   }

}