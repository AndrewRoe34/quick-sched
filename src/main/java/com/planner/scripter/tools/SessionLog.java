package com.planner.scripter.tools;

import com.planner.manager.ScheduleManager;
import com.planner.models.UserConfig;
import com.planner.ui.tables.TableFormatter;

import java.util.Arrays;

public class SessionLog {

    public static String buildSessionLog(ScheduleManager scheduleManager, String script) {
        StringBuilder sb = new StringBuilder();
        sb.append("Script: ").append(script).append("\n");
        UserConfig userConfig = scheduleManager.getUserConfig();
        sb.append("Configuration Details:\n" + "    - Config File: profile.json\n" + "    - Mode: Production\n" + "    - Range: ")
                .append(Arrays.toString(userConfig.getRange())).append("\n")
                .append("    - Week Hours: ").append(Arrays.toString(userConfig.getWeek()))
                .append("\n").append("    - Max Days: ").append(userConfig.getMaxDays()).append("\n")
                .append("    - Archive Days: ").append(userConfig.getArchiveDays()).append("\n")
                .append("    - Priority Scheduling: ").append(userConfig.isPriority()).append("\n")
                .append("    - Overflow Handling: ").append(userConfig.isOverflow()).append("\n")
                .append("    - Fit Day Schedule: ").append(userConfig.isFitDay()).append("\n")
                .append("    - Scheduling Algorithm: ").append(userConfig.getSchedulingAlgorithm()).append("\n")
                .append("    - Minimum Task Duration: ").append(userConfig.getMinHours()).append("\n")
                .append("    - Local Schedule Colors: ").append(userConfig.isLocalScheduleColors()).append("\n\n");
        userConfig.setLocalScheduleColors(false);
        if (!scheduleManager.getRecurringEvents().isEmpty() || !scheduleManager.getIndivEvents().isEmpty()) {
            sb.append(TableFormatter.formatEventSetTables(scheduleManager.getRecurringEvents(), scheduleManager.getIndivEvents(), userConfig));
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
            sb.append(TableFormatter.formatSubTaskTable(scheduleManager.getSchedule(), userConfig));
            sb.append("\n\n");
            sb.append(TableFormatter.formatScheduleTable(scheduleManager.getSchedule(), false));
            sb.append("\n");
        }
        userConfig.setLocalScheduleColors(true);
        return sb.toString();
    }
}
