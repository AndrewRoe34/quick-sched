package com.planner.util;

import com.planner.manager.ScheduleManager;
import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.schedule.day.Day;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Serializer {

    public static String serializeSchedule(List<Card> cards, List<Task> tasks, List<Event> indivEvents, List<Event> recurEvents, List<Day> days) {
        StringBuilder scheduleSb = new StringBuilder();

        if (cards != null) {
            scheduleSb.append(getCardsSb(cards)).append('\n');
        }

        if (tasks != null) {
            scheduleSb.append(getTasksSb(tasks)).append('\n');
        }

        if (indivEvents != null || recurEvents != null) {
            scheduleSb.append("EVENT {").append('\n');

            if (recurEvents == null) {
                scheduleSb.append(getEventsSb(indivEvents));
            } else if (indivEvents == null) {
                scheduleSb.append(getEventsSb(recurEvents));
            } else {
                indivEvents.addAll(recurEvents);
                scheduleSb.append(getEventsSb(indivEvents));
            }

            scheduleSb.append('\n');
        }

        if (days != null) {
            scheduleSb.append(getDaysSb(days));
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

    private static StringBuilder getTasksSb(List<Task> tasks) {
        StringBuilder tasksSb = new StringBuilder();

        tasksSb.append("TASK {").append('\n');

        for (Task task : tasks) {
            tasksSb.append('\t')
                    .append("\"").append(task.getName()).append("\"")
                    .append(" ")
                    .append(task.getTotalHours())
                    .append(" ");

            if (task.getCard() != null) {
                tasksSb.append("+C")
                        .append(task.getCard().getId())
                        .append(" ");
            }

            tasksSb.append("@")
                    .append(" ")
                    .append(task.getDateStamp())
                    .append('\n');
        }

        tasksSb.append("}").append('\n');

        return tasksSb;
    }

    private static StringBuilder getEventsSb(List<Event> events) {
        StringBuilder eventsSb = new StringBuilder();

        for (Event event : events) {
            eventsSb.append('\t')
                    .append("\"").append(event.getName()).append("\"")
                    .append(" ")
                    .append(event.isRecurring())
                    .append(" ");

            if (event.getCard() != null) {
                eventsSb.append("+C")
                        .append(event.getCard().getId())
                        .append(" ");
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

    private static StringBuilder getDaysSb(List<Day> days) {
        StringBuilder daysSb = new StringBuilder();

        daysSb.append("DAY {").append('\n');

        for (Day day : days) {
            daysSb.append('\t')
                    .append(day.getDateStamp())
                    .append(" ");

            for (Task.SubTask subTask : day.getSubTaskList()) {
                daysSb.append("T")
                        .append(subTask.getParentTask().getId())
                        .append(" ")
                        .append(subTask.get24HourTimeStampString())
                        .append(" ");
            }

            for (Event event : day.getEventList()) {
                daysSb.append("E")
                        .append(event.getId())
                        .append(" ");
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
        List<Day> days = new ArrayList<>();
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
                    processDays(lineScanner, events, tasks);
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
            Task t = sm.addTask(ti.getDesc(), ti.getHours(), ti.getDue(), cardCount + ti.getCardId());

            tasks.add(t);
        }
        return tasks;
    }

    /*
    when going through Day, we'll need to modify the number of hours each task has

ex.

10-09-2024 T0 8-9:30
...


since this is the prior day, we would do: task.setTotalHours(task.getTotalHours() - used);


since we are dealing with indexing Tasks and Events with scheduling, we should store a list of each


List<Task> tasks
List<Event> events


Double hours = task.getTotalHours() - used;
int taskId = tasks.get(id).getId();
sm.modTask(taskId, null, hours, null, null); <-- this handles archiving the task automatically for us
     */

    private static void processDays(Scanner lineScanner, List<Event> events, List<Task> tasks) {
        while (lineScanner.hasNextLine()) {
            String line = lineScanner.nextLine();

            Parser.DayInfo di = Parser.parseDay(Parser.tokenize(line));
            Calendar d = di.getDate();
            /*
            todo
              1. create day using di.getDate()
              2. add events manually to Day
              3. add subtasks to Day manually (make sure to follow steps from above about updating Task hours)
              4. return list of days
             */
        }
    }

    public static void main(String[] args) throws IOException {
        ScheduleManager sm = new ScheduleManager();

//        String sched = "CARD {\n" +
//                "  \"CSC\" light_blue\n" +
//                "  \"PY\" orange\n" +
//                "  \"FLJ\" yellow\n" +
//                "  \"PHI\" green\n" +
//                "}\n" +
//                "\n" +
//                "EVENT {\n" +
//                "  true \"OS Class\" +C0 @ tue thu 3pm-4:15\n" +
//                "  true \"PY Class\" +C1 @ mon wed fri 1:55pm-2:45pm\n" +
//                "  true \"PHI Class\" +C3 @ tue thu 8:30-9:45\n" +
//                "  true \"FLJ Recit\" +C2 @ 10:15-11:05 tue\n" +
//                "  true \"FLJ Class\" +C2 @ mon wed fri 10:40-11:30\n" +
//                "  true \"Lunch\" @ mon tue wed thu fri 11:45-1:30\n" +
//                "}\n" +
//                "\n" +
//                "TASK {\n" +
//                "  \"study ch9\" @ thu 6.5 +C2\n" +
//                "  \"do hw4\" @ fri 5 +C1\n" +
//                "  \"read ch2\" @ sat 4 +C3\n" +
//                "  \"project 2\" @ sun 15 +C0\n" +
//                "}\n";

        String sched = "CARD {\n" +
                "  \"Supply Chain\" RED\n" +
                "}\n" +
                "\n" +
                "TASK {\n" +
                "  \"finish ch12\" 4.0 +C0 @ 13-09-2024\n" +
                "}\n" +
                "\n" +
                "EVENT {\n" +
                "  true \"class1\" +C0 @ mon wed fri 01:00pm-2:15pm\n" +
                "}\n" +
                "\n" +
                "DAY {\n" +
                "  11-09-2024 T0 08:00am-12:00pm E0\n" +
                "}\n";

        deserializeSchedule(sched, sm);
        sm.buildSchedule();
        System.out.println(sm.buildScheduleStr());
    }
}
