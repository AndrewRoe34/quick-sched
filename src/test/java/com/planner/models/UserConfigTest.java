package com.planner.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserConfigTest {

    UserConfig userConfig;

    @BeforeEach
    void setUp() {
        userConfig = new UserConfig(new int[]{8, 20}, new int[]{8, 8, 8, 8, 8, 8, 8}, new double[]{0.0, 4.0},14, 5, false, true,
                false, true, true);
    }

    @Test
    void setRange() {
        assertEquals(8, userConfig.getDailyHoursRange()[0]);
        assertEquals(20, userConfig.getDailyHoursRange()[1]);
        userConfig.setDailyHoursRange(new int[]{2, 10});
        assertEquals(2, userConfig.getDailyHoursRange()[0]);
        assertEquals(10, userConfig.getDailyHoursRange()[1]);

        // test for exception
    }

    @Test
    void setWeek() {
        for (int day : userConfig.getHoursPerDayOfWeek()) {
            assertEquals(8, day);
        }
        userConfig.setHoursPerDayOfWeek(new int[]{2, 2, 2, 2, 2, 2, 2});
        for (int day : userConfig.getHoursPerDayOfWeek()) {
            assertEquals(2, day);
        }

        // test for exception
    }

    @Test
    void setMaxDays() {
        assertEquals(14, userConfig.getMaxDays());
        userConfig.setMaxDays(4);
        assertEquals(4, userConfig.getMaxDays());

        // test for exception
    }

    @Test
    void setArchiveDays() {
        assertEquals(5, userConfig.getArchiveDays());
        userConfig.setArchiveDays(10);
        assertEquals(10, userConfig.getArchiveDays());

        // test for exception
    }

    @Test
    void setPriority() {
        assertFalse(userConfig.isPriority());
        userConfig.setPriority(true);
        assertTrue(userConfig.isPriority());
    }

    @Test
    void setOverflow() {
        assertTrue(userConfig.isOverflow());
        userConfig.setOverflow(false);
        assertFalse(userConfig.isOverflow());
    }

//    @Test
//    void setMinHours() {
//        assertEquals(0.5, new double[]{userConfig.getSubtaskRange()});
//        userConfig.setSubtaskRange(new double[]{1.0});
//        assertEquals(1.0, new double[]{userConfig.getSubtaskRange()});
//
//        // test for exception
//    }

    @Test
    void setOptimizeDay() {
        assertFalse(userConfig.isOptimizeDay());
        userConfig.setOptimizeDay(true);
        assertTrue(userConfig.isOptimizeDay());
    }

    @Test
    void setDefaultAtStart() {
        assertTrue(userConfig.isDefaultAtStart());
        userConfig.setDefaultAtStart(false);
        assertFalse(userConfig.isDefaultAtStart());
    }

}