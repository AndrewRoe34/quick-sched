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

public class TableFormatter {

    public static String formatUserConfigTable(UserConfig userConfig) {
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

    public static String formatScriptOptionsTable(List<File> scriptList) {
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

    public static String formatPrettyScheduleTable(List<Day> schedule) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar date = Time.getFormattedCalendarInstance(0);
        sb.append("                                                                            Weekly Schedule\n"); // todo need to make dependent upon num of days
        sb.append("                        ");
        for (int i = 0; i < Math.min(schedule.size(), 6); i++) {
            sb.append("_________________________________________");
        }
        sb.append("_\n");
        int maxItems = 0;
        int[][] task_eventPair = new int[schedule.size()][2];
        sb.append("                        |");
        for (int i = 0; i < Math.min(schedule.size(), 6); i++) {
            sb.append(sdf.format(date.getTime()));
            sb.append("                              |");
            maxItems = Math.max(maxItems, schedule.get(i).getNumSubTasks() + schedule.get(i).getEventList().size());
            task_eventPair[i][0] = 0;
            task_eventPair[i][1] = 0;
            date = Time.getFormattedCalendarInstance(date, 1);
        }

        sb.append("\n                        |");
        for (int i = 0; i < Math.min(schedule.size(), 6); i++) {
            sb.append("________________________________________|");
        }

        for (int i = 0; i < maxItems; i++) {
            sb.append("\n");
            sb.append("                        |");
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

//                    if (userConfig.isLocalScheduleColors()) {
//                        String colorANSICode = getColorANSICode((color));
//                        sb.append(colorANSICode);
//                    }

                    sb.append(ts).append(" - "); // 18 char

                    if (name.length() > 21) {
                        sb.append(name, 0, 22);
                    } else {
                        sb.append(name);
                        sb.append(" ".repeat(22 - name.length()));
                    }

//                    if (userConfig.isLocalScheduleColors())
//                        sb.append("\u001B[0m");

                    sb.append("|");

                } else {
                    sb.append("                                        |");
                }
            }
        }
        sb.append("\n");
        sb.append("                        |");
        for (int i = 0; i < Math.min(schedule.size(), 6); i++) {
            sb.append("________________________________________|");
        }
        sb.append("\n");

        return sb.toString();
    }

    public static String formatDottedScheduleTable(List<Day> schedule, UserConfig userConfig) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar date = Time.getFormattedCalendarInstance(0);

        int maxItems = 0;
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
        for (int i = 0; i < Math.min(schedule.size(), 6); i++) {
            sb.append("-----------------------------------------");
        }

        for (int i = 0; i < maxItems; i++) {
            sb.append("\n");

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

        return sb.toString();
    }

    public static String formatPrettyBoardTable(List<Card> cards) {
        return "";
    }

    public static String formatDottedBoardTable(List<Card> cards, UserConfig userConfig, PriorityQueue<Task> archivedTasks) {
        StringBuilder sb = new StringBuilder();

        // use foreach loop to determine max number of tasks while printing out the first line of Cards
        int maxTasks = 0;
        boolean defaultCardIsEmpty = false;
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

                for (int i = c1.toString().length(); i < 40; i++) {
                    sb.append(" ");
                }
            }

            if (userConfig.isLocalScheduleColors())
                sb.append("\u001B[0m");

            sb.append("|");
        }

        sb.append("\n");
        int cardCount = defaultCardIsEmpty ? 1 : 0;
        for (; cardCount < cards.size(); cardCount++) {
            sb.append("-----------------------------------------");
        }

        // use foreach loop inside a for loop to output the tasks
        for (int i = 0; i < maxTasks; i++) {
            sb.append("\n");

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

        return sb.toString();
    }

    public static String formatRecurringEventsTable() {
        return "";
    }

    public static String formatIndividualEventsTable() {
        return "";
    }

    public static String formatEventSetTables() {
        return "";
    }

    public static String formatTasksTable() {
        return "";
    }

    public static String formatCardsTable() {
        return "";
    }

    public static void main(String[] args) {
        File file = new File("data/scripts");
        File[] list = file.listFiles();
        assert list != null;
        List<File> files = Arrays.asList(list);

        System.out.println(formatScriptOptionsTable(files));
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
