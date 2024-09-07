package com.planner.ui;

import com.planner.manager.ScheduleManager;
import com.planner.models.Card;
import com.planner.util.Parser;

import java.io.IOException;
import java.util.Scanner;

public class CLI {
    // this will hold the ScheduleManager instance
    private ScheduleManager sm;

    public CLI() {
        sm = new ScheduleManager();
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
            try {
                String[] tokens = Parser.tokenize(s);
                if (tokens.length > 0) {
                    // determine type of operation to compute
                    exeCmd(tokens);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.print("> ");
        }
    }

    private void exeCmd(String[] tokens) throws IOException {
        switch (tokens[0].toLowerCase()) {
            case "clear":
                if (tokens.length == 1) {
                    Screen.clearScreen();
                } else {
                    throw new IllegalArgumentException("'clear' has no args.");
                }
                break;
            case "task":
                if (tokens.length > 1) {
                    Parser.TaskInfo ti = Parser.parseTask(tokens);
                    Card c = null;
                    if (ti.getCardId() != -1) {
                        c = sm.getCardById(ti.getCardId());
                        // todo not sure if we should cancel the task creation since card id could not be found?
                    }
                    sm.addTask(ti.getDesc(), ti.getHours(), ti.getDue(), c);
                } else {
                    System.out.println(sm.buildTaskStr());
                }
                break;
            case "subtask":
                if (tokens.length == 1) {
                    System.out.println(sm.buildSubTaskStr());
                } else {
                    throw new IllegalArgumentException("'subtask' has no args.");
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
                    if (tokens[1].contains(".jbin")) {
                        sm.importJBinFile("data/jbin/" + tokens[1]);
                        System.out.println("Imported " + tokens[1] + ".");
                    } else {
                        throw new IllegalArgumentException("Argument must be a jbin file");
                    }
                } else if (tokens.length == 1) {
//                    System.out.println(sm.buildBoardString());
                }
                break;
            case "update":
                if (tokens.length == 1) {
                    // TODO
                } else {
                    throw new IllegalArgumentException("'update' has no args.");
                }
                break;
            case "config":
                if (tokens.length == 1) {
                    ConfigDialog configDialog = new ConfigDialog();
                    configDialog.setupAndDisplayPage();
                } else {
                    throw new IllegalArgumentException("'config' has no args.");
                }
                break;
            case "log":
                if (tokens.length == 1) {
                    System.out.println(sm.getEventLog().toString());
                } else {
                    throw new IllegalArgumentException("'log' has no args.");
                }
                break;
            case "build":
                if (tokens.length == 1) {
                    // check the number of active tasks
                    if (sm.getNumActiveTasks() > 0) {
                        sm.buildSchedule();
                        System.out.println("Schedule built...");
                    } else {
                        System.out.println("No active Tasks to schedule");
                    }
                } else {
                    throw new IllegalArgumentException("'build' has no args.");
                }
                break;
            case "sched":
                if (tokens.length != 1) {
                    throw new IllegalArgumentException("'sched' has no args.");
                } else if (sm.scheduleIsEmpty()) {
                    System.out.println("Schedule is empty...");
                } else {
                    System.out.println(sm.buildScheduleStr());
                }
                break;
            case "report":
                if (tokens.length == 1) {
                    System.out.println(sm.buildReportStr());
                } else {
                    throw new IllegalArgumentException("'report' has no args.");
                }
                break;
//            case "google":
//                if (tokens.length == 1) {
//                    sm.exportScheduleToGoogle();
//                    System.out.println("Exported schedule to Google Calendar.");
//                } else {
//                    System.out.println("Error: 'google' has no args.");
//                }
//                break;
            case "excel":
            case "ls":
                if (tokens.length == 1) {
                    System.out.println("task\n" +
                            "subtask\n" +
                            "card\n" +
                            "event\n" +
                            "mod\n" +
                            "delete\n" +
                            "sched\n" +
                            "config\n" +
                            "jbin\n" +
                            "update\n" +
                            "log\n" +
                            "report\n" +
                            "google\n" +
                            "excel\n" +
                            "doc\n" +
                            "quit");
                }
                break;
            case "doc":
                if (tokens.length > 2) {
                    throw new IllegalArgumentException("'report' can have only 1 or no args.");
                } else if (tokens.length == 1) {
                    System.out.println("\n" + Doc.getDoc() + "\n");
                } else {
                    switch (tokens[1].toLowerCase()) {
                        case "task":
                            System.out.println("\n" + Doc.getTaskDoc() + "\n");
                            break;
                        case "card":
                            System.out.println("\n" + Doc.getCardDoc() + "\n");
                            break;
                        case "event":
                            System.out.println("\n" + Doc.getEventDoc() + "\n");
                            break;
                        case "subtask":
                            System.out.println("\n" + Doc.getSubtaskDoc() + "\n");
                            break;
                        case "build":
                            System.out.println("\n" + Doc.getBuildDoc() + "\n");
                            break;
                        case "sched":
                            System.out.println("\n" + Doc.getSchedDoc() + "\n");
                            break;
                        case "config":
                            System.out.println("\n" + Doc.getConfigDoc() + "\n");
                            break;
                        case "log":
                            System.out.println("\n" + Doc.getLogDoc() + "\n");
                            break;
                        case "quit":
                            System.out.println("\n" + Doc.getQuitDoc() + "\n");
                            break;
                        case "date":
                            System.out.println("\n" + Doc.getDateDoc() + "\n");
                            break;
                        case "color":
                            System.out.println("\n" + Doc.getColorDoc() + "\n");
                            break;
                        case "ts":
                            System.out.println("\n" + Doc.getTimestampDoc() + "\n");
                            break;
                        case "google":
                            System.out.println("\n" + Doc.getGoogleDoc() + "\n");
                            break;
                        case "excel":
                            System.out.println("\n" + Doc.getExcelDoc() + "\n");
                            break;
                        case "json":
                            System.out.println("\n" + Doc.getJsonDoc() + "\n");
                            break;
                        case "ls":
                            System.out.println("\n" + Doc.getLsDoc() + "\n");
                            break;
                        case "read":
                            System.out.println("\n" + Doc.getReadDoc() + "\n");
                            break;
                        case "save":
                            System.out.println("\n" + Doc.getSaveDoc() + "\n");
                            break;
                        case "mod":
                            System.out.println("\n" + Doc.getModDoc() + "\n");
                            break;
                        case "delete":
                            System.out.println("\n" + Doc.getDeleteDoc() + "\n");
                            break;
                        default:
                            System.out.println("Unknown command. Use 'ls' to list all available commands.");
                            break;
                    }
                }
                break;
            case "tutorial":
            case "quit":
                // todo need to keep track of any changes (and if so, prompt user to update)
                sm.quit();
            default:
                throw new IllegalArgumentException("Unknown command entered.");
        }
    }
}
