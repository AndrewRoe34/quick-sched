package com.planner.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests all Card methods
 *
 * @author Andrew Roe
 */
class CardTest {

    private Card c1;

    @BeforeEach
    void setUp() {
        c1 = new Card(0, "Math", Card.Color.LIGHT_BLUE);
    }

    @Test
    void getId() {
        assertEquals(0, c1.getId());
    }

    @Test
    void setTitle() {
        c1.setName("Chemistry");
        assertEquals("Chemistry", c1.getName());
    }

    @Test
    void setCardTasks() {
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task(0, "F", 4.0, 0));
        c1.setCardTasks(taskList);
        assertEquals(taskList, c1.getTask());
    }

    @Test
    void addTask() {
        Task t1 = new Task(0, "Read Ch4", 4, 1);
        assertTrue(c1.getTask().isEmpty());
        c1.addTask(t1);
        assertEquals(1, c1.getTask().size());
        assertEquals(t1, c1.getTask().get(0));
    }

    @Test
    void removeTaskBool() {
        Task t1 = new Task(0, "Read Ch4", 4, 1);
        assertTrue(c1.getTask().isEmpty());
        c1.addTask(t1);
        assertEquals(1, c1.getTask().size());
        assertEquals(t1, c1.getTask().get(0));

        assertTrue(c1.removeTask(t1));
        assertTrue(c1.getTask().isEmpty());
    }

    @Test
    void removeTaskObject() {
        Task t1 = new Task(0, "Read Ch4", 4, 1);
        assertTrue(c1.getTask().isEmpty());
        c1.addTask(t1);
        assertEquals(1, c1.getTask().size());
        assertEquals(t1, c1.getTask().get(0));

        assertEquals(t1, c1.removeTask(0));
        assertTrue(c1.getTask().isEmpty());
    }

    @Test
    void testToString() {
        assertEquals("Math", c1.toString());
    }

    @Test
    void getTask() {
        Task t1 = new Task(0, "Read Ch4", 4, 1);
        assertTrue(c1.getTask().isEmpty());
        c1.addTask(t1);
        assertEquals(1, c1.getTask().size());
        List<Task> taskList = c1.getTask();
        assertEquals(1, taskList.size());
        assertEquals(t1, taskList.get(0));
    }

    @Test
    void getTitle() {
        assertEquals("Math", c1.getName());
    }

    @Test
    void getColorId() {
        assertEquals(Card.Color.LIGHT_BLUE, c1.getColorId());
    }

    @Test
    void setColorId() {
        c1.setColorId(Card.Color.INDIGO);
        assertEquals(Card.Color.INDIGO, c1.getColorId());
    }
}