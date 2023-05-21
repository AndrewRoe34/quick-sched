package agile.planner.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests LongOrderComparator functionality
 *
 * @author Lucia Langaney
 */
public class ShortOrderComparatorTest {
    /**
     * Tests compare functionality
     * */
    @Test
    public void compare() {
        ShortOrderComparator comparator = new ShortOrderComparator();
        Task t1 = new Task(0, "A", 1, 0);
        Task t2 = new Task(1, "B", 1, 1);
        Task t3 = new Task(2, "C", 2, 1);
        Task t4 = new Task(3, "D", 1, 1);
        Task t5 = new Task(4, "E", 2, 1);
        Task t6 = new Task(5, "F", 1, 1);
        Task t7 = new Task(6, "G", 1, 1);

        assertEquals(-1, comparator.compare(t1,t2));
        assertEquals(-1, comparator.compare(t2,t3));
        assertEquals(1, comparator.compare(t2,t1));
        assertEquals(1, comparator.compare(t5,t4));
        assertEquals(0, comparator.compare(t6,t7));
    }
}