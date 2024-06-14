package com.planner.ui;

import com.planner.models.UserConfig;
import com.planner.scripter.ScriptFSM;
import com.planner.ui.tables.TableFormatter;
import com.planner.util.JsonHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;

public class SessionState implements TUIState {

    Scanner scanner = new Scanner(System.in);

    @Override
    public void setupAndDisplayPage() {
        File file = new File("data/scripts");
        File[] files = file.listFiles();
        List<File> scriptList = new ArrayList<>();
        assert files != null;
        for (File f : files) {
            if (f.getName().length() > 5 && ".smpl".equals(f.getName().substring(f.getName().length() - 5))) {
                scriptList.add(f);
            }
        }
        System.out.println();
        System.out.println(TableFormatter.formatScriptOptionsTable(scriptList));
        System.out.print("\n                                                         Enter ID: ");
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (!input.isBlank() && hasInteger(input)) {
                int id = Integer.parseInt(input);
                if (id >= 0 && id < scriptList.size()) {
                    try {
                        TUIState.clearScreen();
                        try {
                            sessionStartUp(scriptList.get(id).getName());
                        } catch (InterruptedException e) {
                            throw new IllegalArgumentException("Could not handle sleep during session startup configuration");
                        }
                        ScriptFSM scriptFSM = new ScriptFSM();
                        scriptFSM.getScheduleManager().resetData();
                        scriptFSM.executeScript(scriptList.get(id).getAbsolutePath());
                        System.out.println(
                                "\n-------------------------------------------------------------\n" +
                                "#############################################################\n" +
                                        "#           Exiting the Simple Script Environment           #\n" +
                                        "#############################################################\n"
                        );
                        // need to have ScheduleManager reset (causing a lot of glitches when you keep rerunning the same file over and over)
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        System.out.print("\n\nWould you like to leave? ");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private boolean hasInteger(String s) {
        try {
            Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void sessionStartUp(String scriptName) throws InterruptedException {
        // Welcome and basic info
        System.out.println(
                        "#############################################################\n" +
                        "#         Welcome to the Simple Script Environment!         #\n" +
                        "#                  Scheduling Made Simple                   #\n" +
                        "#############################################################\n"
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Application Version: 0.5.0");
        sleep(1000);
        System.out.println("Current Date and Time: " + sdf.format(Calendar.getInstance().getTime()));
        sleep(1000);
        System.out.println("Script: " + scriptName);

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
        sleep(1000);
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

}
