package com.planner.util;

import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.schedule.day.Day;
import com.planner.scripter.exception.InvalidGrammarException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Java Binary Serialization is modeled after JSON for data storage/retrieval.
 * This will provide greater efficiency, structure, security, as well as ease of passage
 * of data with the Front-End component
 *
 * @author Andrew Roe
 * @author Abah Olotuche Gabriel
 */
public class JBin {

    /**
     * Creates a Java Binary Serialization string to be later passed or stored
     *
     * @param cards all Cards in System
     * @return JBin String
     */
    public static String createJBin(
            List<Card> cards,
            List<Day> schedule,
            List<Event> indivEvents,
            List<List<Event>> recurringEvents
    ) {

        /* NOTES:
        1. Create Label section (with ID, not associated with system)
        2. Create Task section (with ID, not associated with system)
            --> Could hold a Label, which will be identified as "L#"
        3. Create Card section
            --> Could hold a Label, which will be identified as "L#"
            --> Could hold a Task, which will be identified as "T#"

        HashMaps will be very helpful for decoding JBin later on
        Need to account for possibility that file is read two times in a row
            --> This would mean tasks, cards, and labels are the same/duplicates (will need to verify via the equals' method, i.e. HashSet)
            --> Otherwise, it simply gets added to the system

        FORMATTING OF DATA:
        CHECKLIST {
          <ITEM0>, <ITEM1>, ...
          ...
        }
        TASK {
          <NAME>, <DUE_DATE>, <TOTAL_HR>, <USED_HR>, CL#
          ...
        }
        EVENT {
          <NAME>, <COLOR>, <RECURRING_BOOLEAN>, <DURATION_TIMESTAMPS>, <DAYS/DATE - Depends on 'recurring_boolean'>
          ...
        }
        CARD {
          <Title>, <COLOR>, T#
          ...
        }
        DAY {
          T0 4, T3 2, T1 4
          ...
        }

        NOTE: Try working from bottom to top (might be able to save on efficiency and storage)
         */
        Calendar calendar = Time.getFormattedCalendarInstance(0);
        StringBuilder calendarSB = new StringBuilder();
        if(calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            calendarSB.append("0").append(calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            calendarSB.append(calendar.get(Calendar.DAY_OF_MONTH));
        }
        calendarSB.append("-");
        if(calendar.get(Calendar.MONTH) < 10) {
            calendarSB.append("0").append(calendar.get(Calendar.MONTH) + 1);
        } else {
            calendarSB.append(calendar.get(Calendar.MONTH));
        }
        calendarSB.append("-").append(calendar.get(Calendar.YEAR)).append("\n\n");

        StringBuilder cardSB = new StringBuilder();
        List<Task> taskList = new ArrayList<>();

        if(!cards.isEmpty()) {
            cardSB.append("CARD {\n");
            for(Card c : cards) {
                String title = c.getTitle();
                cardSB.append("  ").append(title);
                cardSB.append(", ").append(c.getColorId());
                for(Task t : c.getTask()) {
                    if(!taskList.contains(t)) {
                        taskList.add(t);
                        cardSB.append(", T").append(taskList.size() - 1);
                    } else {
                        cardSB.append(", T").append(taskList.indexOf(t));
                    }
                }
                cardSB.append("\n");
            }
            cardSB.append("}\n");
        }

        StringBuilder taskSB = new StringBuilder();
        if(!taskList.isEmpty()) {
            taskSB.append("TASK {\n");
            for(Task t : taskList) {
                String name = t.getName();
                double totalHours = t.getTotalHours();
                //int remainingHours = t.getSubTotalHoursRemaining();
                taskSB.append("  ")
                        .append(name)
                        .append(", ")
                        .append(totalHours)
                        .append(", ")
                        .append(Time.differenceOfDays(t.getDueDate(), calendar));
                taskSB.append("\n");
            }
            taskSB.append("}\n");
        }

        StringBuilder daySB = new StringBuilder();
        if(!schedule.isEmpty()) {
            daySB.append("DAY {\n");
            for(Day d : schedule) {
                boolean flag = false;
                daySB.append("  ");
                if (d.getNumSubTasks() == 0) {
                    daySB.append("N/A");
                } else {
                    for(Task.SubTask st : d.getSubTaskList()) {
                        if(flag) {
                            daySB.append(", ");
                        }
                        Task task = st.getParentTask();
                        int i = -1;
                        for(Task t : taskList) {
                            i++;
                            if(task == t) {
                                break;
                            }
                        }
                        daySB.append("T")
                                .append(i)
                                .append(" ")
                                .append(st.getSubTaskHours());
                        flag = true;
                    }
                }
                daySB.append("\n");
            }
            daySB.append("}\n");
        }

        StringBuilder eventSB = new StringBuilder();
        boolean eventsExist = false;

        if (!indivEvents.isEmpty())
            eventsExist = true;

        for (List<Event> event : recurringEvents) {
            if (!event.isEmpty()) {
                eventsExist = true;
                break;
            }
        }

        if (eventsExist) {
            eventSB.append("EVENT {\n");

            for (Event e : indivEvents) {
                eventSB.append("  ")
                        .append(e.getName())
                        .append(", ")
                        .append(e.getColor().toString())
                        .append(", ")
                        .append("false")
                        .append(", ")
                        .append(e.get24HourTimeStampString())
                        .append(", ")
                        .append(e.getDateStamp())
                        .append("\n");
            }

            HashSet<Event> eventsSet = new HashSet<>();

            for (List<Event> events : recurringEvents) {
                for (Event e : events) {
                    if (eventsSet.contains(e))
                        continue;

                    eventSB.append("  ")
                            .append(e.getName())
                            .append(", ")
                            .append(e.getColor().toString())
                            .append(", ")
                            .append("true")
                            .append(", ")
                            .append(e.get24HourTimeStampString())
                            .append(", ")
                            .append(e.getDaysString())
                            .append("\n");

                    eventsSet.add(e);
                }
            }

            eventSB.append("}\n");
        }

        //now go from top to bottom with all the data you now have
        return calendarSB
                .append(taskSB)
                .append("\n")
                .append(cardSB)
                .append("\n")
                .append(eventSB)
                .append("\n")
                .append(daySB)
                .toString();
    }

    /**
     * Processes the Java Binary Serialization string to update the current system
     *
     * @param data JBin string being processed
     * @param tasks Tasks holder
     * @param cards Cards holder
     * @param schedule set of Days for the given schedule being processed
     * @param maxArchiveDays maximum number of past Days to include
     */
    public static void processJBin(
            String data,
            PriorityQueue<Task> tasks,
            List<Event> events,
            int eventId,
            List<Card> cards,
            List<Day> schedule,
            int maxArchiveDays
    ) {
        //NOTE: When processing, you should work from top to bottom (use ArrayLists to easily locate data by index value)
        Scanner jbinScanner = new Scanner(data);
        LocalDate ld = null;
        if(jbinScanner.hasNextLine()) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            ld = LocalDate.parse(jbinScanner.nextLine(), df);
        }

        Calendar calendar = Time.getFormattedCalendarInstance(0);
        assert ld != null;

        // Calendar.MONTH is zero-indexed
        calendar.set(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth());

        boolean checklistOpen = false;
        boolean checklistClosed = false;
        boolean taskOpen = false;
        boolean taskClosed = false;
        boolean eventOpen = false;
        boolean eventClosed = false;
        boolean cardOpen = false;
        boolean cardClosed = false;
        boolean dayOpen = false;
        boolean dayClosed = false;

        List<Task> taskList = new ArrayList<>();

        while(jbinScanner.hasNextLine()) {
            String type = jbinScanner.nextLine();
            String[] tokens = type.split("\\s");
            if(!taskOpen && tokens.length == 2 && "TASK".equals(tokens[0]) && "{".equals(tokens[1])) {
                taskOpen = true;
                while(jbinScanner.hasNextLine()) {
                    type = jbinScanner.nextLine();
                    tokens = type.split(",");
                    if(tokens.length == 0) {
                        throw new InputMismatchException();
                    } else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        taskClosed = true;
                        break;
                    } else if(tokens.length == 3) {
                        taskList.add(new Task(taskList.size(), tokens[0].trim(), Double.parseDouble(tokens[1].trim()),
                                Time.getFormattedCalendarInstance(calendar, Integer.parseInt(tokens[2].trim()))));
                    } else {
                        throw new InputMismatchException();
                    }
                }

                if(!taskClosed) {
                    throw new IllegalArgumentException();
                }
            } else if(!eventOpen && tokens.length == 2 && "EVENT".equals(tokens[0]) && "{".equals(tokens[1])) {
                eventOpen = true;

                while(jbinScanner.hasNextLine()) {
                    tokens = jbinScanner.nextLine().split(",");

                    if(tokens.length == 0)
                        throw new InputMismatchException();

                    else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        eventClosed = true;
                        break;
                    }

                    else if(tokens.length == 5 || tokens.length == 4) {
                        String name = tokens[0].trim();
                        Card.Colors color = parseColor(tokens[1].trim().toUpperCase());
                        boolean recurring = Boolean.parseBoolean(tokens[2].trim());

                        String startString = tokens[3].split("-")[0];
                        String endString = tokens[3].split("-")[1];

                        Calendar start = recurring ? getEventCalendar(null, startString) : getEventCalendar(tokens[4], startString);
                        Calendar end = recurring ? getEventCalendar(null, endString) : getEventCalendar(tokens[4], endString);
                        String[] days = recurring ? tokens[4].trim().split(" ") : null;

                        Event.DayOfWeek[] week = null;
                        if (days != null) {
                            week = new Event.DayOfWeek[days.length];
                            int count = 0;
                            for (String s : days) {
                                // since this is a jbin file, all the days should be fully and properly spelled
                                switch (s.toUpperCase()) {
                                    case "SUN":
                                        week[count++] = Event.DayOfWeek.SUN;
                                        break;
                                    case "MON":
                                        week[count++] = Event.DayOfWeek.MON;
                                        break;
                                    case "TUE":
                                        week[count++] = Event.DayOfWeek.TUE;
                                        break;
                                    case "WED":
                                        week[count++] = Event.DayOfWeek.WED;
                                        break;
                                    case "THU":
                                        week[count++] = Event.DayOfWeek.THU;
                                        break;
                                    case "FRI":
                                        week[count++] = Event.DayOfWeek.FRI;
                                        break;
                                    case "SAT":
                                        week[count++] = Event.DayOfWeek.SAT;
                                        break;
                                    default:
                                        throw new InvalidGrammarException("Invalid recurrent day was passed to Event");
                                }
                            }
                        }

                        if (recurring) {
                            events.add(
                                    new Event(
                                            eventId++,
                                            name,
                                            color,
                                            new Time.TimeStamp(start, end),
                                            week
                                    )
                            );
                        } else {
                            events.add(
                                    new Event(
                                            eventId++,
                                            name,
                                            color,
                                            new Time.TimeStamp(start, end)
                                    )
                            );
                        }
                    }

                    else
                        throw new InputMismatchException();
                }

                if(!eventClosed)
                    throw new IllegalArgumentException();
            } else if(!cardOpen && tokens.length == 2 && "CARD".equals(tokens[0]) && "{".equals(tokens[1])) {
                cardOpen = true;
                Calendar currDay = Time.getFormattedCalendarInstance(0);
                boolean firstCard = true;
                while(jbinScanner.hasNextLine()) {
                    type = jbinScanner.nextLine();
                    tokens = type.split(",");
                    if(tokens.length == 0) {
                        throw new InputMismatchException();
                    } else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        cardClosed = true;
                        break;
                    } else if(tokens.length == 2) { // need to make this '== 2', change else to 'else if tokens.length > 2', and finally else with exception thrown
                        if (firstCard && tokens[0].trim().equals(cards.get(0).getTitle())) {
                            // do nothing here
                        } else {
                            cards.add(new Card(cards.size(), tokens[0].trim(), parseColor(tokens[1].trim())));
                        }
                        firstCard = false;
                    } else if (tokens.length > 2) {
                        Card card = null;
                        if (firstCard && tokens[0].trim().equals(cards.get(0).getTitle())) {
                            // do nothing here
                            card = cards.get(0);
                        } else {
                            cards.add(new Card(cards.size(), tokens[0].trim(), parseColor(tokens[1].trim())));
                            card = cards.get(cards.size() - 1);
                        }
                        firstCard = false;
                        for(int i = 2; i < tokens.length; i++) {
                            String item = tokens[i].trim();
                            int idx = Integer.parseInt(item.substring(1));
                            if(item.length() > 1 && item.charAt(0) == 'T') {
                                Task tempTask = taskList.get(idx);
                                if(tempTask != null) {
                                    int numDays = Time.differenceOfDays(tempTask.getDueDate(), currDay);
                                    if (numDays >= -1 * maxArchiveDays) {
                                        card.addTask(tempTask);
                                        if (tempTask.getTag() == null) {
                                            tempTask.setTag(card.getTitle());
                                        }
                                        if (tempTask.getColor() == null) {
                                            tempTask.setColor(card.getColorId());
                                        }
                                    }
                                    else taskList.set(idx, null);
                                }
                            } else {
                                throw new InputMismatchException();
                            }
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
                if(!cardClosed) {
                    throw new IllegalArgumentException();
                }
            } else if (!dayOpen && tokens.length == 2 && "DAY".equals(tokens[0]) && "{".equals(tokens[1])) {
                boolean[] table = new boolean[taskList.size()];
                dayOpen = true;
                Calendar currDay = Time.getFormattedCalendarInstance(0);
                int dayIdx = 0;
                int dayCount = 0;
                while (jbinScanner.hasNextLine()) {
                    Calendar scheduleDay = Time.getFormattedCalendarInstance(calendar, dayIdx++);

                    type = jbinScanner.nextLine();
                    tokens = type.split(",");

                    if(tokens.length == 0)
                        throw new InputMismatchException();

                    else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        dayClosed = true;
                        break;
                    }
                    else if ("N/A".equals(tokens[0].trim()) && tokens.length == 1) {
                        if (Time.differenceOfDays(scheduleDay, currDay) < 0) continue;
                        Day day = new Day(dayCount++, 8, scheduleDay);
                        schedule.add(day);
                    }
                    else {
                        if (Time.differenceOfDays(scheduleDay, currDay) < 0) continue;
                        Day day = new Day(dayCount++, 8, scheduleDay);
                        double totalHours = 0.0;
                        for (String taskStr : tokens) {
                            String[] pair = taskStr.trim().split("\\s");
                            if (pair.length == 2 && pair[0].charAt(0) == 'T') {
                                int taskIdx = Integer.parseInt(pair[0].substring(1));
                                double hours = Double.parseDouble(pair[1]);
                                totalHours += hours;
                                if (!table[taskIdx]) {
                                    taskList.get(taskIdx).setTotalHours(hours);
                                    table[taskIdx] = true;
                                } else {
                                    taskList.get(taskIdx).setTotalHours(hours + taskList.get(taskIdx).getTotalHours());
                                }
                                day.addFormattedSubTask(taskList.get(taskIdx), hours, totalHours > 8); // todo need to use UserConfig here
                            } else throw new InputMismatchException();
                        }
                        schedule.add(day);
                    }
                }
                if (!dayClosed) throw new IllegalArgumentException();
            } else if(!type.isEmpty()) {
                throw new InputMismatchException();
            }
        }

        for(Task t : taskList) {
            if(t != null) {
                tasks.add(t);
            }
        }
    }

    private static Calendar getEventCalendar(String dateString, String timeString) {
        Calendar calendar = Calendar.getInstance();

        // If dateString is null, then this event is recurring. So, use random numbers
        // we don't care about for the day, month and year.
        int day = dateString == null ? 1 : Integer.parseInt(dateString.split("-")[0].trim());
        int year = dateString == null ? 1 : Integer.parseInt(dateString.split("-")[2].trim());

        // Calendar.MONTH is zero-indexed. This change allows individual events to be added to their corresponding days on the schedule.
        int month = dateString == null ? 1 : Integer.parseInt(dateString.split("-")[1].trim()) - 1;

        calendar.set(
                year,
                month,
                day,
                Integer.parseInt(timeString.split(":")[0].trim()),
                Integer.parseInt(timeString.split(":")[1].trim())
        );

        return calendar;
    }

    private static Card.Colors parseColor(String s) {
        switch (s) {
            case "RED":
                return Card.Colors.RED;
            case "ORANGE":
                return Card.Colors.ORANGE;
            case "YELLOW":
                return Card.Colors.YELLOW;
            case "GREEN":
                return Card.Colors.GREEN;
            case "BLUE":
                return Card.Colors.BLUE;
            case "INDIGO":
                return Card.Colors.INDIGO;
            case "VIOLET":
                return Card.Colors.VIOLET;
            case "BLACK":
                return Card.Colors.BLACK;
            case "LIGHT_CORAL":
                return Card.Colors.LIGHT_CORAL;
            case "LIGHT_GREEN":
                return Card.Colors.LIGHT_GREEN;
            default:
                return Card.Colors.LIGHT_BLUE;
        }
    }
}
