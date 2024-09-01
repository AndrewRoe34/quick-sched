package com.planner.ui;

import com.planner.manager.ScheduleManager;
import com.planner.models.Card;
import com.planner.ui.tables.TableFormatter;
import com.planner.util.Parser;

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

    public void loop() {
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

    private void exeCmd(String[] tokens) {
        switch (tokens[0]) {
            case "clear":
                TUIState.clearScreen();
                break;
            case "task":
                break;
            case "subtask":
                break;
            case "event":
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
                break;
            case "update":
                break;
            case "config":
                break;
            case "log":
                System.out.println(sm.getEventLog().toString());
                break;
            case "build":
                sm.buildSchedule();
                System.out.println("Schedule built...");
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
                break;
            default:
                System.out.println("Error: Unknown command entered.");
                break;
        }
    }
}
