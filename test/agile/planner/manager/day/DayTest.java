package agile.planner.manager.day;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import agile.planner.task.Task;
import agile.planner.util.Time;

/**
 * Tests Day functionality
 *
 * @author Andrew Roe
 */
public class DayTest {

    /**
     * Tests getDate() functionality
     */
    @Test
    public void testGetDate() {
        Day d1 = new Day(8, 2);
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 2);
        future.set(Calendar.HOUR_OF_DAY, 0);
        future.set(Calendar.MINUTE, 0);
        future.set(Calendar.SECOND, 0);
        future.set(Calendar.MILLISECOND, 0);

        assertEquals(future, d1.getDate());
    }

    /**
     * Tests addSubTask() and getSpareHours() and hasSpareHours() functionality
     */
    @Test
    public void testAddSubTask() {
        Day d1 = new Day(8, 0);

        assertTrue(d1.addSubTask(new Task("CSC316", 6, 1)));
        assertEquals(5, d1.getSpareHours());

        assertTrue(d1.hasSpareHours());

        assertTrue(d1.addSubTask(new Task("CSC230", 8, 2)));
        assertEquals(2, d1.getSpareHours());

        assertFalse(d1.addSubTask(new Task("CSC342", 3, 0)));
        assertFalse(d1.hasSpareHours());
    }

    /**
     * Tests toString() functionality
     */
    @Test
    public void testToString() {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.set(Calendar.MONTH, 0);
        date.set(Calendar.YEAR, 3000);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        int days = Time.determineRangeOfDays(date, current);
        Day d1 = new Day(8, days);
        d1.addSubTask(new Task("Future", 10, days));
        String expected = "01-01-3000\n"
                + "-Future, 10hr, Due 01-01-3000 OVERFLOW\n";
        assertEquals(expected, d1.toString());
    }

    /**
     * Tests functionality related to task management
     */
    @Test
    public void testTaskManagement() {
        Day day = new Day(8, 0);
        Task task1 = new Task("A", 4, 0);
        Task task2 = new Task("B", 2, 0);
        assertEquals(0, day.getNumSubTasks());
        day.addSubTask(task1);
        assertEquals(1, day.getNumSubTasks());
        day.addSubTask(task2);
        assertEquals(2, day.getNumSubTasks());

        assertEquals(task1, day.getParentTask(0));
        assertEquals(task2, day.getParentTask(1));

    }

}