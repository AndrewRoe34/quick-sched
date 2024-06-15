package com.planner.ui.tables;

import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;
import com.planner.util.Time;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handles the creation of both dotted and pretty tables for scheduling related data
 *
 * @author Andrew Roe
 */
public class TableFormatter {

    /**
     * Creates a {@link UserConfig} table utilizing the pretty format
     *
     * @param userConfig provided user options
     * @return pretty config table
     */
    public static String formatPrettyUserConfigTable(UserConfig userConfig) {
        String[] optionNames = {"RANGE", "WEEK", "MAX_DAYS", "ARCHIVE_DAYS", "PRIORITY", "OVERFLOW", "FIT_DAY",
                "SCHED_ALGORITHM", "MIN_HOURS", "OPTIMIZE_DAY", "DEFAULT_AT_START", "LOCAL_SCHED_COLORS"};

        StringBuilder sb = new StringBuilder();
        sb.append("                                                            Settings Options\n");
        sb.append("                                         _______________________________________________________\n");
        sb.append("                                         |ID    |OPTION             | DATA                     |\n");
        sb.append("                                         |______|___________________|__________________________|\n");

        for (int i = 0; i < optionNames.length; i++) {
            String optionValue = "";
            switch (i) {
                case 0: optionValue = Arrays.toString(userConfig.getRange()); break;
                case 1: optionValue = Arrays.toString(userConfig.getWeek()); break;
                case 2: optionValue = String.valueOf(userConfig.getMaxDays()); break;
                case 3: optionValue = String.valueOf(userConfig.getArchiveDays()); break;
                case 4: optionValue = String.valueOf(userConfig.isPriority()); break;
                case 5: optionValue = String.valueOf(userConfig.isOverflow()); break;
                case 6: optionValue = String.valueOf(userConfig.isFitDay()); break;
                case 7: optionValue = String.valueOf(userConfig.getSchedulingAlgorithm()); break;
                case 8: optionValue = String.valueOf(userConfig.getMinHours()); break;
                case 9: optionValue = String.valueOf(userConfig.isOptimizeDay()); break;
                case 10: optionValue = String.valueOf(userConfig.isDefaultAtStart()); break;
                case 11: optionValue = String.valueOf(userConfig.isLocalScheduleColors()); break;
            }
            String formattedOptionValue = String.format(" %-25s|", optionValue);
            sb.append(String.format("                                         |%-6d|%-19s|", i, optionNames[i]));
            if (!optionValue.isEmpty()) {
                sb.append(formattedOptionValue);
            } else {
                sb.append("                             |\n"); // If optionValue is empty, fill with spaces
            }
            sb.append("\n");
        }
        sb.append("                                         |______|___________________|__________________________|\n");

        return sb.toString();
    }

    /**
     * Creates a script options table utilizing the pretty format
     *
     * @param scriptList list of available script options
     * @return pretty script options table
     */
    public static String formatPrettyScriptOptionsTable(List<File> scriptList) {
        StringBuilder sb = new StringBuilder();
        sb.append("                                                      Script Options\n");
        sb.append("                                         ____________________________________________\n");
        sb.append("                                         |ID    |Name                 | DATE        |\n");
        sb.append("                                         |______|_____________________|_____________|\n");
        int id = 0;
        for (File file : scriptList) {
            Date date = new Date(file.lastModified());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = simpleDateFormat.format(date);
            sb.append("                                         ");
            String formattedOptionValue = String.format("|%-6d|%-19s  |%-13s|\n", id++, file.getName(), strDate);
            sb.append(formattedOptionValue);
        }
        sb.append("                                         |______|_____________________|_____________|");
        return sb.toString();
    }

    /**
     * Creates a schedule table consisting of {@link Day}, providing options for both dotted and pretty formats
     *
     * @param schedule list of scheduled days
     * @param isPretty whether to display table with pretty format
     * @return dotted schedule table
     */
    public static String formatScheduleTable(List<Day> schedule, UserConfig userConfig, boolean isPretty) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar date = Time.getFormattedCalendarInstance(0);

        if (isPretty) {
            sb.append("             SCHEDULE:\n");
            sb.append("            ");
            sb.append("_________________________________________".repeat(schedule.size()));
            sb.append("_\n");
        } else {
            sb.append("SCHEDULE:\n");
        }

        int maxItems = 0;
        if (isPretty) {
            sb.append("            |");
        }
        int[][] task_eventPair = new int[schedule.size()][2];
        for (int i = 0; i < Math.min(schedule.size(), 6); i++) {
            sb.append(sdf.format(date.getTime()));
            sb.append("                              |");
            maxItems = Math.max(maxItems, schedule.get(i).getNumSubTasks() + schedule.get(i).getEventList().size());
            task_eventPair[i][0] = 0;
            task_eventPair[i][1] = 0;
            date = Time.getFormattedCalendarInstance(date, 1);
        }

        sb.append("\n");
        if (isPretty) {
            sb.append("            |");
            sb.append("________________________________________|".repeat(schedule.size()));
        } else {
            sb.append("-----------------------------------------".repeat(schedule.size()));
        }



        for (int i = 0; i < maxItems; i++) {
            sb.append("\n");
            if (isPretty) {
                sb.append("            |");
            }
            for (int d = 0; d < Math.min(schedule.size(), 6); d++) {
                Day day = schedule.get(d);

                if (i < (day.getNumSubTasks() + day.getNumEvents())) {
                    Time.TimeStamp ts = null;
                    String name = null;
                    Card.Colors color = null;
                    if (task_eventPair[d][0] >= day.getNumSubTasks()) {
                        Event e1 = day.getEvent(task_eventPair[d][1]);
                        ts = e1.getTimeStamp();
                        name = e1.getName();
                        color = e1.getColor();
                        task_eventPair[d][1]++;
                    } else if (task_eventPair[d][1] >= day.getNumEvents()) {
                        Task.SubTask st1 = day.getSubTask(task_eventPair[d][0]);
                        ts = st1.getTimeStamp();
                        name = st1.getParentTask().getName();
                        color = st1.getParentTask().getColor();
                        task_eventPair[d][0]++;
                    } else {
                        Event e1 = day.getEvent(task_eventPair[d][1]);
                        Task.SubTask st1 = day.getSubTask(task_eventPair[d][0]);
                        if (Time.isBeforeEvent(e1.getTimeStamp().getStart(), st1.getTimeStamp().getStart())) {
                            ts = e1.getTimeStamp();
                            name = e1.getName();
                            color = e1.getColor();
                            task_eventPair[d][1]++;
                        } else {
                            ts = st1.getTimeStamp();
                            name = st1.getParentTask().getName();
                            color = st1.getParentTask().getColor();
                            task_eventPair[d][0]++;
                        }
                    }

                    if (userConfig.isLocalScheduleColors()) {
                        String colorANSICode = getColorANSICode((color));
                        sb.append(colorANSICode);
                    }

                    sb.append(ts).append(" - "); // 18 char

                    if (name.length() > 21) {
                        sb.append(name, 0, 21);
                    } else {
                        sb.append(name);
                        sb.append(" ".repeat(22 - name.length()));
                    }

                    if (userConfig.isLocalScheduleColors())
                        sb.append("\u001B[0m");

                    sb.append("|");

                } else {
                    sb.append("                                        |");
                }
            }
        }
        sb.append("\n");

        if (isPretty) {
            sb.append("            |");
            sb.append("________________________________________|".repeat(Math.min(schedule.size(), 6)));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Creates a board table consisting of {@link Card} and {@link Task}, providing options for both dotted and pretty formats
     *
     * @param cards list of scheduled cards
     * @param userConfig provided user options
     * @param archivedTasks previously archived tasks
     * @param isPretty whether to display table with pretty format
     * @return dotted board table
     */
    public static String formatBoardTable(List<Card> cards, UserConfig userConfig, PriorityQueue<Task> archivedTasks, boolean isPretty) {
        StringBuilder sb = new StringBuilder();

        if (isPretty) {
            sb.append("             CARDS:\n");
            sb.append("            ");
            boolean firstCard = true;
            for (Card c1 : cards) {
                if (firstCard && c1.getTask().isEmpty()) {
                    // we do nothing, happens only once at most
                } else {
                    sb.append("_________________________________________");
                }
                firstCard = false;
            }
            sb.append("\n");
        } else {
            sb.append("CARDS:\n");
        }

        // use foreach loop to determine max number of tasks while printing out the first line of Cards
        int maxTasks = 0;
        boolean defaultCardIsEmpty = false;
        if (isPretty) {
            sb.append("            |");
        }
        int cardIdx = 0;
        for (Card c1 : cards) {
            if (cardIdx == 0 && c1.getTask().isEmpty()) {
                cardIdx++;
                defaultCardIsEmpty = true;
                continue;
            }
            if (userConfig.isLocalScheduleColors()) {
                String colorANSICode = getColorANSICode((c1.getColorId()));
                sb.append(colorANSICode);
            }

            maxTasks = Math.max(c1.getTask().size(), maxTasks);
            if (c1.toString().length() > 40)
                sb.append(c1.toString(), 0, 40);
            else {
                sb.append(c1);

                sb.append(" ".repeat(Math.max(0, 40 - c1.toString().length())));
            }

            if (userConfig.isLocalScheduleColors())
                sb.append("\u001B[0m");

            sb.append("|");
        }

        sb.append("\n");
        int cardCount = defaultCardIsEmpty ? 1 : 0;
        if (isPretty) {
            sb.append("            |");
            for (; cardCount < cards.size(); cardCount++) {
                sb.append("________________________________________|");
            }
        } else {
            for (; cardCount < cards.size(); cardCount++) {
                sb.append("-----------------------------------------");
            }
        }


        // use foreach loop inside a for loop to output the tasks
        for (int i = 0; i < maxTasks; i++) {
            sb.append("\n");
            if (isPretty) {
                sb.append("            |");
            }
            cardIdx = 0;
            for(Card c1 : cards) {
                if (cardIdx++ == 0 && c1.getTask().isEmpty()) continue;

                if (i < c1.getTask().size()) {
                    // print out the task (up to 18 characters)
                    Task t1 = c1.getTask().get(i);
                    String outputTask = "";
//                    if (Time.differenceOfDays(t1.getDueDate(), currDate) < 0) {
//                        outputTask = "*";
//                    }

                    if (archivedTasks.contains(t1))
                        outputTask = "*";

                    outputTask += t1.toString();

                    if (outputTask.length() > 40)
                        sb.append(outputTask, 0, 40);
                    else {
                        sb.append(outputTask);
                        sb.append(" ".repeat(40 - outputTask.length()));
                    }

                    sb.append("|");
                }
                else
                    sb.append("                                        |");
            }
        }
        sb.append("\n");

        if (isPretty) {
            sb.append("            |");
            cardCount = defaultCardIsEmpty ? 1 : 0;
            for (; cardCount < cards.size(); cardCount++) {
                sb.append("________________________________________|");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Creates an event table consisting of both recurring and individual {@link Event}, providing options for both dotted and pretty formats
     *
     * @param recurringEvents list of recurring events
     * @param indivEvents list of individual events
     * @param isPretty whether to display table with pretty format
     * @return event table
     */
    public static String formatEventSetTables(List<List<Event>> recurringEvents, List<Event> indivEvents, boolean isPretty) {
        StringBuilder sb = new StringBuilder();

        boolean recurringEventsExist = false;
        for (List<Event> events : recurringEvents) {
            if (!events.isEmpty()) {
                recurringEventsExist = true;
                break;
            }
        }

        if (recurringEventsExist) {
            if (isPretty) {
                sb.append("             Recurring Events:\n");
                sb.append("            _____________________________________________________________________________________________________\n");
                sb.append("            |");
            } else {
                sb.append("RECURRING EVENTS:\n");
            }
            sb.append("ID").append(" ".repeat(5)).append("|");
            sb.append("NAME").append(" ".repeat(16)).append("|");
            sb.append("COLOR").append(" ".repeat(10)).append("|");
            sb.append("TIME").append(" ".repeat(16)).append("|");
            sb.append("DAYS").append(" ".repeat(29)).append("|\n");
            if (isPretty) {
                sb.append("            |_______|____________________|_______________|____________________|_________________________________|\n");
            } else {
                sb.append("----------------------------------------------------------------------------------------------------");
                sb.append("\n");
            }
        }

        HashSet<Event> uniqueRecurringEvents = new HashSet<>();
        for (List<Event> events : recurringEvents) {
            uniqueRecurringEvents.addAll(events);
        }

        for (Event e : uniqueRecurringEvents) {
            if (isPretty) {
                sb.append("            |");
            }

            sb.append(e.getId()).append(" ".repeat(7 - String.valueOf(e.getId()).length())).append("|");

            if (e.getName().length() > 20)
                sb.append(e.getName(), 0, 20).append("|");
            else
                sb.append(e.getName()).append(" ".repeat(20 - e.getName().length())).append("|");

            sb.append(e.getColor()).append(" ".repeat(15 - String.valueOf(e.getColor()).length())).append("|");

            sb.append(e.getTimeStamp().toString()).append(" ".repeat(
                    20 - e.getTimeStamp().toString().length())
            ).append("|");

            sb.append(Arrays.toString(
                            e.getDays()),
                    1,
                    Arrays.toString(e.getDays()).length() - 1
            ).append(" ".repeat(33 - Arrays.toString(e.getDays()).length() + 2)).append("|");

            sb.append("\n");
        }

        if (recurringEventsExist) {
            if (isPretty) {
                sb.append("            |_______|____________________|_______________|____________________|_________________________________|\n\n");
            } else {
                sb.append("\n");
            }
        }

        if (!indivEvents.isEmpty()) {
            if (isPretty) {
                sb.append("             Individual Events:\n");
                sb.append("            ________________________________________________________________________________\n");
                sb.append("            |");
            } else {
                sb.append("INDIVIDUAL EVENTS:\n");
            }
            sb.append("ID").append(" ".repeat(5)).append("|")
                    .append("NAME").append(" ".repeat(16)).append("|")
                    .append("COLOR").append(" ".repeat(10)).append("|")
                    .append("TIME").append(" ".repeat(16)).append("|")
                    .append("DATE").append(" ".repeat(8)).append("|\n");

            if (isPretty) {
                sb.append("            |_______|____________________|_______________|____________________|____________|\n");
            } else {
                sb.append("-".repeat(79));
                sb.append("\n");
            }
        }

        for (Event e : indivEvents) {
            if (isPretty) {
                sb.append("            |");
            }

            sb.append(e.getId()).append(" ".repeat(7 - String.valueOf(e.getId()).length())).append("|");

            if (e.getName().length() > 20)
                sb.append(e.getName(), 0, 20).append("|");
            else
                sb.append(e.getName()).append(" ".repeat(20 - e.getName().length())).append("|");

            sb.append(e.getColor()).append(" ".repeat(15 - String.valueOf(e.getColor()).length())).append("|");
            sb.append(e.getTimeStamp().toString()).append(" ".repeat(20 - e.getTimeStamp().toString().length())).append("|");
            sb.append(e.getDateStamp()).append(" ".repeat(12 - e.getDateStamp().length())).append("|");

            sb.append("\n");
        }

        if (!indivEvents.isEmpty()) {
            if (isPretty) {
                sb.append("            |_______|____________________|_______________|____________________|____________|\n");
            } else {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Creates a {@link com.planner.models.Task.SubTask} table, providing options for both dotted and pretty formats
     *
     * @param schedule list of days containing subtasks
     * @param isPretty whether to display table with pretty format
     * @return subtask table
     */
    public static String formatSubTaskTable(List<Day> schedule, boolean isPretty) {
        StringBuilder sb = new StringBuilder();
        if (isPretty) {
            sb.append("             SUBTASKS:\n" +
                    "            ________________________________________________________________________________________________________\n" +
                    "            |");
        } else {
            sb.append("SUBTASKS:\n");
        }
        sb.append("ID     |NAME                |COLOR          |HOURS     |TIME                |DATE        |DUE         |\n");

        if (isPretty) {
            sb.append("            |_______|____________________|_______________|__________|____________________|____________|____________|\n");
        } else {
            sb.append("-------------------------------------------------------------------------------------------------------\n");
        }

        for (Day day : schedule) {
            for (Task.SubTask subTask : day.getSubTasks()) {
                if (isPretty) {
                    sb.append("            |");
                }
                sb.append(subTask.getParentTask().getId()).append(" ".repeat(7 - String.valueOf(subTask.getParentTask().getId()).length())).append("|");

                if (subTask.getParentTask().getName().length() > 20)
                    sb.append(subTask.getParentTask().getName(), 0, 20).append("|");
                else
                    sb.append(subTask.getParentTask().getName()).append(" ".repeat(20 - subTask.getParentTask().getName().length())).append("|");

                sb.append(subTask.getParentTask().getColor()).append(" ".repeat(15 - String.valueOf(subTask.getParentTask().getColor()).length())).append("|");
                sb.append(subTask.getSubTaskHours()).append(" ".repeat(10 - String.valueOf(subTask.getSubTaskHours()).length())).append("|");
                sb.append(subTask.getTimeStamp().toString()).append(" ".repeat(20 - subTask.getTimeStamp().toString().length())).append("|");

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String date = sdf.format(subTask.getTimeStamp().getStart().getTime());
                sb.append(date).append(" ".repeat(12 - date.length())).append("|");
                sb.append(subTask.getParentTask().getDateStamp()).append(" ".repeat(12 - subTask.getParentTask().getDateStamp().length())).append("|");

                sb.append("\n");
            }
        }

        if (isPretty) {
            sb.append("            |_______|____________________|_______________|__________|____________________|____________|____________|\n");
        } else {
            sb.append("\n");
        }

        return sb.toString();
    }

    private static String getColorANSICode(Card.Colors color) {
        if (color == null) return "\u001B[38;2;3;155;229m";
        // Color Code - \001B[38;2;<r>;<g>;<b>m
        switch (color) {
            case RED:
                return "\u001B[38;2;213;0;0m";
            case ORANGE:
                return "\u001B[38;2;244;81;30m";
            case YELLOW:
                return "\u001B[38;2;246;191;38m";
            case GREEN:
                return "\u001B[38;2;11;128;67m";
            case LIGHT_BLUE:
                return "\u001B[38;2;3;155;229m";
            case BLUE:
                return "\u001B[38;2;63;81;181m";
            case INDIGO:
                return "\u001B[38;2;142;36;170m";
            case VIOLET:
                return "\u001B[38;2;121;134;203m";
            case BLACK:
                return "\u001B[38;2;97;97;97m";
            case LIGHT_GREEN:
                return "\u001B[38;2;51;182;121m";
            case LIGHT_CORAL:
                return "\u001B[38;2;230;124;115m";
            default:
                return "\u001B[38;2;207;211;203m";
        }
    }
}
