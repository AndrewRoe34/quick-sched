package agile.planner.schedule.day;

import java.util.Calendar;

import agile.planner.data.Task;
import agile.planner.util.Time;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        Day d1 = new Day(0, 8, 2);
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
        Day d1 = new Day(0, 8, 0);

        assertTrue(d1.addSubTaskDynamically(new Task(0, "CSC316", 6, 1)));
        assertEquals(5, d1.getSpareHours());

        assertTrue(d1.hasSpareHours());

        assertTrue(d1.addSubTaskDynamically(new Task(1, "CSC230", 8, 2)));
        assertEquals(2, d1.getSpareHours());

        assertFalse(d1.addSubTaskDynamically(new Task(2, "CSC342", 3, 0)));
        assertFalse(d1.hasSpareHours());
    }

    /**
     * Tests getCapacity() functionality
     */
    @Test
    public void testGetCapacity() {
        Day d1 = new Day(0, 8, 0);
        assertEquals(8, d1.getCapacity());
        Day d2 = new Day(0, 1, 1);
        assertEquals(1, d2.getCapacity());
    }

    /**
     * Tests getId() functionality
     */
    @Test
    public void testGetId() {
        Day d1 = new Day(0, 8, 0);
        Day d2 = new Day(1, 1, 1);
        assertEquals(0, d1.getId());
        assertEquals(1, d2.getId());
    }

    /**
     * Tests getHoursFilled() functionality
     */
    @Test
    public void testGetHoursFilled() {
        Day d1 = new Day(0, 8, 0);
        d1.addSubTaskDynamically(new Task(0, "CSC316", 6, 1));
        assertEquals(3, d1.getHoursFilled());
        d1.addSubTaskDynamically(new Task(1, "CSC116", 3, 0));
        assertEquals(6, d1.getHoursFilled());
    }

    /**
     * Tests toString() functionality
     */
    @Test
    public void testFormattedString() {
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
        Day d1 = new Day(0, 8, days);
        d1.addSubTaskDynamically(new Task(0, "Future", 10, days));
        String expected = "01-01-3000\n"
                + "-Future, 10hr, Due 01-01-3000 OVERFLOW\n";
        assertEquals(expected, d1.formattedString());
    }

    /**
     * Tests functionality related to task management
     */
    @Test
    public void testTaskManagement() {
        Day day = new Day(0, 8, 0);
        Task task1 = new Task(0, "A", 4, 0);
        Task task2 = new Task(1, "B", 2, 0);
        assertEquals(0, day.getNumSubTasks());
        day.addSubTaskDynamically(task1);
        assertEquals(1, day.getNumSubTasks());
        day.addSubTaskDynamically(task2);
        assertEquals(2, day.getNumSubTasks());

        assertEquals(task1, day.getParentTask(0));
        assertEquals(task2, day.getParentTask(1));
    }

}