package agile.planner.data;

import agile.planner.user.UserConfig;
import agile.planner.util.EventLog;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.PriorityQueue;

import static org.junit.Assert.*;

/**
 * Tests LongOrderComparator functionality
 *
 * @author Lucia Langaney
 */
public class LongOrderComparatorTest {
    /**
     * Tests compare functionality
     * */
    @Test
    public void compare() {
        LongOrderComparator comparator = new LongOrderComparator();
        Task t1 = new Task(0, "A", 1, 0);
        Task t2 = new Task(1, "B", 2, 1);
        Task t3 = new Task(2, "C", 1, 1);
        Task t4 = new Task(3, "D", 1, 1);
        Task t5 = new Task(4, "E", 2, 1);
        Task t6 = new Task(5, "F", 1, 1);
        Task t7 = new Task(6, "G", 1, 1);

        assertEquals(-1, comparator.compare(t1,t2));
        assertEquals(-1, comparator.compare(t2,t3));
        assertEquals(1, comparator.compare(t2,t1));
        assertEquals(1, comparator.compare(t4,t5));
        assertEquals(0, comparator.compare(t6,t7));
    }
}