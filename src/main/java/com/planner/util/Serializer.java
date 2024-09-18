package com.planner.util;

import com.planner.manager.ScheduleManager;
import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;

import java.io.IOException;
import java.util.*;

public class Serializer {

    public static String serializeSchedule(List<Card> cards, List<Task> tasks, List<Event> indivEvents, List<Event> recurEvents, List<Day> days) {
        StringBuilder scheduleSb = new StringBuilder();

        if (cards != null) {
            scheduleSb.append(getCardsSb(cards)).append('\n');
        }

        if (tasks != null) {
            scheduleSb.append(getTasksSb(tasks, cards)).append('\n');
        }

        if (indivEvents != null || recurEvents != null) {
            scheduleSb.append("EVENT {").append('\n');

            if (recurEvents == null) {
                scheduleSb.append(getEventsSb(indivEvents, cards));
            } else if (indivEvents == null) {
                scheduleSb.append(getEventsSb(recurEvents, cards));
            } else {
                recurEvents.addAll(indivEvents);
                scheduleSb.append(getEventsSb(recurEvents));
            }

            scheduleSb.append('\n');
        }

        if (days != null) {
            if (indivEvents == null) {
                scheduleSb.append(getDaysSb(days, tasks, recurEvents));
            } else {
                scheduleSb.append(getDaysSb(days, tasks, indivEvents));
            }
        }
      
        return scheduleSb.toString();
    }

    private static StringBuilder getCardsSb(List<Card> cards) {
        StringBuilder cardsSb = new StringBuilder();

        cardsSb.append("CARD {").append('\n');

        for (Card card : cards) {
            cardsSb.append('\t')
                    .append("\"").append(card.getName()).append("\"")
                    .append(" ")
                    .append(card.getColor())
                    .append('\n');
        }

        cardsSb.append("}").append('\n');

        return cardsSb;
    }

    private static StringBuilder getTasksSb(List<Task> tasks, List<Card> cards) {
        StringBuilder tasksSb = new StringBuilder();

        tasksSb.append("TASK {").append('\n');

        for (Task task : tasks) {
            tasksSb.append('\t')
                    .append("\"").append(task.getName()).append("\"")
                    .append(" ")
                    .append(task.getTotalHours())
                    .append(" ");

            if (task.getCard() != null) {
                for (int i = 0; i < cards.size(); i++) {
                    if (cards.get(i).getId() == task.getCard().getId()) {
                        tasksSb.append("+C")
                                .append(i)
                                .append(" ");
                    }
                }
            }

            tasksSb.append("@")
                    .append(" ")
                    .append(task.getDateStamp())
                    .append('\n');
        }

        tasksSb.append("}").append('\n');

        return tasksSb;
    }

    private static StringBuilder getEventsSb(List<Event> events, List<Card> cards) {
        StringBuilder eventsSb = new StringBuilder();

        for (Event event : events) {
            eventsSb.append('\t')
                    .append(event.isRecurring())
                    .append(" ")
                    .append("\"").append(event.getName()).append("\"")
                    .append(" ");

            if (event.getCard() != null) {
                for (int i = 0; i < cards.size(); i++) {
                    if (cards.get(i).getId() == event.getCard().getId()) {
                        eventsSb.append("+C")
                                .append(i)
                                .append(" ");
                    }
                }
            }

            eventsSb.append("@")
                    .append(" ");

            if (event.isRecurring()) {
                eventsSb.append(event.getDaysString());
            } else {
                eventsSb.append(event.getDateStamp());
            }

            eventsSb.append(" ");

            eventsSb.append(event.getTimeStamp().toString())
                    .append('\n');
        }

        eventsSb.append("}").append('\n');

        return eventsSb;
    }

    private static StringBuilder getDaysSb(List<Day> days, List<Task> tasks, List<Event> events) {
        StringBuilder daysSb = new StringBuilder();

        daysSb.append("DAY {").append('\n');

        for (Day day : days) {
            daysSb.append('\t')
                    .append(day.getDateStamp())
                    .append(" ");

            for (Task.SubTask subTask : day.getSubTaskList()) {
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getId() == subTask.getParentTask().getId()) {
                        daysSb.append("T")
                                .append(i)
                                .append(" ")
                                .append(subTask.getTimeStamp().toString())
                                .append(" ");
                    }
                }
            }

            for (Event event : day.getEventList()) {
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).getId() == event.getId()) {
                        daysSb.append("E")
                                .append(i)
                                .append(" ");
                    }
                }
            }

            daysSb.append('\n');
        }

        daysSb.append("}");

        return daysSb;
    }
  
    public static void deserializeSchedule(String data, ScheduleManager sm) {
        // todo currently not handling exceptions (so, it just cancels out if we run into an error)
        Scanner lineScanner = new Scanner(data);
        int cardCount = sm.getCards().size();
        List<Event> events = null;
        List<Task> tasks = null;
        while (lineScanner.hasNextLine()) {
            switch (lineScanner.nextLine().trim()) {
                case "CARD {":
                    processCards(lineScanner, sm);
                    break;
                case "EVENT {":
                    events = processEvents(lineScanner, cardCount, sm);
                    break;
                case "TASK {":
                    tasks = processTasks(lineScanner, cardCount, sm);
                    break;
                case "DAY {":
                    List<Day> days = processDays(lineScanner, events, tasks, sm);
                    if (!days.isEmpty()) {
                        sm.setSched(days);
                    }
                    break;
            }
        }
    }

    private static void processCards(Scanner lineScanner, ScheduleManager sm) {
        while (lineScanner.hasNextLine()) {
            String line = lineScanner.nextLine();
            if ("}".equals(line.trim())) {
                break;
            }
            Parser.CardInfo ci = Parser.parseCard(Parser.tokenize("card " + line));
            sm.addCard(ci.getName(), ci.getColor());
        }
    }

    private static List<Event> processEvents(Scanner lineScanner, int cardCount, ScheduleManager sm) {
        List<Event> events = new ArrayList<>();
        while (lineScanner.hasNextLine()) {
            String line = lineScanner.nextLine();
            if ("}".equals(line.trim())) {
                break;
            }
            Parser.EventInfo ei = Parser.parseEvent(Parser.tokenize("event " + line));

            // todo all this here needs to be moved to sm.addEvent() --> START
            Calendar start = ei.getTimestamp()[0];
            Calendar end = ei.getTimestamp()[1];

            List<Calendar> dates = ei.getDates();

            if (!ei.isRecurring() && dates != null && dates.size() > 1) {
                throw new IllegalArgumentException("Event is non-recurring but has multiple days");
            }

            if (!ei.isRecurring() && dates != null) {
                start.set(Calendar.DAY_OF_MONTH, dates.get(0).get(Calendar.DAY_OF_MONTH));
                start.set(Calendar.MONTH, dates.get(0).get(Calendar.MONTH));
                start.set(Calendar.YEAR, dates.get(0).get(Calendar.YEAR));

                end.set(Calendar.DAY_OF_MONTH, dates.get(0).get(Calendar.DAY_OF_MONTH));
                end.set(Calendar.MONTH, dates.get(0).get(Calendar.MONTH));
                end.set(Calendar.YEAR, dates.get(0).get(Calendar.YEAR));
            }

            Time.TimeStamp timeStamp = new Time.TimeStamp(start, end);
            // todo <--- END


            Event e = sm.addEvent(ei.getName(), ei.getCardId() == null ? null : cardCount + ei.getCardId(),  timeStamp, ei.isRecurring(), dates);

            events.add(e);
        }
        return events;
    }

    private static List<Task> processTasks(Scanner lineScanner, int cardCount, ScheduleManager sm) {
        List<Task> tasks = new ArrayList<>();
        while (lineScanner.hasNextLine()) {
            String line = lineScanner.nextLine();
            if ("}".equals(line.trim())) {
                break;
            }
            Parser.TaskInfo ti = Parser.parseTask(Parser.tokenize("task " + line));

            Task t = sm.addTask(ti.getDesc(), ti.getHours(), ti.getDue(), ti.getCardId() == null ? null : cardCount + ti.getCardId());

            tasks.add(t);
        }
        return tasks;
    }

    private static List<Day> processDays(Scanner lineScanner, List<Event> events, List<Task> tasks, ScheduleManager sm) {
        List<Day> days = new ArrayList<>();
        Calendar today = Time.getFormattedCalendarInstance(0);
        UserConfig userConfig = sm.getUserConfig();
        int dayId = 0;
        while (lineScanner.hasNextLine()) {
            String line = lineScanner.nextLine();
            if ("}".equals(line.trim())) {
                break;
            }
            Parser.DayInfo di = Parser.parseDay(Parser.tokenize(line));
            Calendar d = di.getDate();
            int dayHrs = userConfig.getHoursPerDayOfWeek()[d.get(Calendar.DAY_OF_WEEK) - 1];
            Day day = new Day(dayId++, dayHrs, d);
            for (int id : di.getEventIds()) {
                day.forceAddEvent(events.get(id));
            }

            for (int id : di.getTaskTimeStampsMap().keySet()) {
                Task t = tasks.get(id);
                List<Time.TimeStamp> timestamps = di.getTaskTimeStampsMap().get(id);
                for (Time.TimeStamp ts : timestamps) {
                    double hours = Time.getTimeInterval(ts.getStart(), ts.getEnd());
                    day.forceAddTask(t, hours, ts);
                    if (!Time.doDatesMatch(today, d) && d.compareTo(today) < 0) {
                        // update Task here since the day is older than today
                        double updatedHours = t.getTotalHours() - hours;
                        sm.modTask(id, null, updatedHours, null, null);
                    }
                }
            }
            day.sortSubTasks();
            days.add(day);
        }
        return days;
    }
}
