package agile.planner.io;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import agile.planner.io.IOProcessing;
import agile.planner.schedule.day.Day;
import agile.planner.user.UserConfig;

import agile.planner.data.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests IOProcessing functionality
 *
 * @author Andrew Roe
 */
public class IOProcessingTest {

    /**
     * Tests readCfg() functionality
     */
    @Test
    public void readCfg() {
        //Valid case
        try {
            UserConfig uc = IOProcessing.readCfg(null);
            assertNotNull(uc);
        } catch (Exception e) {
            fail();
        }
        //Misspelled keyword
        try {
            IOProcessing.readCfg("data/bad_settings.cfg");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InputMismatchException);
        }
        //Invalid input for expected string
        try {
            IOProcessing.readCfg("data/bad_settings1.cfg");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InputMismatchException);
        }
        //Invalid array
        try {
            IOProcessing.readCfg("data/bad_settings2.cfg");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InputMismatchException);
        }
        //Empty file
        try {
            IOProcessing.readCfg("data/bad_settings3.cfg");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InputMismatchException);
        }
        //Missing element
        try {
            IOProcessing.readCfg("data/bad_settings4.cfg");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof InputMismatchException);
        }
    }

    /**
     * Tests readSchedule() functionality
     */
    @Test @Deprecated
    public void readTasks() {
        try {
            Map<Integer, Task> taskMap = new HashMap<>();
            PriorityQueue<Task> pq = new PriorityQueue<>();
            int lastDueInDays = IOProcessing.readTasks("data/break.txt", pq, taskMap, 0);
            assertEquals("A", pq.remove().getName());
            assertEquals("C", pq.remove().getName());
            assertEquals("D", pq.remove().getName());
            assertEquals("B", pq.remove().getName());
            assertEquals(2, lastDueInDays);
            assertTrue(pq.isEmpty());
        } catch (FileNotFoundException e) {
            fail();
        }
    }

    /**
     * Tests writeDay() functionality
     */
    @Test
    public void outputDay() {
        //Test valid data
        String testFile = "data/s_day.txt";
        Path p1 = Path.of("data/s_day.txt");
        try {
            Day d1 = new Day(0, 8, 0);
            d1.addSubTaskDynamically(new Task(0, "CSC116", 8, 2));
            IOProcessing.outputDay(d1, 0, new PrintStream(testFile));
            String actual = Files.readString(p1);
            assertEquals("Day 1: " + d1.toString(), actual);
        } catch(Exception e) {
            fail();
        }
        //Test data with overflow
        try {
            Day d1 = new Day(0, 8, 0);
            d1.addSubTaskDynamically(new Task(0, "CSC116", 9, 0));
            IOProcessing.outputDay(d1, 1, new PrintStream(testFile));
            String actual = Files.readString(p1);
            assertEquals("1 overflows have occurred within schedule...\r\nDay 1: " + d1.toString(), actual);
        } catch(Exception e) {
            fail();
        }
    }

    /**
     * Tests writeSchedule() functionality
     */
    @Test
    public void outputSchedule() {
        //Test valid data
        String testFile = "data/s_day.txt";
        Path p1 = Path.of("data/s_day.txt");
        try {
            Day d1 = new Day(0, 8, 0);
            d1.addSubTaskDynamically(new Task(0, "CSC116", 8, 2));
            List<Day> list = new ArrayList<>();
            list.add(d1);
            IOProcessing.outputSchedule(list, 0, new PrintStream(testFile));
            String actual = Files.readString(p1);
            assertEquals("Day 1: " + d1.toString(), actual);
        } catch(Exception e) {
            fail();
        }
        //Test data with overflow
        try {
            Day d1 = new Day(0, 8, 0);
            d1.addSubTaskDynamically(new Task(0, "CSC116", 9, 0));
            List<Day> list = new ArrayList<>();
            list.add(d1);
            IOProcessing.outputSchedule(list, 1, new PrintStream(testFile));
            String actual = Files.readString(p1);
            assertEquals("1 overflows have occurred within schedule...\r\nDay 1: " + d1.toString(), actual);
        } catch(Exception e) {
            fail();
        }
    }

    /**
     * Tests read/write JBin file functionality
     */
    @Test
    public void readAndWriteJBinFile() {
        IOProcessing.writeJBinFile("data/schedule.jbin", "JIMMY JOHNS");
        assertEquals("JIMMY JOHNS", IOProcessing.readJBinFile("data/schedule.bin"));
    }
}