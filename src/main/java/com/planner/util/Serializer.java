package com.planner.util;

import com.planner.manager.ScheduleManager;
import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.schedule.day.Day;

import java.util.List;
import java.util.Scanner;

public class Serializer {

    public static String serializeSchedule(List<Card> cards, List<Task> tasks, List<Event> events, List<Day> days) {
        StringBuilder scheduleSb = new StringBuilder();

        if (cards != null) {
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

            scheduleSb.append(cardsSb).append('\n');
        }

        if (tasks != null) {
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

            scheduleSb.append(tasksSb).append('\n');
        }

        if (events != null) {
            StringBuilder eventsSb = new StringBuilder();

            eventsSb.append("EVENT {").append('\n');

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
                        .append(" ")
                        .append(event.getDateStamp())
                        .append(" ")
                        .append(event.getDateStamp())
                        .append('\n');
            }

            eventsSb.append("}").append('\n');

            scheduleSb.append(eventsSb).append('\n');
        }

        if (days != null) {
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

            daysSb.append("}").append('\n');

            scheduleSb.append(daysSb).append('\n');
        }

        return scheduleSb.toString();
    }



    /*
card "PY" orange
card "FLJ" yellow
event true "OS Class" +C0 @ tue thu 3pm-4:15
task "study ch9" @ thu 6.5 +C1

in order to fix indexing, we can quickly update the relevant index

ex.

if ScheduleManager has 4 cards and we're reading in a new one, we would automatically set this card index to '4'

so, if we have a task referencing card '0', we would take that and add it to the number of cards from ScheduleManager
	- ti.setCardId(id + sm.getNumCards());


we need to do this process with the Event as well


Day is going to be annoying
     */
    public static void deserializeSchedule(String filename, String data) {
        // 1. append the relevant identifier to each line (followed by a space)
        // 2. tokenize the line
        // 3. parse the relevant type
        // 4. call scheduleManager's method to create that type
        // 5. return info object that relates:
        //    - how many cards, tasks, events, days read in
        //    - how many errors you encountered (remember, we skip past these)
        Scanner lineScanner = new Scanner("sched/" + filename);
        while (lineScanner.hasNextLine()) {
            switch (lineScanner.nextLine().trim()) {
                case "CARD {":

                case "EVENT {":
                case "TASK {":
                case "DAY {":
                default:
                    // error
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
}
