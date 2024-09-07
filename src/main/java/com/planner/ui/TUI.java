package com.planner.ui;

import com.planner.models.UserConfig;
import com.planner.util.JsonHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import static java.lang.Thread.sleep;

public class TUI {

    private void sessionStartUp() throws InterruptedException {
        // Welcome and basic info
        System.out.println(
                "#############################################################\n" +
                        "#         Welcome to the Simple Script Environment!         #\n" +
                        "#                  Scheduling Made Simple                   #\n" +
                        "#############################################################\n"
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Application Version: 0.6.0");
        sleep(1000);
        System.out.println("Current Date and Time: " + sdf.format(Calendar.getInstance().getTime()));

        // Config and System Info
        try {
            // Get Java version
            String javaVersionCommand = "java -version";
            String javaVersion = executeCommand(javaVersionCommand);
            UserConfig userConfig = JsonHandler.readUserConfig(Files.readString(Paths.get("settings/profile.json")));
            String sysInfo = getSystemInfo();

            System.out.println(
                    "Configuration Details:\n" +
                            "    - Config File: profile.json\n" +
                            "    - Mode: Production\n" +
                            "    - Range: " + Arrays.toString(userConfig.getDailyHoursRange()) + "\n" +
                            "    - Week Hours: " + Arrays.toString(userConfig.getHoursPerDayOfWeek()) + "\n" +
                            "    - Max Days: " + userConfig.getMaxDays() + "\n" +
                            "    - Archive Days: " + userConfig.getArchiveDays() + "\n" +
                            "    - Priority Scheduling: " + userConfig.isPriority() + "\n" +
                            "    - Overflow Handling: " + userConfig.isOverflow() + "\n" +
                            "    - Fit Day Schedule: " + userConfig.isFitDay() + "\n" +
                            "    - Scheduling Algorithm: " + userConfig.getSchedulingAlgorithm() + "\n" +
                            "    - Minimum Task Duration: " + userConfig.getMinHours() + "\n"
            );
        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException("Could not properly set up config for session");
        }
//        sleep(500);

        // Logs and warnings/alerts
        System.out.println(
                "Logs:\n" +
                        "    - System Log: logs/system.log\n" +
                        "    - Scripter Log: logs/scripter.log\n" +
                        "" +
                        "Warnings or Alerts:\n" +
                        "    - No alerts at this time. Check logs/system.log for future warnings.\n"
        );
        sleep(1000);

        // Repo & License
        System.out.println(
                        "GitHub Repo: https://github.com/AndrewRoe34/agile-planner\n" +
                        "\n" +
                        "License: MIT License\n" +
                        "-------------------------------------------------------------"
        );
        sleep(2000);
    }

    private String executeCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Command execution failed with exit code: " + exitCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        reader.close();

        return output.toString().trim();
    }

    private String getSystemInfo() throws IOException, InterruptedException {
        String osName = executeCommand("wmic os get name");
        osName = osName.substring(osName.indexOf("Microsoft")).split("\\|")[0].trim();

        String osVersion = executeCommand("wmic os get version");
        osVersion = osVersion.replaceAll("[^\\d.]", "").trim();

        // Get total and available memory
        String totalMemory = executeCommand("wmic ComputerSystem get TotalPhysicalMemory");
        totalMemory = totalMemory.replaceAll("[^\\d]", "").trim();
        long totalMemoryInMB = Long.parseLong(totalMemory) / (1024 * 1024);

        String availableMemory = executeCommand("wmic OS get FreePhysicalMemory");
        availableMemory = availableMemory.replaceAll("[^\\d]", "").trim();
        long availableMemoryInMB = Long.parseLong(availableMemory) / 1024;

        // Calculate used memory
        long usedMemoryInMB = totalMemoryInMB - availableMemoryInMB;

        // Get CPU load
        String cpuLoad = executeCommand("wmic cpu get loadpercentage");
        cpuLoad = cpuLoad.replaceAll("[^\\d]", "").trim();

        return "OS Name: " + osName + "\n" +
                "OS Version: " + osVersion + "\n" +
                "Initial Resource Usage:\n" +
                "    - Memory: " + usedMemoryInMB + "MB / " + totalMemoryInMB + "MB\n" +
                "    - CPU: " + cpuLoad + "% of 4 cores\n";
    }


    public static void main(String... args) throws IOException, InterruptedException {
        TUI tui = new TUI();
        Screen.clearScreen();
        tui.sessionStartUp();

        CLI cli = new CLI();
        cli.loop();
    }
}
