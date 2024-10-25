package com.planner.ui;

import com.planner.models.UserConfig;
import com.planner.util.JsonHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import static java.lang.Thread.sleep;

public class TUI {

    private void sessionStartUp() throws InterruptedException {
        // Welcome and basic info
        System.out.println("#########################################################\n" +
                "#             Welcome to Quick Sched 1.2.0!             #\n" +
                "#                 Scheduling Made Simple                #\n" +
                "#########################################################\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Application Version: 1.2.0");
        sleep(1000);
        System.out.println("Current Date and Time: " + sdf.format(Calendar.getInstance().getTime()));

        // Config and System Info
        try {
            UserConfig userConfig = JsonHandler.readUserConfig(Files.readString(Paths.get("settings/profile.json")));

            System.out.println(
                    "Configuration Details:\n" +
                            "    - Config File: profile.json\n" +
                            "    - Mode: Production\n" +
                            "    - Range: " + Arrays.toString(userConfig.getDailyHoursRange()) + "\n" +
                            "    - Week Hours: " + Arrays.toString(userConfig.getHoursPerDayOfWeek()) + "\n" +
                            "    - Subtask Range: " + Arrays.toString(userConfig.getSubtaskRange()) + "\n" +
                            "    - Max Days: " + userConfig.getMaxDays() + "\n" +
                            "    - Archive Days: " + userConfig.getArchiveDays() + "\n" +
                            "    - Priority Scheduling: " + userConfig.isPriority() + "\n" +
                            "    - Overflow Handling: " + userConfig.isOverflow() + "\n" +
                            "    - Optimize Day: " + userConfig.isOptimizeDay() + "\n" +
                            "    - Default at Start: " + userConfig.isDefaultAtStart() + "\n" +
                            "    - Pretty Time: " + userConfig.isFormatPrettyTime() + "\n"
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not properly set up config for session");
        }

        // Logs and warnings/alerts
        System.out.println(
                "Logs:\n" +
                        "    - System Log: logs/system.log\n" +
                        "    - Session Log: logs/session.log\n" +
                        "Warnings or Alerts:\n" +
                        "    - Google Calendar is available only to test participants. To request, contact: aproe343@gmail.com\n"
        );
        sleep(1000);

        // Repo & License
        System.out.println(
                "GitHub Repo: https://github.com/AndrewRoe34/quick-sched\n\n" +
                        "License: MIT License\n" +
                        "-------------------------------------------------------------"
        );

        sleep(1000);
    }


    public static void main(String... args) throws IOException, InterruptedException {
        TUI tui = new TUI();
        Screen.clearScreen();
        tui.sessionStartUp();

        CLI cli = new CLI();
        cli.loop();
    }
}
