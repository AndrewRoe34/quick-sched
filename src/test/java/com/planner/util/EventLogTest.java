package com.planner.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests EventLog functionality
 */
public class EventLogTest {

    /** EventLog of all system actions */
    private static EventLog eventLog;

    /**
     * Initial setup of EventLog
     *
     * @throws FileNotFoundException if file not locatable
     */
    @BeforeEach
    public void before() throws FileNotFoundException {
        eventLog = EventLog.getEventLog();
    }

    @Test
    public void reportTaskAction() {
        fail();
    }

    @Test
    public void reportWeekEdit() {
        fail();
    }

    @Test
    public void reportDayAction() {
        fail();
    }

    @Test
    public void reportException() {
        fail();
    }

    @Test
    public void reportUserLogin() {
        fail();
    }

    @Test
    public void reportExitSession() {
        fail();
    }
}