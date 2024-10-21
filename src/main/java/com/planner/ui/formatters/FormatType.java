package com.planner.ui.formatters;

import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.ui.tables.TableFormatter;
import com.planner.util.Time;

import java.util.Calendar;

public class FormatType {
    private static final String resetColor = "\u001B[0m";

    public static String formatCard(Card card) {
        return "Card Details:" +
                '\n' +
                "-------------------------------------------------------------" +
                '\n' +
                "ID   \t" +
                card.getId() +
                '\n' +
                "Name \t" +
                card.getName() +
                '\n' +
                "Color\t" +
                TableFormatter.getColorANSICode(card.getColor()) +
                card.getColor() +
                resetColor +
                '\n' +
                "-------------------------------------------------------------";
    }

    public static String formatTask(Task task) {
        Calendar scheduleTime = Calendar.getInstance();

        return (( (!Time.doDatesMatch(task.getDueDate(), scheduleTime) && task.getDueDate().compareTo(scheduleTime) < 0) || task.getTotalHours() == 0)? "Archived " : "Active ") +
                "Task Details: " +
                '\n' +
                "-------------------------------------------------------------" +
                '\n' +
                "ID          \t" +
                task.getId() +
                '\n' +
                "Name        \t" +
                task.getName() +
                '\n' +
                "Card        \t" +
                (task.getCard() == null? "-" : TableFormatter.getColorANSICode(task.getCard().getColor()) + task.getTag()) +
                resetColor +
                '\n' +
                "Total Hours \t" +
                task.getTotalHours() +
                '\n' +
                "Due Date    \t" +
                task.getDateStamp() +
                '\n' +
                "-------------------------------------------------------------";
    }

    public static String formatEvent(Event event) {
        return  (event.isRecurring()? "Recurring " : "Individual ") +
                "Event Details: " +
                '\n' +
                "-------------------------------------------------------------" +
                '\n' +
                "ID              \t" +
                event.getId() +
                '\n' +
                "Name            \t" +
                event.getName() +
                '\n' +
                "Card            \t" +
                (event.getCard() == null? "-" : TableFormatter.getColorANSICode(event.getCard().getColor()) + event.getCard().getName()) +
                resetColor +
                '\n' +
                "Timestamp       \t" +
                event.get24HourTimeStampString() +
                '\n' +
                (event.isRecurring()? "Recurrence Days:\t" + event.getDaysString() + '\n' : "") +
                "-------------------------------------------------------------";
    }
}
