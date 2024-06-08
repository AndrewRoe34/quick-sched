package com.planner.ui.tables;

import com.planner.models.Card;
import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    public static String formatScheduleTable(List<Day> schedule) {
        return "";
    }

    public static String formatBoardTable(List<Card> cards) {
        return "";
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
}
