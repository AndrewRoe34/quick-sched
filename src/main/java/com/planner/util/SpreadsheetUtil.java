package com.planner.util;

import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.schedule.day.Day;
import org.dhatim.fastexcel.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Class to handle utility functions for SpreadsheetIO.
 *
 * @author Abah Olotuche Gabriel
 */
public class SpreadsheetUtil {
    public static String convertColorsEnumToFastExcelColorEnum(Card.Colors colorID) {
        switch (colorID) {
            case RED:
                return Color.RED;
            case LIGHT_GREEN:
                return Color.LIGHT_GREEN;
            case BLUE:
                return Color.DARK_SKY_BLUE;
            case GREEN:
                return Color.GREEN;
            case INDIGO:
                return Color.INDIGO;
            case ORANGE:
                return Color.ORANGE;
            case VIOLET:
                return Color.VIOLET;
            case YELLOW:
                return Color.YELLOW;
            case LIGHT_BLUE:
                return Color.BABY_BLUE;
            case LIGHT_CORAL:
                return Color.CORAL;
            default:
                return Color.BLACK;
        }
    }

    public static String getCardTaskString(Card card, int index, PriorityQueue<Task> archivedTasks) {
        Task task = card.getTask().get(index);

        String taskString = "";
        if (archivedTasks.contains(task))
            taskString += "*";

        taskString += task.toString();

        return taskString;
    }

    public static List<String> arrangeTasksAndEventsInOrderOfOccurrence(Day day) {
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

                if (Time.isBeforeEvent(e1.getTimeStamp().getStart(), st1.getTimeStamp().getStart())) {
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
