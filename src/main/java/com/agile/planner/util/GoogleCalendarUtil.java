package com.agile.planner.util;

import com.agile.planner.data.Task;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.List;
import java.util.Scanner;

/**
 * Handler of all core linker and utility functions for GoogleIO
 *
 * @author Andrew Roe
 */
public class GoogleCalendarUtil {

    public static Event formatTaskToEvent(Task task) {
        Event event = new Event()
                .setSummary(task.getName())
                .setDescription("Agile Planner\n\neb007aba6df2559a02ceb17ddba47c85b3e2b930");
        DateTime startDateTime = new DateTime("2024-03-06T09:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2024-03-06T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);
//        event.setColorId("9"); https://chat.openai.com/share/95995dc1-1061-4e88-81e6-fc15a72e8370
        return event;
    }

    public static List<Task> formatEventsToTasks(List<Event> items) {
        for(Event i1 : items) {
            String title = i1.getSummary();
            String description = i1.getDescription();
            Scanner strScanner = new Scanner(description);
            if(strScanner.hasNextLine()) {
                String date = strScanner.nextLine();
            }
            if(strScanner.hasNextLine()) {
                String hashcode = strScanner.nextLine();
            }
        }
        //todo
        items.get(0).getSummary();
        return null;
    }

}