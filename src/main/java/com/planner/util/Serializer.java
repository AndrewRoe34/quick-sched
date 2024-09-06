package com.planner.util;

import com.planner.manager.ScheduleManager;
import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.schedule.day.Day;

import java.util.List;

public class Serializer {

    public static String serializeSchedule(List<Card> cards, List<Task> tasks, List<Event> events, List<Day> days) {
        return null;
    }

    public static void deserializeSchedule(String data, ScheduleManager scheduleManager) {
        // 1. append the relevant identifier to each line (followed by a space)
        // 2. tokenize the line
        // 3. parse the relevant type
        // 4. call scheduleManager's method to create that type
        // 5. return info object that relates:
        //    - how many cards, tasks, events, days read in
        //    - how many errors you encountered (remember, we skip past these)
    }
}
