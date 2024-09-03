package com.planner.ui;

import com.planner.models.UserConfig;
import com.planner.ui.tables.TableFormatter;
import com.planner.util.JsonHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConfigState implements TUIState {

    private boolean writeToFile;
    private UserConfig userConfig;
    private String configTable;
    private final Scanner scanner;

    public ConfigState() {
        try {
            userConfig = JsonHandler.readUserConfig(Files.readString(Paths.get("settings/profile.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        configTable = TableFormatter.formatPrettyUserConfigTable(userConfig);
        scanner = new Scanner(System.in);
    }

    @Override
    public void setupAndDisplayPage() {
        formatTablePrompt();
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.isBlank()) return;
            if (Character.toUpperCase(input.charAt(0)) == 'Y') {
                formatOptionPrompt();
            }
        }

        if (writeToFile) {
            // need to update the data here
            Path path = Paths.get("settings/profile.cfg");
            try {
                Files.write(path, JsonHandler.createUserConfig(userConfig).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            writeToFile = false;
            configTable = TableFormatter.formatPrettyUserConfigTable(userConfig);
        }
    }

    private void formatTablePrompt() {
        System.out.println();
        System.out.println(configTable);
        System.out.print("\n" +
                "\n" +
                "                                                 Would you like to edit a setting?\n" +
                "\n" +
                "                                                            (Y/N): ");
    }

    private void formatOptionPrompt() {
        System.out.print("\n");
        while (true) {
            System.out.print("                                                         Enter ID: ");
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if (!input.isBlank() && hasInteger(input)) {
                    int id = Integer.parseInt(input);
                    if (id < 0 || id > 10) break;
                    formatConfigPrompt(id);
                    break;
                }
            }
        }
    }

    private void formatConfigPrompt(int id) {
        writeToFile = true;
        switch (id) {
            case 0:
                System.out.print("\n                                                 Hours of operation range");
                System.out.print("\n                                                       Input [# #]: ");
                if (scanner.hasNextLine()) {
                    String[] tokens = scanner.nextLine().split(" ");
                    if (tokens.length == 2 && hasInteger(tokens[0]) && hasInteger(tokens[1])) {
                        int start = Integer.parseInt(tokens[0]);
                        int end = Integer.parseInt(tokens[1]);
                        int[] range = userConfig.getRange();
                        range[0] = start;
                        range[1] = end;
                        userConfig.setRange(range);
                    } else throw new IllegalArgumentException("Invalid inputs provided for config option Range");
                }
                break;
            case 1:
                System.out.print("\n                                                 Global hours for week");
                System.out.print("\n                                                 Input [# # # # # # #]: ");
                if (scanner.hasNextLine()) {
                    String[] tokens = scanner.nextLine().split(" ");
                    int[] week = new int[tokens.length];
                    boolean validInput = true;
                    for (int i = 0; i < week.length; i++) {
                        if (!hasInteger(tokens[i])) {
                            validInput = false;
                            break;
                        }
                        int hour = Integer.parseInt(tokens[i]);
                        if (hour < 0 || hour > 24) {
                            validInput = false;
                            break;
                        }
                        week[i] = hour;
                    }
                    if (validInput) {
                        userConfig.setWeek(week);
                    } else {
                        throw new IllegalArgumentException("Invalid inputs provided for config option Global hours for week");
                    }
                }
                break;
            case 2:
                System.out.print("\n                                                 Maximum number of days to display");
                System.out.print("\n                                                       Input [#]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (hasInteger(input)) {
                        int maxDays = Integer.parseInt(input);
                        userConfig.setMaxDays(maxDays);
                    } else {
                        throw new IllegalArgumentException("Invalid input for max days config option, expected integer but was not");
                    }
                }
                break;
            case 3:
                System.out.print("\n                                                 Maximum number of past days");
                System.out.print("\n                                                       Input [#]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (hasInteger(input)) {
                        int archiveDays = Integer.parseInt(input);
                        userConfig.setArchiveDays(archiveDays);
                    } else {
                        // Do nothing or provide feedback to the user regarding invalid input
                    }
                }
                break;
            case 4:
                HashMap<String, Boolean> priorityMap = promptBooleanInput("Enable priority for tasks");
                if (priorityMap.get("valid")) {
                    userConfig.setPriority(priorityMap.get("input"));
                }
                break;
            case 5:
                HashMap<String, Boolean> overflowMap = promptBooleanInput("Display overflow");
                if (overflowMap.get("valid")) {
                    userConfig.setOverflow(overflowMap.get("input"));
                }
                break;
            case 6:
                HashMap<String, Boolean> scheduleMap = promptBooleanInput("Fit schedule");
                if (scheduleMap.get("valid")) {
                    userConfig.setFitDay(scheduleMap.get("input"));
                }
                break;
            case 7:
                System.out.print("\n                                                 Scheduling algorithm (0-1)");
                System.out.print("\n                                                       Input [#]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (hasInteger(input)) {
                        int schedulingAlgorithm = Integer.parseInt(input);
                        if (schedulingAlgorithm >= 0 && schedulingAlgorithm <= 4) {
                            userConfig.setSchedulingAlgorithm(schedulingAlgorithm);
                        } else {
                            // Do nothing or provide feedback to the user regarding invalid input range
                        }
                    } else {
                        // Do nothing or provide feedback to the user regarding invalid input
                    }
                }
                break;
            case 8:
                System.out.print("\n                                                 Minimum hours for a given day");
                System.out.print("\n                                                       Input [#.0]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    try {
                        double minHours = Double.parseDouble(input);
                        userConfig.setMinHours(minHours);
                    } catch (NumberFormatException e) {
                        // Do nothing or provide feedback to the user regarding invalid input
                    }
                }
                break;
            case 9:
                HashMap<String, Boolean> optimizeMap = promptBooleanInput("Optimize day");
                if (optimizeMap.get("valid")) {
                    userConfig.setOptimizeDay(optimizeMap.get("input"));
                }
                break;
            case 10:
                HashMap<String, Boolean> defaultStartMap = promptBooleanInput("Optimize day");
                if (defaultStartMap.get("valid")) {
                    userConfig.setDefaultAtStart(defaultStartMap.get("input"));
                }
                break;
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

    private HashMap<String, Boolean> promptBooleanInput(String prompt)
    {
        System.out.print("\n                                                " + prompt + " (true/false)");
        System.out.print("\n                                                        Input [T/F]: ");

        HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();

        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                hashMap.put("valid", true);
                hashMap.put("input", Boolean.parseBoolean(input));
            } else {
                // Do nothing or provide feedback to the user regarding invalid input
                System.out.println("Error: Invalid input, must be true or false");
                hashMap.put("valid", false);
            }
        }
        return hashMap;
    }
}
