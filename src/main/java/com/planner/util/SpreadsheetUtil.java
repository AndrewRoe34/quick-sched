package com.planner.util;

import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Class to handle utility functions for SpreadsheetIO.
 *
 * @author Abah Olotuche Gabriel
 * @author Andrew Roe
 */
public class SpreadsheetUtil {
    /**
     * Creates an event Worksheet if there are any events in the schedule.
     *
     * @param wb Workbook that owns the created Worksheet.
     * @param schedule Schedule that will be checked for events.
     *
     * @return A valid 'Events' Worksheet if any events are found in the schedule,
     * else {@code null}.
     */
    public static Worksheet createEventSheet(Workbook wb, List<Day> schedule) {
        Worksheet eventSheet = null;

        for (Day day : schedule) {
            if (!day.getEventList().isEmpty()) {
                eventSheet = wb.newWorksheet("Events");

                eventSheet.value(0, 0, "ID");
                eventSheet.value(0, 1, "NAME");
                eventSheet.value(0, 2, "COLOR");
                eventSheet.value(0, 3, "TIME");
                eventSheet.value(0, 4, "DATE");
                eventSheet.value(0, 5, "DAYS");

                eventSheet.range(
                        0,
                        0,
                        0,
                        5
                ).style().bold().fillColor(org.dhatim.fastexcel.Color.GRAY7).set();

                break;
            }
        }

        return eventSheet;
    }

    /**
     * Creates a task Worksheet if there are any tasks in the schedule.
     *
     * @param wb Workbook that owns the created Worksheet.
     * @param schedule Schedule that will be checked for tasks.
     *
     * @return A valid 'Tasks' Worksheet if any tasks are found in the schedule,
     * else {@code null}.
     */
    public static Worksheet createTaskSheet(Workbook wb, List<Day> schedule) {
        Worksheet taskSheet = null;

        for (Day day : schedule) {
            if (day.getNumSubTasks() != 0) {
                taskSheet = wb.newWorksheet("Tasks");

                taskSheet.value(0, 0, "ID");
                taskSheet.value(0, 1, "NAME");
                taskSheet.value(0, 2, "COLOR");
                taskSheet.value(0, 3, "HOURS");
                taskSheet.value(0, 4, "TIME");
                taskSheet.value(0, 5, "DATE");
                taskSheet.value(0, 6, "DUE");

                taskSheet.range(
                        0,
                        0,
                        0,
                        6
                ).style().bold().fillColor(org.dhatim.fastexcel.Color.GRAY7).set();

                break;
            }
        }

        return taskSheet;
    }

    /**
     * Creates a board Worksheet if there are any cards were created.
     *
     * @param wb Workbook that owns the created Worksheet.
     * @param cards Cards that will be checked.
     *
     * @return A valid 'Boards' Worksheet if any cards are found,
     * else {@code null}.
     */
    public static Worksheet createBoardSheet(Workbook wb, List<Card> cards) {
        Worksheet boardSheet = null;

        if (cards.isEmpty())
            return boardSheet;

        boardSheet = wb.newWorksheet("Board");

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);

            boardSheet.value(0, i, card.getTitle());
            boardSheet.style(0, i).fontColor(
                    convertColorsEnumToFastExcelColorEnum(card.getColorId())
            ).bold().fillColor(org.dhatim.fastexcel.Color.GRAY7).set();
        }

        return boardSheet;
    }

    /**
     * Creates a schedule Worksheet.
     *
     * @param wb Workbook that owns the created Worksheet.
     * @param schedule Schedule to be used for Worksheet creation.
     *
     * @return A valid 'Schedule' Worksheet.
     */
    public static Worksheet createScheduleSheet(Workbook wb, List<Day> schedule) {
        Worksheet scheduleSheet = wb.newWorksheet("Schedule");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        for (int i = 0; i < schedule.size(); i++) {
            Day day = schedule.get(i);

            scheduleSheet.value(0, i, sdf.format(day.getDate().getTime()));
        }

        scheduleSheet.range(
                0,
                0,
                0,
                schedule.size() - 1
        ).style().bold().fillColor(org.dhatim.fastexcel.Color.GRAY7).set();

        return scheduleSheet;
    }

    /**
     * Populates an event Worksheet if it exists.
     *
     * @param eventSheet The nullable {@code Events} Worksheet.
     * @param schedule Schedule that will be used to populate the Worksheet.
     */
    public static void populateEventSheet(Worksheet eventSheet, List<Day> schedule) {
        if (eventSheet == null)
            return;

        int eventCount = 0;
        for (Day day : schedule) {
            for (Event event : day.getEventList()) {

                int id = event.getId();
                String name = event.getName();
                String color = event.getColor().toString();
                String time = event.getTimeStamp().toString();
                String date = event.isRecurring() ? "-" : event.getDateStamp();
                String days = event.isRecurring() ? Arrays.toString(event.getDays()) : "-";

                eventCount++;
                eventSheet.value(eventCount, 0, id);
                eventSheet.value(eventCount, 1, name);
                eventSheet.value(eventCount, 2, color);
                eventSheet.value(eventCount, 3, time);
                eventSheet.value(eventCount, 4, date);
                eventSheet.value(eventCount, 5, days);
            }
        }
    }

    /**
     * Populates a task Worksheet if it exists.
     *
     * @param taskSheet The nullable {@code Tasks} Worksheet.
     * @param schedule Schedule that will be used to populate the Worksheet.
     */
    public static void populateTaskSheet(Worksheet taskSheet, List<Day> schedule) {
        if (taskSheet == null)
            return;

        int taskCount = 0;
        for (Day day : schedule) {
            for (Task.SubTask subTask : day.getSubTaskList()) {

                Task parentTask = subTask.getParentTask();

                int id = parentTask.getId();
                String name = parentTask.getName();
                String color = parentTask.getColor() == null ? "None" : parentTask.getColor().toString();
                double hours = subTask.getSubTaskHours();
                String time = subTask.getTimeStamp().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String date = sdf.format(subTask.getTimeStamp().getStart().getTime());
                String due = parentTask.getDateStamp();

                taskCount++;
                taskSheet.value(taskCount, 0, id);
                taskSheet.value(taskCount, 1, name);
                taskSheet.value(taskCount, 2, color);
                taskSheet.value(taskCount, 3, hours);
                taskSheet.value(taskCount, 4, time);
                taskSheet.value(taskCount, 5, date);
                taskSheet.value(taskCount, 6, due);
            }
        }
    }

    /**
     * Populates a board Worksheet if it exists.
     *
     * @param boardSheet The nullable {@code Boards} Worksheet.
     * @param cards Cards that will be used to populate the Worksheet.
     * @param archivedTasks Sequence of archived tasks that will be used to set
     *                      a flag on each task in a card's column.
     */
    public static void populateBoardSheet(Worksheet boardSheet, List<Card> cards, PriorityQueue<Task> archivedTasks) {
        if (boardSheet == null)
            return;

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);

            for (int j = 0; j < card.getTask().size(); j++) {
                String taskString = getCardTaskString(card, j, archivedTasks);

                boardSheet.value(j + 1, i, taskString);
            }
        }
    }

    /**
     * Populates a schedule Worksheet if it exists.
     *
     * @param scheduleSheet The nullable {@code Schedule} Worksheet.
     * @param schedule Schedule that will be used to populate the Worksheet.
     * @param userConfig User configuration settings that will be checked
     *                   for the user's choice to enable/disable coloring.
     *                   This choice affects the font color of each event/task
     *                   under a day's column in the schedule.
     */
    public static void populateScheduleSheet(Worksheet scheduleSheet, List<Day> schedule, UserConfig userConfig) {
        for (int i = 0; i < schedule.size(); i++) {
            Day day = schedule.get(i);

            List<String> arrangedIDs = arrangeTasksAndEventsInOrderOfOccurrence(day);
            for (int j = 0; j < arrangedIDs.size(); j++) {
                String id = arrangedIDs.get(j);

                Time.TimeStamp ts = null;
                String name = "";
                Card.Color color = null;

                StringBuilder output = new StringBuilder();

                int index = Integer.parseInt(String.valueOf(id.charAt(1)));
                if (id.charAt(0) == 't') {
                    Task.SubTask subTask = day.getSubTask(index);

                    ts = subTask.getTimeStamp();
                    name = subTask.getParentTask().getName();
                    color = subTask.getParentTask().getColor();
                }
                else if (id.charAt(0) == 'e') {
                    Event event = day.getEvent(index);

                    ts = event.getTimeStamp();
                    name = event.getName();
                    color = event.getColor();
                }

                output.append(ts).append(" - ").append(name);

                scheduleSheet.value(j + 1, i, output.toString());

                scheduleSheet.style(j + 1, i).fontColor(
                        convertColorsEnumToFastExcelColorEnum(color)
                ).set();
            }
        }
    }

    private static String convertColorsEnumToFastExcelColorEnum(Card.Color colorID) {
        switch (colorID) {
            case RED:
                return org.dhatim.fastexcel.Color.RED;
            case LIGHT_GREEN:
                return org.dhatim.fastexcel.Color.LIGHT_GREEN;
            case BLUE:
                return org.dhatim.fastexcel.Color.DARK_SKY_BLUE;
            case GREEN:
                return org.dhatim.fastexcel.Color.GREEN;
            case INDIGO:
                return org.dhatim.fastexcel.Color.INDIGO;
            case ORANGE:
                return org.dhatim.fastexcel.Color.ORANGE;
            case VIOLET:
                return org.dhatim.fastexcel.Color.VIOLET;
            case YELLOW:
                return org.dhatim.fastexcel.Color.YELLOW;
            case LIGHT_BLUE:
                return org.dhatim.fastexcel.Color.BABY_BLUE;
            case LIGHT_CORAL:
                return org.dhatim.fastexcel.Color.CORAL;
            default:
                return org.dhatim.fastexcel.Color.BLACK;
        }
    }

    private static String getCardTaskString(Card card, int index, PriorityQueue<Task> archivedTasks) {
        Task task = card.getTask().get(index);

        String taskString = "";
        if (archivedTasks.contains(task))
            taskString += "*";

        taskString += task.toString();

        return taskString;
    }

    private static List<String> arrangeTasksAndEventsInOrderOfOccurrence(Day day) {
        int[] taskEventPair = new int[2];

        List<String> arrangedIDs = new ArrayList<>();

        for (int i = 0; i < (day.getNumEvents() + day.getNumSubTasks()); i++) {
            if (taskEventPair[0] >= day.getNumSubTasks()) {
                arrangedIDs.add("e" + taskEventPair[1]);
                taskEventPair[1]++;
            }
            else if (taskEventPair[1] >= day.getNumEvents()) {
                arrangedIDs.add("t" + taskEventPair[0]);
                taskEventPair[0]++;
            }
            else {
                Event e1 = day.getEvent(taskEventPair[1]);
                Task.SubTask st1 = day.getSubTask(taskEventPair[0]);

                if (Time.isBefore(e1.getTimeStamp().getStart(), st1.getTimeStamp().getStart())) {
                    arrangedIDs.add("e" + taskEventPair[1]);
                    taskEventPair[1]++;
                }
                else {
                    arrangedIDs.add("t" + taskEventPair[0]);
                    taskEventPair[0]++;
                }
            }
        }

        return arrangedIDs;
    }
}
