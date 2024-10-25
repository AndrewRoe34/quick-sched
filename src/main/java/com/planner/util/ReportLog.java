package com.planner.util;

import com.planner.manager.ScheduleManager;
import com.planner.models.UserConfig;
import com.planner.ui.tables.TableFormatter;

import java.util.Arrays;

public class ReportLog {

    public static String buildReportLog(ScheduleManager scheduleManager) {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------\n" +
                "CONFIG INFORMATION\n" +
                "------------------------------------------\n");
        UserConfig userConfig = scheduleManager.getUserConfig();
        sb.append("Configuration File:   profile.json\n")
                .append("Mode:                 Production\n")
                .append("Range:                ").append(Arrays.toString(userConfig.getDailyHoursRange())).append("\n")
                .append("Week Hours:           ").append(Arrays.toString(userConfig.getHoursPerDayOfWeek())).append("\n")
                .append("Subtask Range:        ").append(Arrays.toString(userConfig.getSubtaskRange())).append("\n")
                .append("Max Days:             ").append(userConfig.getMaxDays()).append("\n")
                .append("Archive Days:         ").append(userConfig.getArchiveDays()).append("\n")
                .append("Priority Scheduling:  ").append(userConfig.isPriority() ? "Yes" : "No").append("\n")
                .append("Overflow Handling:    ").append(userConfig.isOverflow() ? "Yes" : "No").append("\n")
                .append("Optimize Day:         ").append(userConfig.isOptimizeDay() ? "Yes" : "No").append("\n")
                .append("Default at Start:     ").append(userConfig.isDefaultAtStart() ? "Yes" : "No").append("\n")
                .append("Pretty Time:          ").append(userConfig.isFormatPrettyTime() ? "Yes" : "No").append("\n\n");
        if (!scheduleManager.getRecurEvents().isEmpty() || !scheduleManager.getIndivEvents().isEmpty()) {
            sb.append(TableFormatter.formatEventSetTables(scheduleManager.getRecurEvents(), scheduleManager.getIndivEvents(), false));
            sb.append("\n\n");
        }

        if (!scheduleManager.getCards().isEmpty()) {
            sb.append(TableFormatter.formatCardTable(scheduleManager.getCards(), false));
            sb.append("\n\n");
        }

        if (scheduleManager.getNumTasks() > 0) {
            sb.append(TableFormatter.formatTaskTable(scheduleManager.getTaskManager(), scheduleManager.getArchivedTasks(), false));
            sb.append("\n\n");
        }

        if (!scheduleManager.getSchedule().isEmpty()) {
            sb.append(TableFormatter.formatSubTaskTable(scheduleManager.getSchedule(), false));
            sb.append("\n\n");
            sb.append(TableFormatter.formatScheduleTable(scheduleManager.getSchedule(), false));
            sb.append("\n");
        }
        return sb.toString();
    }
}
