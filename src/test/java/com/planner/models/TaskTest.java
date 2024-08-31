package com.planner.models;

import com.planner.util.Time;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;
    private int id;
    private int numOfDays;
    private int numOfHours;
    private String tag;

    @BeforeEach
    void setUp() {
        id = 0;
        numOfDays = 2;
        numOfHours = 8;
        tag = "School";

        task = new Task(id, "Solve math homework", numOfHours, numOfDays);
        task.setTag(tag);
    }

    @Test
    void getId() {
        assertEquals(id, task.getId());
    }

    @Test
    void getTag() {
        assertEquals(tag, task.getTag());
    }

    @Test
    void setTag() {
        task.setTag("Homework");
        assertEquals("Homework", task.getTag());
    }

    @Test
    void addSubTask() {
        int subTask1Hours = 3;
        int subTask2Hours = 2;

        Calendar start = Calendar.getInstance();
        Calendar end1 = Calendar.getInstance();
        end1.add(Calendar.HOUR, subTask1Hours);
        Calendar end2 = Calendar.getInstance();
        end2.add(Calendar.HOUR, subTask2Hours);

        Time.TimeStamp timeStamp1 = new Time.TimeStamp(start, end1);
        Time.TimeStamp timeStamp2 = new Time.TimeStamp(start, end2);

        Task.SubTask subTask1 = task.addSubTask(subTask1Hours, false, timeStamp1);
        Task.SubTask subTask2 = task.addSubTask(subTask2Hours, false, timeStamp2);
        
        assertEquals(3, subTask1.getSubTaskHours());
        assertEquals(task, subTask1.getParentTask());
        assertFalse(subTask1.isOverflow());
        assertEquals(timeStamp1, subTask1.getTimeStamp());
        assertEquals(0, subTask1.compareTo(subTask2));
    }

    @Test
    void reset() {
        task.reset();
        assertEquals(0, task.getSubTotalHoursRemaining() - task.getTotalHours());
        assertEquals(0, task.getAverageNumHours());
    }

    @Test
    void getSubTotalHoursRemaining() {
        int subTaskHours = 3;

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.HOUR, subTaskHours);

        Time.TimeStamp timeStamp = new Time.TimeStamp(start, end);

        task.addSubTask(subTaskHours, false, timeStamp);

        assertEquals(5, task.getSubTotalHoursRemaining());
        task.reset();
        assertEquals(8, task.getSubTotalHoursRemaining());
    }

    @Test
    void compareTo() {
        Task task1 = new Task(1, "Study", 5, 3);
        Task task2 = new Task(1, "Study", 5, 3);

        assertEquals(0, task1.compareTo(task2));

        Calendar start = Calendar.getInstance();
        Time.TimeStamp timeStamp = new Time.TimeStamp(start, task2.getDueDate());

        task2.addSubTask(2, false, timeStamp);

        assertEquals(-1, task1.compareTo(task2));

        task1.addSubTask(3, false, timeStamp);

        assertEquals(1, task1.compareTo(task2));
    }

    @Test
    void getName() {
        assertEquals("Solve math homework", task.getName());
    }

    @Test
    void getDueDate() {
        Calendar dueDate = Time.getFormattedCalendarInstance(numOfDays);

        assertEquals(dueDate, task.getDueDate());
    }

    @Test
    void getDateStamp() {
        Calendar dueDate = Time.getFormattedCalendarInstance(numOfDays);

        int day = dueDate.get(Calendar.DAY_OF_MONTH);
        int month = dueDate.get(Calendar.MONTH) + 1;
        int year = dueDate.get(Calendar.YEAR);

        StringBuilder dateStamp = new StringBuilder();

        if (day < 10)  dateStamp.append("0");
        dateStamp.append(day).append("-");
        if (month < 10)  dateStamp.append("0");
        dateStamp.append(month).append("-");
        dateStamp.append(year);

        assertEquals(dateStamp.toString(), task.getDateStamp());
    }

    @Test
    void getTotalHours() {
        assertEquals(numOfHours, task.getTotalHours());
    }

    @Test
    void setTotalHours() {
        task.setTotalHours(numOfHours + 4);
        assertEquals(numOfHours + 4, task.getTotalHours());

        task.setTotalHours(numOfHours);
        assertEquals(numOfHours, task.getTotalHours());
    }

    @Test
    void setAverageNumHours() {
        Calendar date = Calendar.getInstance();
        Calendar dueDate = task.getDueDate();

        int days = Time.determineRangeOfDays(date, dueDate) + 1;

        double avgNumHours = (double) numOfHours / days;
        avgNumHours += avgNumHours % days == 0 ? 0 : 1;

        task.setAverageNumHours(date);

        assertEquals(avgNumHours, task.getAverageNumHours());
    }

    @Test
    void getAverageNumHours() {
        task.reset();
        assertEquals(0, task.getAverageNumHours());
    }

    @Test
    void getColor() {
        assertNull(task.getColor());
    }

    @Test
    void setColor() {
        task.setColor(Card.Color.YELLOW);
        assertEquals(Card.Color.YELLOW, task.getColor());
    }

    @Test
    void testToString() {
        String taskString = String.format("Task [name=%s, total=%s]", task.getName(), task.getTotalHours());
        assertEquals(taskString, task.toString());
    }

    @Test
    void testEquals() {
        assertNotEquals(null, task);

        Task task1 = new Task(1, "Study", 5, 3);
        Task task2 = new Task(1, "Study", 5, 3);
        Task task3 = new Task(2, "Work", 5, 3);

        assertEquals(task1, task2);
        assertNotEquals(task1, task3);
    }
}