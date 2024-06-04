package com.planner.util;

import com.planner.models.Card;
import com.planner.models.CheckList;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.manager.ScheduleManager;
import com.planner.schedule.day.Day;

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
    public static String createJBin(List<Card> cards) {

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
          <NAME>, <COLOR>, <DURATION_TIMESTAMPS>, <RECURRING_BOOLEAN>, <DAYS [Optional - Depends on 'recurring_boolean']>
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
        List<CheckList> checkListList = new ArrayList<>();
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
                CheckList cl = t.getCheckList();
                if(cl != null) {
                    checkListList.add(cl);
                    taskSB.append(", CL").append(checkListList.size() - 1);
                }
                taskSB.append("\n");
            }
            taskSB.append("}\n");
        }
        StringBuilder clSB = new StringBuilder();
        if(!checkListList.isEmpty()) {
            clSB.append("CHECKLIST {\n");
            for(CheckList cl : checkListList) {
                clSB.append("  ").append(cl.getName());
                for(CheckList.Item i : cl.getItems()) {
                    clSB.append(", ").append(i);
                }
                clSB.append("\n");
            }
            clSB.append("}\n\n");
        }
        StringBuilder daySB = new StringBuilder();
        ScheduleManager sm = ScheduleManager.getScheduleManager();
        if(!sm.scheduleIsEmpty()) {
            daySB.append("DAY {\n");
            for(Day d : sm.getSchedule()) {
                boolean flag = false;
                daySB.append("  ");
                if (d.getNumSubTasks() == 0) {
                    daySB.append("N/A");
                } else {
                    for(Task.SubTask st : d.getSubTasks()) {
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
        //now go from top to bottom with all the data you now have
        return calendarSB
                .append(clSB)
                .append(taskSB)
                .append("\n")
                .append(cardSB)
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
    public static void processJBin(String data, PriorityQueue<Task> tasks, List<Event> events, List<Card> cards,
                                   List<Day> schedule, int maxArchiveDays) {
        //NOTE: When processing, you should work from top to bottom (use ArrayLists to easily locate data by index value)
        Scanner jbinScanner = new Scanner(data);
        LocalDate ld = null;
        if(jbinScanner.hasNextLine()) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            ld = LocalDate.parse(jbinScanner.nextLine(), df);
        }

        Calendar calendar = Time.getFormattedCalendarInstance(0);
        assert ld != null;

        //todo codeblock needs to be refactored and tested further

        // Calendar.MONTH is zero-indexed
        calendar.set(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth());

        //todo codeblock needs to be refactored and tested further

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

        List<CheckList> checkLists = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();
        List<Event> eventList = new ArrayList<>();

        while(jbinScanner.hasNextLine()) {
            String type = jbinScanner.nextLine();
            String[] tokens = type.split("\\s");
            if(!checklistOpen && tokens.length == 2 && "CHECKLIST".equals(tokens[0]) && "{".equals(tokens[1])) {
                checklistOpen = true;
                while(jbinScanner.hasNextLine()) {
                    type = jbinScanner.nextLine();
                    tokens = type.split(",");
                    if(tokens.length == 0) {
                        throw new InputMismatchException();
                    } else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        checklistClosed = true;
                        break;
                    } else if(tokens.length == 1) {
                        checkLists.add(new CheckList(checkLists.size(), tokens[0].trim()));
                    } else {
                        checkLists.add(new CheckList(checkLists.size(), tokens[0].trim()));
                        int itemId = 0;
                        for(int i = 1; i < tokens.length; i++) {
                            String item = tokens[i];
                            boolean complete = false;
                            if(item.charAt(item.length() - 1) == '+') {
                                complete = true;
                                item = item.substring(0, item.length() - 1);
                            }
                            checkLists.get(checkLists.size() - 1).addItem(item.trim());
                            checkLists.get(checkLists.size() - 1).markItemById(itemId++, complete);
                        }
                    }
                }

                if(!checklistClosed) {
                    throw new IllegalArgumentException();
                }
            } else if(!taskOpen && tokens.length == 2 && "TASK".equals(tokens[0]) && "{".equals(tokens[1])) {
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
                    } else if(tokens.length > 3) {
                        taskList.add(new Task(taskList.size(), tokens[0].trim(), Double.parseDouble(tokens[1].trim()),
                                Time.getFormattedCalendarInstance(calendar, Integer.parseInt(tokens[2].trim()))));
                        for(int i = 3; i < tokens.length; i++) {
                            String item = tokens[i].trim();
                            if(item.length() > 2 && item.charAt(0) == 'C' && item.charAt(1) == 'L') {
                                taskList.get(taskList.size() - 1).addCheckList(checkLists.get(Integer.parseInt(item.substring(2))));
                            } else {
                                throw new InputMismatchException();
                            }
                        }
                    } else {
                        throw new InputMismatchException();
                    }
                }

                if(!taskClosed) {
                    throw new IllegalArgumentException();
                }
            }

            else if(!eventOpen && tokens.length == 2 && "EVENT".equals(tokens[0]) && "{".equals(tokens[1])) {
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
                        String duration = tokens[2].trim();
                        String startString = duration.split("-")[0].trim();
                        String endString = duration.split("-")[1].trim();

                        Calendar start = getEventCalendar(startString);
                        Calendar end = getEventCalendar(endString);

                        String eventName = tokens[0].trim();
                        String colorString = tokens[1].trim();

                        boolean recurring = Boolean.parseBoolean(tokens[3].trim());
                        String[] days = recurring ? tokens[4].trim().split(" ") : null;
                        eventList.add(
                            new Event(
                                eventList.size(),
                                eventName,
                                parseColor(colorString),
                                new Time.TimeStamp(start, end),
                                recurring,
                                days
                            )
                        );
                    }

                    else
                        throw new InputMismatchException();
                }

                if(!eventClosed)
                    throw new IllegalArgumentException();
            }

            else if(!cardOpen && tokens.length == 2 && "CARD".equals(tokens[0]) && "{".equals(tokens[1])) {
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
                    if(tokens.length == 0) {
                        throw new InputMismatchException();
                    } else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        dayClosed = true;
                        break;
                    } else if ("N/A".equals(tokens[0].trim()) && tokens.length == 1) {
                        Day day = new Day(dayCount++, 8, scheduleDay);
                        schedule.add(day);
                    } else {
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
                                day.addSubTask(taskList.get(taskIdx), hours, totalHours > 8); // todo need to use UserConfig here
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

        events.addAll(eventList);
    }

    private static Calendar getEventCalendar(String timeString) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(
                Integer.parseInt(timeString.split(":")[4]),
                Integer.parseInt(timeString.split(":")[3]) - 1,
                Integer.parseInt(timeString.split(":")[2]),
                Integer.parseInt(timeString.split(":")[0]),
                Integer.parseInt(timeString.split(":")[1])
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
