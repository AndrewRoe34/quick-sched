package com.planner.ui;

import com.planner.manager.ScheduleManager;
import com.planner.models.Card;
import com.planner.models.UserConfig;
import com.planner.ui.tables.TableFormatter;
import com.planner.util.Parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class CLI {
    // this will hold the ScheduleManager instance
    private ScheduleManager sm;

    public CLI() {
        sm = ScheduleManager.getScheduleManager();
    }
    /*
    List of supported commands:
    task
    subtask
    event
    card

    jbin
    update
    config
    log

    build
    sched
    report

    clear
    time

    tutorial (this is a step-by-step tutorial that accepts 'next' prev' or 'quit')
        - needs to go over all core commands
        - needs to explain how each command works


    also, need to prompt user if day is halfway over if they'd like to schedule for today or tomorrow (only if config option is not set for start of day)
     */

    public void loop() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            String[] tokens = Parser.tokenize(s);
            if (tokens.length > 0) {
                // determine type of operation to compute
                exeCmd(tokens);
            }
            System.out.print("> ");
        }
    }

    private void exeCmd(String[] tokens) throws IOException {
        switch (tokens[0]) {
            case "clear":
                if (tokens.length == 1) {
                    TUIState.clearScreen();
                } else {
                    System.out.println("Error: 'clear' has no args.");
                }
                break;
            case "task":
                if (tokens.length > 1) {
                    // TODO
                } else {
                    System.out.println(sm.buildTaskStr());;;;
                }
                break;
            case "subtask":
                if (tokens.length == 1) {
                    System.out.println(sm.buildSubTaskStr());;;;
                } else {
                    System.out.println("Error: 'subtask' has no args.");
                }
                break;
            case "event":
                if (tokens.length > 1) {
                    // TODO
                } else {
                    System.out.println(sm.buildEventStr());
                }
                break;
            case "card":
                if (tokens.length > 1) {
                    // need to add card
                    Parser.CardInfo ci = Parser.parseCard(tokens);
                    Card c = sm.addCard(ci.getName(), ci.getColor());
                    System.out.println("Added Card " + c.getId() + ".");
                } else {
                    System.out.println(sm.buildCardStr());
                }
                break;
            case "jbin":
                if (tokens.length == 2) {
                    if (tokens[1].contains(".jbin"))
                    {
                        sm.importJBinFile(tokens[1]);
                        System.out.println("Imported " + tokens[1] + ".");
                    } else {
                        System.out.println("Error: Argument must be a jbin file");
                    }
                } else if (tokens.length == 1) {
                    System.out.println(sm.buildBoardString());
                }
                break;
            case "update":
                if (tokens.length == 1) {
                    // TODO
                } else {
                    System.out.println("Error: 'update' has no args.");
                }
                break;
            case "config":
                if (tokens.length == 1) {
                    UserConfig userConfig = sm.getUserConfig();
                    System.out.println(
                            "Configuration Details:\n" +
                                    "    - Config File: profile.json\n" +
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
                } else {
                    System.out.println("Error: 'config' has no args.");
                }
                break;
            case "log":
                if (tokens.length == 1) {
                    System.out.println(sm.getEventLog().toString());
                } else {
                    System.out.println("Error: 'log' has no args.");
                }
                break;
            case "build":
                if (tokens.length == 1) {
                    sm.buildSchedule();
                    System.out.println("Schedule built...");
                } else {
                    System.out.println("Error: 'build' has no args.");
                }
                break;
            case "sched":
                if (sm.scheduleIsEmpty()) {
                    System.out.println("Schedule is empty...");
                } else {
                    System.out.println(sm.buildScheduleStr());
                }
                break;
            case "report":
                if (tokens.length == 1) {
                    System.out.println(sm.buildReportStr());
                } else {
                    System.out.println("Error: 'report' has no args.");
                }
                break;
            case "google":
                if (tokens.length == 1) {
                    sm.exportScheduleToGoogle();
                    System.out.println("Exported schedule to Google Calendar.");
                } else {
                    System.out.println("Error: 'google' has no args.");
                }
                break;
            default:
                System.out.println("Error: Unknown command entered.");
                break;
        }
    }
}
