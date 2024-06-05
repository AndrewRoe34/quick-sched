package com.planner.schedule;

import com.planner.models.Task;
import com.planner.io.IOProcessing;
import com.planner.schedule.day.Day;
import com.planner.models.UserConfig;
import com.planner.util.EventLog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests CompactScheduler functionality
 *
 * @author Andrew Roe
 */
public class CompactSchedulerTest {

//    /** Logs scheduling data */
//    private static EventLog eventLog;
//    /** User settings for system */
//    private static UserConfig config;
//    /** Holds all Tasks for system */
//    private PriorityQueue<Task> pq;
//    /** Holds completed Tasks for system */
//    private PriorityQueue<Task> complete;
//    /** Maps Tasks to IDs */
//    private Map<Integer, Task> taskMap;
//
//    /**
//     * Sets up EventLog and UserConfig before all testing
//     *
//     * @throws Exception if FileNotFound
//     */
//    @BeforeAll
//    public static void setUpClass() throws Exception {
//        eventLog = EventLog.getEventLog();
//        config = IOProcessing.readCfg(null);
//    }
//
//    /**
//     * Sets up core data structures for running scheduler
//     */
//    @BeforeEach
//    public void setUp() {
//        pq = new PriorityQueue<>();
//        complete = new PriorityQueue<>();
//        taskMap = new HashMap<>();
//        config.setFitDay(false);
//    }
//
//    /**
//     * Tests scheduler functionality but with valid data
//     *
//     * @throws FileNotFoundException if file cannot be located
//     */
//    @Test
//    public void assignDayValid() throws FileNotFoundException {
//        Scheduler compactSched = CompactScheduler.getSingleton(config, eventLog);
//
//        IOProcessing.readTasks("data/break.txt", pq, taskMap, 0);
//        Day d0 = new Day(0, 8, 0);
//        Day d1 = new Day(1, 8, 1);
//        Day d2 = new Day(2, 8, 2);
//
//        //DAY 0
//        compactSched.assignDay(d0, 0, complete, pq);
//        assertEquals(1, d0.getNumSubTasks());
//        assertEquals(0, d0.getSpareHours());
//        assertEquals(8, d0.getHoursFilled());
//        assertEquals(0, d0.getParentTask(0).getSubTotalHoursRemaining());
//        assertEquals("A", d0.getParentTask(0).getName());
//        //DAY 1
//        compactSched.assignDay(d1, 0, complete, pq);
//        assertEquals(2, d1.getNumSubTasks());
//        assertEquals(0, d1.getSpareHours());
//        assertEquals(10,d1.getHoursFilled());
//        assertEquals(0, d1.getParentTask(0).getSubTotalHoursRemaining());
//        assertEquals("C", d1.getParentTask(0).getName());
//        assertEquals(0, d1.getParentTask(1).getSubTotalHoursRemaining());
//        assertEquals("D", d1.getParentTask(1).getName());
//        //DAY 2
//        compactSched.assignDay(d2, 0, complete, pq);
//        assertEquals(1, d2.getNumSubTasks());
//        assertEquals(2, d2.getSpareHours());
//        assertEquals(6, d2.getHoursFilled());
//        assertEquals(0, d2.getParentTask(0).getSubTotalHoursRemaining());
//        assertEquals("B", d2.getParentTask(0).getName());
//
//        System.out.println(d0);
//        System.out.println(d1);
//        System.out.println(d2);
//    }
//
//    /**
//     * Tests scheduler functionality but with leftover data
//     *
//     * @throws FileNotFoundException if file cannot be located
//     */
//    @Test
//    public void assignDayLeftover() throws FileNotFoundException {
//        Scheduler compactSched = CompactScheduler.getSingleton(config, eventLog);
//
//        IOProcessing.readTasks("data/compact.txt", pq, taskMap, 0);
//        Day d0 = new Day(0, 8, 0);
//        Day d1 = new Day(1, 8, 1);
//
//        //DAY 0
//        compactSched.assignDay(d0, 0, complete, pq);
//        assertEquals(2, d0.getNumSubTasks());
//        assertEquals(0, d0.getSpareHours());
//        assertEquals(8, d0.getHoursFilled());
//        assertEquals(0, d0.getParentTask(0).getSubTotalHoursRemaining());
//        assertEquals("A", d0.getParentTask(0).getName());
//        assertEquals(2, d0.getParentTask(1).getSubTotalHoursRemaining());
//        assertEquals("B", d0.getParentTask(1).getName());
//        //DAY 1
//        compactSched.assignDay(d1, 0, complete, pq);
//        assertEquals(2, d1.getNumSubTasks());
//        assertEquals(3, d1.getSpareHours());
//        assertEquals(5,d1.getHoursFilled());
//        assertEquals(0, d1.getParentTask(0).getSubTotalHoursRemaining());
//        assertEquals("C", d1.getParentTask(0).getName());
//        assertEquals(0, d1.getParentTask(1).getSubTotalHoursRemaining());
//        assertEquals("B", d1.getParentTask(1).getName());
//    }
//
//    /**
//     * Tests scheduler functionality but with overflow data
//     *
//     * @throws FileNotFoundException if file cannot be located
//     */
//    @Test
//    public void assignDayOverflow() throws FileNotFoundException {
//        Scheduler compactSched = CompactScheduler.getSingleton(config, eventLog);
//
//        IOProcessing.readTasks("data/overflow.txt", pq, taskMap, 0);
//        Day d0 = new Day(0, 8, 0);
//
//        //DAY 0
//        compactSched.assignDay(d0, 0, complete, pq);
//        assertEquals(3, d0.getNumSubTasks());
//        assertEquals(0, d0.getSpareHours());
//        assertEquals(14, d0.getHoursFilled());
//    }
//
//    /**
//     * Tests scheduler functionality but with FIT_SCHEDULE=TRUE
//     *
//     * @throws FileNotFoundException if file cannot be located
//     */
//    @Test
//    public void assignDayFitSchedule() throws FileNotFoundException {
//        config.setFitDay(true);
//        Scheduler compactSched = CompactScheduler.getSingleton(config, eventLog);
//
//        IOProcessing.readTasks("data/overflow.txt", pq, taskMap, 0);
//        Day d0 = new Day(0, 8, 0);
//
//        //DAY 0
//        compactSched.assignDay(d0, 0, complete, pq);
//        assertEquals(2, d0.getNumSubTasks());
//        assertEquals(0, d0.getSpareHours());
//        assertEquals(8, d0.getHoursFilled());
//    }
}