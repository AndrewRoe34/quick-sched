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

    private void exeCmd(String[] tokens) throws IOException { // todo will need to catch IllegalArgsException and print to console error
        switch (tokens[0]) {
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
                    sm.addTask(ti.getDesc(), ti.getHours(), ti.getDue(), null);
                    // TODO
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
                    ConfigUI configUI = new ConfigUI();
                    configUI.setupAndDisplayPage();
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
                    sm.buildSchedule();
                    System.out.println("Schedule built...");
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
                            "tutorial\n" +
                            "quit");
                }
                break;
            case "doc":
                if (tokens.length == 1) {
                    System.out.println("Agile Planner is a dynamic scheduling platform that automates the process of creating a comprehensive schedule.\n" +
                            "\n" +
                            "Available Commands:\n" +
                            "  task      Create a new Task or display all Task data\n" +
                            "  subtask   Display all scheduled SubTasks\n" +
                            "  card      Create a new Card or display all Card data\n" +
                            "  event     Create a new Event or display all Event data\n" +
                            "  mod       Modify schedule data\n" +
                            "  delete    Delete schedule data\n" +
                            "  sched     Display user schedule\n" +
                            "  config    View or modify user config settings\n" +
                            "  jbin      Display all JBin files or read in data\n" +
                            "  update    Update the stored db with new scheduling data\n" +
                            "  log       Display the system log to console\n" +
                            "  report    Produce report of all schedule data\n" +
                            "  google    Export schedule data to Google Calendar\n" +
                            "  excel     Export schedule data to a .xlsx\n" +
                            "  doc       Display documentation for a command\n" +
                            "  tutorial  Generate a walkthrough to learn Agile Planner\n" +
                            "  quit      Exit application\n" +
                            "\n" +
                            "Doc References:\n" +
                            "  date      Display list of all valid date formats\n" +
                            "  ts        Display list of all valid timestamp formats\n");
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
