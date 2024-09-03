package com.planner.ui;

import com.planner.models.UserConfig;
import com.planner.ui.tables.TableFormatter;
import com.planner.util.JsonHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ConfigState implements Screen {

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
                    if (id < 0 || id > 11) break;
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
                System.out.print("\n                                                 Enable priority for tasks (true/false)");
                System.out.print("\n                                                        Input [T/F]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                        boolean priority = Boolean.parseBoolean(input);
                        userConfig.setPriority(priority);
                    } else {
                        // Do nothing or provide feedback to the user regarding invalid input
                    }
                }
                break;
            case 5:
                System.out.print("\n                                                 Display overflow (true/false)");
                System.out.print("\n                                                        Input [T/F]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                        boolean overflow = Boolean.parseBoolean(input);
                        userConfig.setOverflow(overflow);
                    } else {
                        // Do nothing or provide feedback to the user regarding invalid input
                    }
                }
                break;
            case 6:
                System.out.print("\n                                                 Fit schedule (true/false)");
                System.out.print("\n                                                        Input [T/F]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                        boolean fitDay = Boolean.parseBoolean(input);
                        userConfig.setFitDay(fitDay);
                    } else {
                        // Do nothing or provide feedback to the user regarding invalid input
                    }
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
                System.out.print("\n                                                 Optimize day (true/false)");
                System.out.print("\n                                                       Input [T/F]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                        boolean optimizeDay = Boolean.parseBoolean(input);
                        userConfig.setOptimizeDay(optimizeDay);
                    } else {
                        // Do nothing or provide feedback to the user regarding invalid input
                    }
                }
                break;
            case 10:
                System.out.print("\n                                                 Default at start (true/false)");
                System.out.print("\n                                                       Input [T/F]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                        boolean defaultAtStart = Boolean.parseBoolean(input);
                        userConfig.setDefaultAtStart(defaultAtStart);
                    } else {
                        // Do nothing or provide feedback to the user regarding invalid input
                    }
                }
                break;
            case 11:
                System.out.print("\n                                                 Local schedule colors (true/false)");
                System.out.print("\n                                                       Input [T/F]: ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                        boolean localScheduleColors = Boolean.parseBoolean(input);
                        userConfig.setLocalScheduleColors(localScheduleColors);
                    } else {
                        // Do nothing or provide feedback to the user regarding invalid input
                    }
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

}
