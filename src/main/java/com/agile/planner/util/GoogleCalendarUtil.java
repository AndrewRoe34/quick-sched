package com.agile.planner.util;

import com.agile.planner.data.Task;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

/**
 * Handler of all core linker and utility functions for GoogleIO
 *
 * @author Andrew Roe
 */
public class GoogleCalendarUtil {

    public static Event formatTaskToEvent(Task task, int subTaskHours, int dayIdx, int dayHour) {
        Event event = new Event().setSummary(task.getName()); //todo need to display labels with given Task
        StringBuilder sb = new StringBuilder("Due: ");
        sb.append(task.getDueDate().get(Calendar.YEAR))
                .append("-")
                .append(task.getDueDate().get(Calendar.MONTH) + 1)
                .append("-")
                .append(task.getDueDate().get(Calendar.DAY_OF_MONTH))
                .append("\n\n");
        CheckList cl = task.getCheckList();
        if(cl != null) {
            sb.append(cl.getName()).append(":<ul>");
            for(CheckList.Item i1 : cl.getItems()) {
                sb.append("<li>")
                        .append(i1.getDescription())
                        .append("</li>");
            }
            sb.append("</ul>\n");
        }

        sb.append("Agile Planner\n\neb007aba6df2559a02ceb17ddba47c85b3e2b930");
        event.setDescription(sb.toString());

        Calendar now = Time.getFormattedCalendarInstance(dayIdx);
        now.add(Calendar.HOUR, dayHour);
        DateTime startDateTime = new DateTime(now.getTime());
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        event.setStart(start);

        now.add(Calendar.HOUR, subTaskHours);
        DateTime endDateTime = new DateTime(now.getTime());
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setEnd(end);
        //https://chat.openai.com/share/95995dc1-1061-4e88-81e6-fc15a72e8370
        event.setColorId("1");

        return event;
    }

    public static List<String> formatEventsToTasks(List<Event> items) throws IOException {
        List<String> tasks = new ArrayList<>();
        for(Event i1 : items) {
            if(i1.getDescription().contains("eb007aba6df2559a02ceb17ddba47c85b3e2b930")) {
                String title = i1.getSummary();
                String start = i1.getStart().toPrettyString();
                String end = i1.getEnd().toPrettyString();
                tasks.add("title=" + title + ", start=" + start + ", end=" + end);
            }
        }
        return tasks;
    }

}