package com.planner.util;

import com.planner.models.Card;
import com.planner.models.Task;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Handler of all core linker and utility functions for GoogleIO
 *
 * @author Andrew Roe
 * @author Abah Olotuche Gabriel
 */
public class GoogleCalendarUtil {

    public static Event formatTaskToGoogleEvent(Task.SubTask subTask) {
        Task task = subTask.getParentTask();
        Event event = new Event().setSummary(task.getName()); //todo need to display label names with given Task

        StringBuilder sb = new StringBuilder("Due: ");
        sb.append(task.getDueDate().get(Calendar.YEAR))
                .append("-")
                .append(task.getDueDate().get(Calendar.MONTH) + 1)
                .append("-")
                .append(task.getDueDate().get(Calendar.DAY_OF_MONTH))
                .append("\n\n");

        sb.append("Agile Planner\n\neb007aba6df2559a02ceb17ddba47c85b3e2b930");
        event.setDescription(sb.toString());

        Time.TimeStamp timeStamp = subTask.getTimeStamp();

        DateTime startDateTime = new DateTime(timeStamp.getStart().getTime());
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(timeStamp.getEnd().getTime());
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setEnd(end);

        // will use a switch case here with regards to setting the color ids
        if (task.getColor() == null)
            return event;

        event.setColorId(convertAnsiToGoogleColor(task.getColor()));

        return event;
    }

    public static List<String> formatGoogleEventsToTasks(List<Event> items) throws IOException {
        List<String> tasks = new ArrayList<>();
        for(Event i1 : items) {
            if(i1.getDescription() != null && i1.getDescription().contains("eb007aba6df2559a02ceb17ddba47c85b3e2b930")) {
                String title = i1.getSummary();
                String start = i1.getStart().toPrettyString();
                String end = i1.getEnd().toPrettyString();
                tasks.add("title=" + title + ", start=" + start + ", end=" + end);
            }
        }
        return tasks;
    }


    public static Event formatEventToGoogleEvent(com.planner.models.Event e) {
        Event event = new Event().setSummary(e.getName());

        event.setDescription("Agile Planner\n\neb007aba6df2559a02ceb17ddba47c85b3e2b930");

        DateTime startDateTime = new DateTime(e.getTimeStamp().getStart().getTime());
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(e.getTimeStamp().getEnd().getTime());
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setEnd(end);

        if (e.getCard() == null)
            return event;

        event.setColorId(convertAnsiToGoogleColor(e.getCard().getColor()));

        return event;
    }

    private static String convertAnsiToGoogleColor(Card.Color color) {
        switch (color) {
            case RED:
                return "11";
            case ORANGE:
                return "6";
            case YELLOW:
                return "5";
            case GREEN:
                return "10";
            case LIGHT_GREEN:
                return "2";
            case LIGHT_BLUE:
                return "7";
            case BLUE:
                return "1";
            case INDIGO:
                return "3";
            case VIOLET:
                return "9";
            case BLACK:
                return "8";
            case LIGHT_CORAL:
                return "4";
        }
        return null;
    }
}