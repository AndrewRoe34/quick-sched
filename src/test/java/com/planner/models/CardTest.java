package com.planner.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testToString() {
        assertEquals("Math", c1.toString());
    }

    @Test
    void getTitle() {
        assertEquals("Math", c1.getName());
    }

    @Test
    void getColor() {
        assertEquals(Card.Color.LIGHT_BLUE, c1.getColor());
    }

    @Test
    void setColor() {
        c1.setColor(Card.Color.INDIGO);
        assertEquals(Card.Color.INDIGO, c1.getColor());
    }
}