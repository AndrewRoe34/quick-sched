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
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class TUIContext {

    private TUIState tuiState;

    private static final TUIState sessionState = new SessionState();
    private static final TUIState configState = new ConfigState();

    public void setTuiState(TUIState tuiState) {
        this.tuiState = tuiState;
    }

    public void configurePage() {
        tuiState.setupAndDisplayPage();
    }

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
            UserConfig userConfig = JsonHandler.readUserConfig(Files.readString(Paths.get("settings/profile.cfg")));
            String sysInfo = getSystemInfo();

            System.out.println(
                    "Configuration Details:\n" +
                            "    - Config File: profile.cfg\n" +
                            "    - Mode: Production\n" +
                            "    - Range: " + Arrays.toString(userConfig.getRange()) + "\n" +
                            "    - Week Hours: " + Arrays.toString(userConfig.getWeek()) + "\n" +
                            "    - Max Days: " + userConfig.getMaxDays() + "\n" +
                            "    - Archive Days: " + userConfig.getArchiveDays() + "\n" +
                            "    - Priority Scheduling: " + userConfig.isPriority() + "\n" +
                            "    - Overflow Handling: " + userConfig.isOverflow() + "\n" +
                            "    - Fit Day Schedule: " + userConfig.isFitDay() + "\n" +
                            "    - Scheduling Algorithm: " + userConfig.getSchedulingAlgorithm() + "\n" +
                            "    - Minimum Task Duration: " + userConfig.getMinHours() + "\n" +
                            "    - Local Schedule Colors: "  + userConfig.isLocalScheduleColors() + "\n"
            );
            sleep(1000);
            System.out.print(sysInfo);
            System.out.println("Java Version:");
            System.out.println(javaVersion.trim() + "\n"); // Trimming to remove extra spaces
            sleep(1000);
        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException("Could not properly set up config and system info for session");
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

        // External dependencies
        System.out.println(
                "External Dependencies:\n" +
                        "    - com.google.code.gson:gson:2.10.1\n" +
                        "    - com.google.api-client:google-api-client:2.6.0\n" +
                        "    - com.google.oauth-client:google-oauth-client-jetty:1.34.1\n" +
                        "    - com.google.apis:google-api-services-calendar:v3-rev20220715-2.0.0\n" +
                        "    - org.dhatim:fastexcel:0.18.0\n" +
                        "\n" +
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
        TUIContext tuiContext = new TUIContext();
        TUIState.clearScreen();
        tuiContext.sessionStartUp();
//        TUIState.clearScreen();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 'man' to see all available commands.");
        while (true) {
            System.out.print("> ");
            if (scanner.hasNextLine()) {
                String userInput = scanner.nextLine();
                if (!userInput.isBlank()) {
                    switch (Character.toUpperCase(userInput.charAt(0))) {
                        case 'M':
                            System.out.println("S - Session\nE - Editor\nC - Config\nQ - Quit");
                            break;
                        case 'S':
                            tuiContext.setTuiState(sessionState);
                            tuiContext.configurePage();
                            TUIState.clearScreen();
                            break;
                        case 'C':
                            tuiContext.setTuiState(configState);
                            tuiContext.configurePage();
//                            TUIState.clearScreen();
                            break;
                        case 'Q':
                            System.exit(0);
                    }
                }
            } else break;
//            // clear the screen here to refresh the page
//            TUIState.clearScreen();
        }
    }
}
