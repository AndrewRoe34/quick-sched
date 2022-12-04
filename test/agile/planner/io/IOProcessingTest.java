package agile.planner.io;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.PriorityQueue;

import org.junit.Test;

import agile.planner.task.Task;

/**
 * Tests IOProcessing functionality
 *
 * @author Andrew Roe
 */
public class IOProcessingTest {

    /**
     * Tests writeSchedule() functionality
     */
    @Test
    public void testWriteSchedule() {
//		Day d1 = new Day(8, 0);
//		SubTask st1 = new Task("Task1", 4, 0).addSubTask(4);
//		d1.addSubTask(st1);
//		Day d2 = new Day(6, 1);
//		SubTask st2 = new Task("Task2", 5, 1).addSubTask(5);
//		d2.addSubTask(st2);
//		Day d3 = new Day(7, 2);
//		SubTask st3 = new Task("Task3", 3, 2).addSubTask(3);
//		d3.addSubTask(st3);
//		LinkedList<Day> list = new LinkedList<>();
//		list.addLast(d1);
//		list.addLast(d2);
//		list.addLast(d3);
//
//		IOProcessing.writeSchedule(list);

    }

    /**
     * Tests readSchedule() functionality
     */
    @Test
    public void testReadSchedule() {
        try {
            PriorityQueue<Task> pq = IOProcessing.readTasks("data/break.txt");
            assertEquals("A", pq.remove().getName());
            assertEquals("B", pq.remove().getName());
            assertEquals("C", pq.remove().getName());
            assertEquals("D", pq.remove().getName());
            assertTrue(pq.isEmpty());
        } catch (FileNotFoundException e) {
            fail();
        }
    }

}