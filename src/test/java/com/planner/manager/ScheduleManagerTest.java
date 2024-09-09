package com.planner.manager;

import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.util.Time;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleManagerTest {
    @Test
    void addEvent() {
        ScheduleManager sm = new ScheduleManager();

        Card cloud = sm.addCard("Cloud", Card.Color.BLUE);
        Card school = sm.addCard("Bus", Card.Color.YELLOW);
        Card grass = sm.addCard("Grass", Card.Color.GREEN);
        Card plate = sm.addCard("Fruit", Card.Color.ORANGE);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        Calendar end = (Calendar) start.clone();
        end.set(Calendar.HOUR, end.get(Calendar.HOUR) + 1);
        Time.TimeStamp timestamp = new Time.TimeStamp(start, end);

        sm.addEvent("IndivE1", 0, timestamp, false, null);
        sm.addEvent("IndivE2", 1, timestamp, false, null);

        List<Calendar> dates = new ArrayList<>();

        dates.add(Time.getFormattedCalendarInstance(0));
        dates.add(Time.getFormattedCalendarInstance(2));

        sm.addEvent("RecurE1", 2, timestamp, true, dates);

        dates.add(Time.getFormattedCalendarInstance(4));

        sm.addEvent("RecurE2", 3, timestamp, true, dates);

        assertThrows(Exception.class,
                () -> sm.addEvent("RecurE3", 3, timestamp, true, null));

        List<Event> indivEvents = sm.getIndivEvents();

        assertEquals(2, indivEvents.size());

        assertEquals("IndivE1 BLUE " + timestamp + " false", eventToString(indivEvents.get(0)));
        assertEquals("IndivE2 YELLOW " + timestamp + " false", eventToString(indivEvents.get(1)));

        assertEquals("RecurE1 GREEN " + timestamp + " true" + " reoccurs 2 days", eventToString(sm.getRecurEvents().get(dates.get(0).get(Calendar.DAY_OF_WEEK) -1).get(0)));
        assertEquals("RecurE2 ORANGE " + timestamp + " true" + " reoccurs 3 days", eventToString(sm.getRecurEvents().get(dates.get(0).get(Calendar.DAY_OF_WEEK) -1).get(1)));
    }

    @Test
    void addCard() {
        ScheduleManager sm = new ScheduleManager();

        sm.addCard("school", Card.Color.BLUE);
        sm.addCard("work", Card.Color.ORANGE);
        sm.addCard("hobbies", Card.Color.RED);

        List<Card> cards = sm.getCards();

        assertEquals("school", cards.get(0).getName());
        assertEquals("work", cards.get(1).getName());
        assertEquals("hobbies", cards.get(2).getName());
    }

    @Test
    void addTask() {
        ScheduleManager sm = new ScheduleManager();
        sm.addCard("school", Card.Color.BLUE);
        sm.addCard("work", Card.Color.BLUE);
        sm.addCard("hobbies", Card.Color.BLUE);

        Calendar due = Calendar.getInstance();

        due.set(Calendar.HOUR, due.get(Calendar.HOUR) + 10);
        sm.addTask("homework", 3, due, 0);
        due.set(Calendar.HOUR, due.get(Calendar.HOUR) + 15);
        sm.addTask("meeting", 2, due, 1);
        due.set(Calendar.HOUR, due.get(Calendar.HOUR) + 20);
        sm.addTask("piano practice", 1, due, 2);

        assertEquals(3, sm.getTaskManager().size());
        assertEquals("Task [name=homework, total=3.0]", sm.getTaskManager().peek().toString());
        assertEquals("school", sm.getTaskManager().peek().getTag());
    }

    @Test
    void modTask() {
        ScheduleManager sm = new ScheduleManager();
        sm.addCard("school", Card.Color.BLUE);

        Calendar due = Calendar.getInstance();

        due.set(Calendar.HOUR, due.get(Calendar.HOUR) + 10);
        sm.addTask("homework", 3, due, 0);

        sm.modTask(0, "project", null, null, null);
        assertEquals("Task [name=project, total=3.0]", sm.getTaskManager().peek().toString());

        sm.modTask(0, null, 4.0, null, null);
        assertEquals("Task [name=project, total=4.0]", sm.getTaskManager().peek().toString());

        assertThrows(IllegalArgumentException.class, () -> sm.modTask(10, "non-existent task", null, null, null));
    }

    @Test
    void modCard() {
        ScheduleManager sm = new ScheduleManager();

        sm.addCard("school", Card.Color.BLUE);

        sm.modCard(0, "school work", Card.Color.VIOLET);

        assertEquals("school work", sm.getCards().get(0).getName());

        assertEquals(Card.Color.VIOLET, sm.getCards().get(0).getColor());

        sm.modCard(0, null, Card.Color.INDIGO);

        assertEquals(Card.Color.INDIGO, sm.getCards().get(0).getColor());

        assertThrows(IllegalArgumentException.class, () -> sm.modTask(10, "non-existent card", null, null, null));
    }

    @Test
    void modEvent() {
        ScheduleManager sm = new ScheduleManager();

        Card cloud = sm.addCard("Cloud", Card.Color.BLUE);
        Card fire = sm.addCard("Fire", Card.Color.RED);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        Calendar end = (Calendar) start.clone();
        end.set(Calendar.HOUR, end.get(Calendar.HOUR) + 1);
        Time.TimeStamp timestamp = new Time.TimeStamp(start, end);

        sm.addEvent("IndivE1", 0, timestamp, false, null);

        List<Calendar> dates = new ArrayList<>();

        dates.add(Time.getFormattedCalendarInstance(1));
        dates.add(Time.getFormattedCalendarInstance(2));

        sm.addEvent("RecurE1", 0, timestamp, true, dates);

        sm.modEvent(0, null, 1, timestamp, null);
        assertEquals("IndivE1 RED " + timestamp + " false", eventToString(sm.getIndivEvents().get(0)));

        assertThrows(IllegalArgumentException.class, () -> sm.modEvent(0, null, 1, timestamp, dates));

        dates.add(Time.getFormattedCalendarInstance(3));
        dates.add(Time.getFormattedCalendarInstance(4));

        sm.modEvent(1, null, null, timestamp, dates);
        assertEquals("RecurE1 BLUE " + timestamp + " true" + " reoccurs 4 days", eventToString(sm.getRecurEvents().get(dates.get(0).get(Calendar.DAY_OF_WEEK) - 1).get(0)));

        assertNull(sm.modEvent(10, "non-existent event", null, null, null));

    }

    @Test
    void deleteTask() {
        ScheduleManager sm = new ScheduleManager();
        sm.addCard("school", Card.Color.BLUE);

        Calendar due = Calendar.getInstance();
        due.set(Calendar.HOUR, due.get(Calendar.HOUR) + 10);
        sm.addTask("homework", 3, due, 0);

        assertTrue(sm.deleteTask(0));
        assertFalse(sm.deleteTask(0));
        assertEquals(0, sm.getTaskManager().size());
    }

    @Test
    void deleteCard() {
        ScheduleManager sm = new ScheduleManager();

        sm.addCard("school", Card.Color.RED);

        Calendar due = Calendar.getInstance();
        due.set(Calendar.HOUR, due.get(Calendar.HOUR) + 10);
        sm.addTask("homework", 3, due, 0);

        assertTrue(sm.deleteCard(0));
        assertFalse(sm.deleteCard(0));
        assertEquals(0, sm.getCards().size());

        assertNull(sm.getTask(0).getCard());
    }

    @Test
    void deleteEvent() {
        ScheduleManager sm = new ScheduleManager();

        Card cloud = sm.addCard("Cloud", Card.Color.BLUE);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        Calendar end = (Calendar) start.clone();
        end.set(Calendar.HOUR, end.get(Calendar.HOUR) + 1);
        Time.TimeStamp timestamp = new Time.TimeStamp(start, end);

        sm.addEvent("IndivE1", 0, timestamp, false, null);

        List<Calendar> dates = new ArrayList<>();

        dates.add(Time.getFormattedCalendarInstance(1));
        dates.add(Time.getFormattedCalendarInstance(2));

        sm.addEvent("RecurE1", 0, timestamp, true, dates);

        assertTrue(sm.deleteEvent(0));
        assertEquals(0, sm.getIndivEvents().size());

        assertTrue(sm.deleteEvent(1));
        for (List<Event> dayEvents : sm.getRecurEvents()) {
            assertEquals(0, dayEvents.size());
        }

        assertFalse(sm.deleteEvent(2));
    }

    private String eventToString(Event e) {
        StringBuilder eventSb = new StringBuilder();
        eventSb.append(e.getName())
                .append(" ")
                .append(e.getCard().getColor())
                .append(" ")
                .append(e.getTimeStamp())
                .append(" ")
                .append(e.isRecurring());
        if (e.isRecurring()) {
            eventSb.append(" reoccurs ")
                    .append(e.getDays().length)
                    .append(" days");
        }
        return eventSb.toString();
    }
}