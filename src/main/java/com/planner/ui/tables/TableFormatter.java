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
        String[] optionNames = {"RANGE", "WEEK", "SUBTASK_RANGE", "MAX_DAYS", "ARCHIVE_DAYS", "PRIORITY",
                "OVERFLOW", "OPTIMIZE_DAY", "DEFAULT_AT_START", "PRETTY_TIME"};

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
                case 2: optionValue = Arrays.toString(userConfig.getSubtaskRange()); break;
                case 3: optionValue = String.valueOf(userConfig.getMaxDays()); break;
                case 4: optionValue = String.valueOf(userConfig.getArchiveDays()); break;
                case 5: optionValue = String.valueOf(userConfig.isPriority()); break;
                case 6: optionValue = String.valueOf(userConfig.isOverflow()); break;
                case 7: optionValue = String.valueOf(userConfig.isOptimizeDay()); break;
                case 8: optionValue = String.valueOf(userConfig.isDefaultAtStart()); break;
                case 9: optionValue = String.valueOf(userConfig.isFormatPrettyTime()); break;
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
        sb.append("------------------------------------------\n");
        sb.append("SCHEDULE\n");
        sb.append("------------------------------------------\n\n");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        boolean flag = false;

        for (Day day : schedule) {
            if (flag) {
                sb.append("\n");
            } else {
                flag = true;
            }

            String date = sdf.format(day.getDate().getTime());
            // Print out the date
            sb.append("DATE: ").append(date).append("\n");
            sb.append("------------------------------------------\n");

            int taskIdx = 0;
            int eventIdx = 0;

            // Iterate over tasks and events for the day
            while (taskIdx < day.getNumSubTasks() || eventIdx < day.getNumEvents()) {
                Card.Color color = null;
                String name = "";
                String tag = "      -     "; // Default value with padding
                double hours = 0;
                String timeStamp = "";
                String idPrefix = "";  // To differentiate between tasks and events

                // Check whether to process an event or a task
                if (taskIdx >= day.getNumSubTasks() || (eventIdx < day.getNumEvents() &&
                        Time.isAfter(day.getSubTaskList().get(taskIdx).getTimeStamp().getStart(),
                                day.getEventList().get(eventIdx).getTimeStamp().getStart()))) {
                    // Handle event data
                    Event event = day.getEvent(eventIdx);
                    color = event.getCard() != null ? event.getCard().getColor() : null;
                    name = event.getName();
                    tag = event.getCard() != null ? event.getCard().getName() : tag;
                    hours = Time.getTimeInterval(event.getTimeStamp().getStart(), event.getTimeStamp().getEnd());
                    timeStamp = event.getTimeStamp().toString();
                    idPrefix = "E.ID:";  // Prefix for events
                    eventIdx++;
                } else {
                    // Handle task data
                    Task.SubTask subTask = day.getSubTask(taskIdx);
                    Task task = subTask.getParentTask();
                    color = task.getColor();
                    name = task.getName();
                    tag = task.getTag() != null ? task.getTag() : tag;
                    hours = subTask.getSubTaskHours();
                    timeStamp = subTask.getTimeStamp().toString();
                    idPrefix = "T.ID:";  // Prefix for tasks
                    taskIdx++;
                }

                // Optionally apply color
                if (useColor && color != null) {
                    sb.append(getColorANSICode(color));
                }

                // Format the ID prefix and value
                sb.append(idPrefix).append(" ").append(String.format("%-5d", idPrefix.equals("E.ID:") ? day.getEvent(eventIdx - 1).getId() : day.getSubTask(taskIdx - 1).getParentTask().getId()))
                        .append(" | ");

                // Format the name with a maximum width of 19 characters
                String formattedName = name.length() > 19 ? name.substring(0, 19) : name;
                int namePadding = 19 - formattedName.length();

                // Format the tag with a maximum width of 12 characters
                String formattedTag = tag.length() > 14 ? tag.substring(0, 14) : tag;
                int tagPadding = 14 - formattedTag.length();

                // Format and append the task/event details
                sb.append(formattedName)
                        .append(" ".repeat(namePadding))
                        .append("| ")
                        .append(formattedTag) // Adjusted for tag field width
                        .append(" ".repeat(tagPadding))  // Updated space for tag field
                        .append("|");

                // Format the hours to ensure it fits with width like | 1.0 hrs  |
                sb.append(String.format("%5.1f hrs ", hours))
                        .append("| ")
                        .append(timeStamp)
                        .append("\n");

                if (useColor) {
                    sb.append("\u001B[0m"); // Reset color
                }
            }

            for (String s : day.getOverflowErrors()) {
                sb.append(s).append("\n");
            }
        }

        sb.append("\n");
        return sb.toString();
    }



    public static String formatTaskTable(PriorityQueue<Task> currTasks, PriorityQueue<Task> archiveTasks, boolean useColor) {
        StringBuilder sb = new StringBuilder();

        sb.append("------------------------------------------\n");
        sb.append("TASKS\n");
        sb.append("------------------------------------------\n");

        sb.append("ID   | NAME                | CARD           | HOURS     | DUE         | ARCHIVED\n");
        sb.append("-----|---------------------|----------------|-----------|-------------|---------\n");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        List<Task> list = new ArrayList<>(archiveTasks);
        list.addAll(currTasks);

        list.sort(Comparator.comparingInt(Task::getId));

        Calendar curr = Time.getFormattedCalendarInstance(0);

        for (Task task : list) {
            Card.Color color = task.getColor();
            int id = task.getId();
            String name = task.getName();
            String tag = task.getTag() != null ? task.getTag() : "       -       ";
            double hours = task.getTotalHours();
            String due = task.getDateStamp();

            // Handle color if needed
            if (useColor && color != null) {
                sb.append(getColorANSICode(color));
            }

            // Dynamically adjust spacing after ID
            String idStr = String.valueOf(id);
            sb.append(idStr).append(" ".repeat(Math.max(0, 5 - idStr.length()))).append("| ");

            // NAME - Ensure max 19 characters
            if (name.length() > 19) {
                sb.append(name, 0, 19).append(" | ");
            } else {
                sb.append(name).append(" ".repeat(Math.max(0, 19 - name.length()))).append(" | ");
            }

            // TAG - Truncate to 14 characters if too long
            if (tag.length() > 14) {
                tag = tag.substring(0, 14);
            }
            sb.append(tag).append(" ".repeat(Math.max(0, 14 - tag.length()))).append(" | ");

            // HOURS
            sb.append(String.format("%.1f", hours)).append(" ".repeat(Math.max(0, 9 - String.format("%.1f", hours).length()))).append(" | ");

            // DUE
            sb.append(due).append("  | ");

            // ARCHIVED
            if ((!Time.doDatesMatch(task.getDueDate(), curr) && task.getDueDate().compareTo(curr) < 0) || task.getTotalHours() == 0) {
                sb.append("Yes\n");
            } else {
                sb.append("No\n");
            }

            if (useColor) {
                sb.append("\u001B[0m"); // Reset ANSI color
            }
        }

        return sb.toString();
    }

    public static String formatCardTable(List<Card> cards, boolean useColor) {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("------------------------------------------\n");
        sb.append("CARDS\n");
        sb.append("------------------------------------------\n");
        sb.append("ID   | NAME          | COLOR      \n");
        sb.append("-----|---------------|------------\n");

        // Card details
        for (Card card : cards) {
            String colorStr = card.getColor().toString();  // Convert color to String
            int id = card.getId();
            String name = card.getName();

            // Handle color if needed
            if (useColor && card.getColor() != null) {
                sb.append(getColorANSICode(card.getColor()));
            }

            // ID column (5 characters, padded)
            sb.append(id)
                    .append(" ".repeat(Math.max(0, 5 - String.valueOf(id).length())))
                    .append("| ");

            // NAME column (15 characters, truncated or padded)
            if (name.length() > 13) {
                sb.append(name, 0, 13).append(" | ");
            } else {
                sb.append(name).append(" ".repeat(13 - name.length())).append(" | ");
            }

            // COLOR column (12 characters, truncated or padded)
            sb.append(colorStr)
                    .append(" ".repeat(Math.max(0, 12 - colorStr.length())))
                    .append("\n");

            // Reset color formatting (if using ANSI codes)
            if (useColor) {
                sb.append("\u001B[0m");
            }
        }

        return sb.toString();
    }


    /**
     * Creates an event table consisting of both recurring and individual {@link Event}, providing options for both dotted and pretty formats
     *
     * @param recurringEvents list of recurring events
     * @param indivEvents list of individual events
     * @param useColor whether to display color or not
     * @return event table
     */
    public static String formatEventSetTables(List<List<Event>> recurringEvents, List<Event> indivEvents, boolean useColor) {
        StringBuilder sb = new StringBuilder();

        boolean recurringEventsExist = false;
        for (List<Event> events : recurringEvents) {
            if (!events.isEmpty()) {
                recurringEventsExist = true;
                break;
            }
        }

        if (recurringEventsExist) {
            sb.append("------------------------------------------\n");
            sb.append("RECURRING EVENTS\n");
            sb.append("------------------------------------------\n");

            sb.append("ID   | NAME                | CARD           | TIME            | DAYS\n");
            sb.append("-----|---------------------|----------------|-----------------|----------------\n");

            HashSet<Event> uniqueRecurringEvents = new HashSet<>();
            for (List<Event> events : recurringEvents) {
                uniqueRecurringEvents.addAll(events);
            }

            for (Event e : uniqueRecurringEvents) {
                if (e.getCard() != null && useColor) {
                    String colorANSICode = getColorANSICode(e.getCard().getColor());
                    sb.append(colorANSICode);
                }

                sb.append(String.format("%-4d", e.getId())).append(" | ");

                String name = e.getName();
                sb.append(name.length() > 19 ? name.substring(0, 19) : String.format("%-19s", name)).append(" | ");

                String tag = e.getCard() == null ? "       -       " : e.getCard().getName();
                sb.append(tag.length() > 14 ? tag.substring(0, 14) : String.format("%-14s", tag)).append(" | ");

                sb.append(e.getTimeStamp()).append(" | ");

                sb.append(Arrays.toString(
                                e.getDays()),
                        1,
                        Arrays.toString(e.getDays()).length() - 1
                ).append("\n");

                if (e.getCard() != null && useColor) {
                    sb.append("\u001B[0m");
                }
            }

            sb.append("\n");
        }

        if (!indivEvents.isEmpty()) {
            sb.append("------------------------------------------\n");
            sb.append("INDIVIDUAL EVENTS\n");
            sb.append("------------------------------------------\n");

            sb.append("ID   | NAME                | CARD           | TIME            | DATE\n");
            sb.append("-----|---------------------|----------------|-----------------|------------\n");

            for (Event e : indivEvents) {
                if (e.getCard() != null && useColor) {
                    String colorANSICode = getColorANSICode(e.getCard().getColor());
                    sb.append(colorANSICode);
                }

                sb.append(String.format("%-4d", e.getId())).append(" | ");

                String name = e.getName();
                sb.append(name.length() > 19 ? name.substring(0, 19) : String.format("%-19s", name)).append(" | ");

                String tag = e.getCard() == null ? "       -       " : e.getCard().getName();
                sb.append(tag.length() > 14 ? tag.substring(0, 14) : String.format("%-14s", tag)).append(" | ");

                sb.append(e.getTimeStamp()).append(" | ");

                sb.append(String.format(e.getDateStamp())).append("\n");

                if (e.getCard() != null && useColor) {
                    sb.append("\u001B[0m");
                }
            }

            sb.append("\n");
        }

        return sb.toString();
    }


    /**
     * Creates a {@link com.planner.models.Task.SubTask} table, providing options for both dotted and pretty formats
     *
     * @param schedule list of days containing subtasks
     * @param useColor whether to display color or not
     * @return subtask table
     */
    public static String formatSubTaskTable(List<Day> schedule, boolean useColor) {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("------------------------------------------\n");
        sb.append("SUBTASKS\n");
        sb.append("------------------------------------------\n");
        sb.append("ID   | NAME                | CARD           | HOURS | TIME            | DATE       | DUE        \n");
        sb.append("-----|---------------------|----------------|-------|-----------------|------------|------------\n");

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Iterate through the schedule and each subtask
        for (Day day : schedule) {
            for (Task.SubTask subTask : day.getSubTaskList()) {
                // Get color if needed
                if (subTask.getParentTask().getCard() != null && useColor) {
                    String colorANSICode = getColorANSICode(subTask.getParentTask().getColor());
                    sb.append(colorANSICode);
                }

                // ID (5 characters, left-aligned)
                int id = subTask.getParentTask().getId();
                sb.append(String.format("%-5d", id)).append("| ");

                // NAME (20 characters, left-aligned, truncated if longer)
                String name = subTask.getParentTask().getName();
                sb.append(String.format("%-20s", name.length() > 20 ? name.substring(0, 20) : name)).append("| ");

                // TAG (15 characters, left-aligned, truncated if longer)
                String tag = subTask.getParentTask().getTag() != null ? subTask.getParentTask().getTag() : "       -       ";
                sb.append(String.format("%-15s", tag.length() > 15 ? tag.substring(0, 15) : tag)).append("| ");

                // HOURS (5 characters, right-aligned)
                String hours = String.format("%.1f", subTask.getSubTaskHours());
                sb.append(String.format("%-6s", hours)).append("| ");

                // TIME (17 characters, left-aligned, truncated if longer)
                String time = subTask.getTimeStamp().toString(); // Adjust this to the desired time format
                sb.append(String.format("%-16s", time)).append("| ");

                // DATE (10 characters, left-aligned, formatted)
                String date = sdf.format(subTask.getTimeStamp().getStart().getTime());
                sb.append(String.format("%-11s", date)).append("| ");

                // DUE (10 characters, left-aligned, formatted)
                String due = subTask.getParentTask().getDateStamp();
                sb.append(String.format("%-10s", due));

                // Reset color formatting (if using ANSI codes)

                if (subTask.getParentTask().getCard() != null && useColor) {
                    sb.append("\u001B[0m");
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    public static String getColorANSICode(Card.Color color) {
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
