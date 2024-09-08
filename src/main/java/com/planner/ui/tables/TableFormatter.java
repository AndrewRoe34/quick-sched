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
                "SCHED_ALGORITHM", "MIN_HOURS", "OPTIMIZE_DAY", "DEFAULT_AT_START"};

        StringBuilder sb = new StringBuilder();
        sb.append("                                                            Settings Options\n");
        sb.append("                                         _______________________________________________________\n");
        sb.append("                                         |ID    |OPTION             | DATA                     |\n");
        sb.append("                                         |______|___________________|__________________________|\n");

        for (int i = 0; i < optionNames.length; i++) {
            String optionValue = "";
            switch (i) {
                case 0: optionValue = Arrays.toString(userConfig.getDailyHoursRange()); break;
                case 1: optionValue = Arrays.toString(userConfig.getHoursPerDayOfWeek()); break;
                case 2: optionValue = String.valueOf(userConfig.getMaxDays()); break;
                case 3: optionValue = String.valueOf(userConfig.getArchiveDays()); break;
                case 4: optionValue = String.valueOf(userConfig.isPriority()); break;
                case 5: optionValue = String.valueOf(userConfig.isOverflow()); break;
                case 6: optionValue = String.valueOf(userConfig.isFitDay()); break;
                case 7: optionValue = String.valueOf(userConfig.getSchedulingAlgorithm()); break;
                case 8: optionValue = String.valueOf(userConfig.getMinHours()); break;
                case 9: optionValue = String.valueOf(userConfig.isOptimizeDay()); break;
                case 10: optionValue = String.valueOf(userConfig.isDefaultAtStart()); break;
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
        sb.append("                                         +------------------------------------------+\n");
        sb.append("                                         |ID    |Name                 | DATE        |\n");
        sb.append("                                         +------+---------------------+-------------+\n");
        int id = 0;
        for (File file : scriptList) {
            Date date = new Date(file.lastModified());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = simpleDateFormat.format(date);
            sb.append("                                         ");
            String formattedOptionValue = String.format("|%-6d|%-19s  |%-13s|\n", id++, file.getName(), strDate);
            sb.append(formattedOptionValue);
        }
        sb.append("                                         +------+---------------------+-------------+");
        return sb.toString();
    }

    /**
     * Creates a schedule table consisting of {@link Day}, providing options for both dotted and pretty formats
     *
     * @param schedule list of scheduled days
     * @return dotted schedule table
     */
    public static String formatScheduleTable(List<Day> schedule, boolean useColor) {
        StringBuilder sb = new StringBuilder();
        sb.append("SCHEDULE:\n");
        sb.append("ID     |NAME                |TAG            |HOURS     |TIME                |DUE         |\n");
        sb.append("------------------------------------------------------------------------------------------\n");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        boolean flag = false;
        for (Day day : schedule) {
            if (flag) {
                sb.append("\n");
            } else flag = true;

            String date = sdf.format(day.getDate().getTime());
            // print out day here
            sb.append(date).append("\n");

            int taskIdx = 0;
            int eventIdx = 0;
            while (taskIdx < day.getNumSubTasks() || eventIdx < day.getNumEvents()) {
                Card.Color color = null;
                int id = 0;
                String name = "";
                String tag = "       -       ";
                double hours = 0;
                String timeStamp = "";
                String due = "     -    ";
                if (taskIdx >= day.getNumSubTasks() || eventIdx < day.getNumEvents() &&
                        Time.isAfter(day.getSubTaskList().get(taskIdx).getTimeStamp().getStart(),
                                day.getEventList().get(eventIdx).getTimeStamp().getStart())) {
                    Event event = day.getEvent(eventIdx);

                    color = event.getCard() != null ? event.getCard().getColor() : null;
                    id = event.getId();
                    name = event.getName();
                    hours = Time.getTimeInterval(event.getTimeStamp().getStart(), event.getTimeStamp().getEnd());
                    timeStamp = event.getTimeStamp().toString();
                    eventIdx++;
                } else {
                    // handle Task data
                    Task.SubTask subTask = day.getSubTask(taskIdx);
                    Task task = subTask.getParentTask();

                    color = task.getColor();
                    id = task.getId();
                    name = task.getName();
                    tag = task.getTag() != null ? task.getTag() : tag;
                    hours = subTask.getSubTaskHours();
                    timeStamp = subTask.getTimeStamp().toString();
                    due = task.getDateStamp();
                    taskIdx++;
                }

                if (useColor && color != null) {
                    sb.append(getColorANSICode(color));
                }

                sb.append(id)
                        .append(" ".repeat(7 - String.valueOf(id).length()))
                        .append("|");

                if (name.length() > 20) {
                    sb.append(name, 0, 20).append("|");
                } else {
                    sb.append(name)
                            .append(" ".repeat(20 - name.length()))
                            .append("|");
                }
                sb.append(tag)
                        .append(" ".repeat(15 - tag.length()))
                        .append("|");
                sb.append(hours)
                        .append(" ".repeat(10 - String.valueOf(hours).length()))
                        .append("|");
                sb.append(timeStamp)
                        .append("     |");
                sb.append(due)
                        .append("  |\n");
                if (useColor) {
                    sb.append("\u001B[0m");
                }
            }
        }

        sb.append("\n");

        return sb.toString();
    }

    public static String formatTaskTable(PriorityQueue<Task> currTasks, PriorityQueue<Task> archiveTasks, boolean useColor) {
        StringBuilder sb = new StringBuilder();
        sb.append("TASKS:\n");
        sb.append("ID     |NAME                |TAG            |HOURS     |DUE         |ARCHIVED |\n");
        sb.append("-------------------------------------------------------------------------------\n");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        List<Task> list = new ArrayList<>(archiveTasks);
        int numArchived = list.size();
        list.addAll(currTasks);

        int idx = 0;
        for (Task task : list) {
            Card.Color color = task.getColor();
            int id = task.getId();
            String name = task.getName();
            String tag = task.getTag() != null ? task.getTag() : "       -       ";
            double hours = task.getTotalHours();
            String due = task.getDateStamp();

            if (useColor && color != null) {
                sb.append(getColorANSICode(color));
            }

            sb.append(id)
                    .append(" ".repeat(7 - String.valueOf(id).length()))
                    .append("|");

            if (name.length() > 20) {
                sb.append(name, 0, 20).append("|");
            } else {
                sb.append(name)
                        .append(" ".repeat(20 - name.length()))
                        .append("|");
            }
            sb.append(tag)
                    .append(" ".repeat(15 - tag.length()))
                    .append("|");
            sb.append(hours)
                    .append(" ".repeat(10 - String.valueOf(hours).length()))
                    .append("|");
            sb.append(due)
                    .append("  |");
            if (idx < numArchived) {
                sb.append("TRUE     |\n");
            } else {
                sb.append("FALSE    |\n");
            }
            if (useColor) {
                sb.append("\u001B[0m");
            }
            idx++;
        }

        sb.append("\n");

        return sb.toString();
    }

    public static String formatCardTable(List<Card> cards, boolean useColor) {
        StringBuilder sb = new StringBuilder();
        sb.append("CARDS:\n");
        sb.append("ID     |NAME                |COLOR          |\n");
        sb.append("---------------------------------------------\n");

        for (Card card : cards) {
            String colors = card.getColor() + "";
            int id = card.getId();
            String name = card.getName();

            if (useColor && card.getColor() != null) {
                sb.append(getColorANSICode(card.getColor()));
            }

            sb.append(id)
                    .append(" ".repeat(7 - String.valueOf(id).length()))
                    .append("|");

            if (name.length() > 20) {
                sb.append(name, 0, 20).append("|");
            } else {
                sb.append(name)
                        .append(" ".repeat(20 - name.length()))
                        .append("|");
            }
            sb.append(colors)
                    .append(" ".repeat(15 - colors.length()))
                    .append("|\n");

            if (useColor) {
                sb.append("\u001B[0m");
            }
        }

        sb.append("\n");

        return sb.toString();
    }

    /**
     * Creates an event table consisting of both recurring and individual {@link Event}, providing options for both dotted and pretty formats
     *
     * @param recurringEvents list of recurring events
     * @param indivEvents list of individual events
     * @param userConfig user settings for table format
     * @return event table
     */
    public static String formatEventSetTables(List<List<Event>> recurringEvents, List<Event> indivEvents, UserConfig userConfig) {
        StringBuilder sb = new StringBuilder();

        boolean recurringEventsExist = false;
        for (List<Event> events : recurringEvents) {
            if (!events.isEmpty()) {
                recurringEventsExist = true;
                break;
            }
        }

        if (recurringEventsExist) {
            sb.append("RECURRING EVENTS:\n");

            sb.append("ID").append(" ".repeat(5)).append("|");
            sb.append("NAME").append(" ".repeat(16)).append("|");
            sb.append("COLOR").append(" ".repeat(10)).append("|");
            sb.append("TIME").append(" ".repeat(16)).append("|");
            sb.append("DAYS").append(" ".repeat(29)).append("|\n");

            sb.append("----------------------------------------------------------------------------------------------------");
            sb.append("\n");
        }

        HashSet<Event> uniqueRecurringEvents = new HashSet<>();
        for (List<Event> events : recurringEvents) {
            uniqueRecurringEvents.addAll(events);
        }

        for (Event e : uniqueRecurringEvents) {
            if (e.getCard() != null) {
                String colorANSICode = getColorANSICode((e.getCard().getColor()));
                sb.append(colorANSICode);
            }

            sb.append(e.getId()).append(" ".repeat(7 - String.valueOf(e.getId()).length())).append("|");

            if (e.getName().length() > 20)
                sb.append(e.getName(), 0, 20).append("|");
            else
                sb.append(e.getName()).append(" ".repeat(20 - e.getName().length())).append("|");

            if (e.getCard() == null) {
                sb.append("     -").append(" ".repeat(15 - "     -".length())).append("|");
            } else {
                sb.append(e.getCard().getColor()).append(" ".repeat(15 - String.valueOf(e.getCard().getColor()).length())).append("|");
            }

            sb.append(e.getTimeStamp().toString()).append(" ".repeat(
                    20 - e.getTimeStamp().toString().length())
            ).append("|");

            sb.append(Arrays.toString(
                            e.getDays()),
                    1,
                    Arrays.toString(e.getDays()).length() - 1
            ).append(" ".repeat(33 - Arrays.toString(e.getDays()).length() + 2)).append("|");

            sb.append("\u001B[0m");

            sb.append("\n");
        }

        if (recurringEventsExist) {
            sb.append("\n");
        }

        if (!indivEvents.isEmpty()) {
            sb.append("INDIVIDUAL EVENTS:\n");

            sb.append("ID").append(" ".repeat(5)).append("|")
                    .append("NAME").append(" ".repeat(16)).append("|")
                    .append("COLOR").append(" ".repeat(10)).append("|")
                    .append("TIME").append(" ".repeat(16)).append("|")
                    .append("DATE").append(" ".repeat(8)).append("|\n");

            sb.append("-".repeat(79));
            sb.append("\n");
        }

        for (Event e : indivEvents) {
            if (e.getCard() != null) {
                String colorANSICode = getColorANSICode((e.getCard().getColor()));
                sb.append(colorANSICode);
            }

            sb.append(e.getId()).append(" ".repeat(7 - String.valueOf(e.getId()).length())).append("|");

            if (e.getName().length() > 20)
                sb.append(e.getName(), 0, 20).append("|");
            else
                sb.append(e.getName()).append(" ".repeat(20 - e.getName().length())).append("|");


            if (e.getCard() == null) {
                sb.append("     -").append(" ".repeat(15 - "     -".length())).append("|");
            } else {
                sb.append(e.getCard().getColor()).append(" ".repeat(15 - String.valueOf(e.getCard().getColor()).length())).append("|");
            }


            sb.append(e.getTimeStamp().toString()).append(" ".repeat(20 - e.getTimeStamp().toString().length())).append("|");
            sb.append(e.getDateStamp()).append(" ".repeat(12 - e.getDateStamp().length())).append("|");

            sb.append("\u001B[0m");

            sb.append("\n");
        }

        if (!indivEvents.isEmpty()) {
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Creates a {@link com.planner.models.Task.SubTask} table, providing options for both dotted and pretty formats
     *
     * @param schedule list of days containing subtasks
     * @param userConfig user settings for table format
     * @return subtask table
     */
    public static String formatSubTaskTable(List<Day> schedule, UserConfig userConfig) {
        StringBuilder sb = new StringBuilder();

        sb.append("SUBTASKS:\n");

        sb.append("ID     |NAME                |TAG            |HOURS     |TIME                |DATE        |DUE         |\n");

        sb.append("-------------------------------------------------------------------------------------------------------\n");

        for (Day day : schedule) {
            for (Task.SubTask subTask : day.getSubTaskList()) {
                String colorANSICode = getColorANSICode((subTask.getParentTask().getColor()));
                sb.append(colorANSICode);

                sb.append(subTask.getParentTask().getId()).append(" ".repeat(7 - String.valueOf(subTask.getParentTask().getId()).length())).append("|");

                if (subTask.getParentTask().getName().length() > 20)
                    sb.append(subTask.getParentTask().getName(), 0, 20).append("|");
                else
                    sb.append(subTask.getParentTask().getName()).append(" ".repeat(20 - subTask.getParentTask().getName().length())).append("|");

                String tag = "       -       ";
                tag = subTask.getParentTask().getTag() != null ? subTask.getParentTask().getTag() : tag;
                sb.append(tag).append(" ".repeat(15 - tag.length())).append("|");

                sb.append(subTask.getSubTaskHours()).append(" ".repeat(10 - String.valueOf(subTask.getSubTaskHours()).length())).append("|");
                sb.append(subTask.getTimeStamp().toString()).append(" ".repeat(20 - subTask.getTimeStamp().toString().length())).append("|");

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String date = sdf.format(subTask.getTimeStamp().getStart().getTime());
                sb.append(date).append(" ".repeat(12 - date.length())).append("|");
                sb.append(subTask.getParentTask().getDateStamp()).append(" ".repeat(12 - subTask.getParentTask().getDateStamp().length())).append("|");

                sb.append("\u001B[0m");
                sb.append("\n");
            }
        }

        sb.append("\n");

        return sb.toString();
    }

    private static String getColorANSICode(Card.Color color) {
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
