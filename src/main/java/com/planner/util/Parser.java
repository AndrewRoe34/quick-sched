package com.planner.util;

import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Parser {

    public static String[] tokenize(String line) {
        int start = 0;

        List<String> tokens = new ArrayList<>();
        while (start < line.length()) {
            if (line.charAt(start) == ' ' || line.charAt(start) == '\t') {
                start++;
            } else if (line.charAt(start) == '"') {
                int end = start + 1;
                boolean closed = false;
                while (end < line.length()) {
                    if (line.charAt(end) == '"') {
                        closed = true;
                        break;
                    }
                    end++;
                }
                if (!closed) {
                    throw new IllegalArgumentException("Strings must be closed by quotes");
                } else if (start + 1 == end) {
                    throw new IllegalArgumentException("Strings cannot be empty");
                }
                end++;
                tokens.add(line.substring(start, end));
                start = end;
            } else {
                int end = start + 1;
                while (end < line.length()) {
                    if (line.charAt(end) == ' ' || line.charAt(end) == '\t') {
                        break;
                    }
                    end++;
                }
                tokens.add(line.substring(start, end));
                start = end;
            }
        }
        return tokens.toArray(new String[0]);
    }

    public static void parseTask(String[] args) {

    }

    public static Card parseCard(String[] args, int id) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Invalid operation, cannot create Card");
        }

        Card.Colors colors = null;
        String name = null;
        for (int i = 1; i < args.length; i++) {
            if (args[i].charAt(0) == '"') {
                if (name != null) {
                    throw new IllegalArgumentException("Cannot have multiple names for Card");
                }
                name = args[i];
            } else {
                if (colors != null) {
                    throw new IllegalArgumentException("Cannot have multiple colors for Card");
                }
                colors = parseColor(args[i]);
                if (colors == null) {
                    throw new IllegalArgumentException("Invalid color provided for Card");
                }
            }
        }
        return new Card(id, name, colors);
    }

    public static void parseEvent(String[] args) {

    }

    public static void parseDay(String[] args) {

    }

    public static void parseModTask(String[] args) {

    }

    public static void parseModEvent(String[] args) {

    }

    public static void parseModCard(String[] args) {

    }

    public static void parseDelete(String[] args) {

    }

    private static Card.Colors parseColor(String s) {
        switch (s.toUpperCase()) {
            case "RED":
                return Card.Colors.RED;
            case "ORANGE":
                return Card.Colors.ORANGE;
            case "YELLOW":
                return Card.Colors.YELLOW;
            case "GREEN":
                return Card.Colors.GREEN;
            case "BLUE":
                return Card.Colors.BLUE;
            case "INDIGO":
                return Card.Colors.INDIGO;
            case "VIOLET":
                return Card.Colors.VIOLET;
            case "BLACK":
                return Card.Colors.BLACK;
            case "LIGHT_CORAL":
                return Card.Colors.LIGHT_CORAL;
            case "LIGHT_GREEN":
                return Card.Colors.LIGHT_GREEN;
            case "LIGHT_BLUE":
                return Card.Colors.LIGHT_BLUE;
            default:
                return null;
        }
    }

    private static Calendar parseDate(String s) {
        Calendar curr = Time.getFormattedCalendarInstance(0);
        int day =  curr.get(Calendar.DAY_OF_WEEK) - 1;
        int numDays = 0;
        switch (s.toUpperCase()) {
            case "EOD":
                return curr;
            case "TMRW":
                return Time.getFormattedCalendarInstance(curr, 1);
            case "EOW":
            case "SAT":
            case "SATURDAY":
                numDays = (Calendar.SATURDAY - day + 7) % 7;
                return Time.getFormattedCalendarInstance(curr, numDays);
            case "SUN":
            case "SUNDAY":
                numDays = (Calendar.SUNDAY - day + 7) % 7;
                return Time.getFormattedCalendarInstance(curr, numDays);
            case "MON":
            case "MONDAY":
                numDays = (Calendar.MONDAY - day + 7) % 7;
                return Time.getFormattedCalendarInstance(curr, numDays);
            case "TUE":
            case "TUESDAY":
                numDays = (Calendar.TUESDAY - day + 7) % 7;
                return Time.getFormattedCalendarInstance(curr, numDays);
            case "WED":
            case "WEDNESDAY":
                numDays = (Calendar.WEDNESDAY - day + 7) % 7;
                return Time.getFormattedCalendarInstance(curr, numDays);
            case "THU":
            case "THURSDAY":
                numDays = (Calendar.THURSDAY - day + 7) % 7;
                return Time.getFormattedCalendarInstance(curr, numDays);
            case "FRI":
            case "FRIDAY":
                numDays = (Calendar.FRIDAY - day + 7) % 7;
                return Time.getFormattedCalendarInstance(curr, numDays);
            default:
                // parse specific date (e.g. dd-MM-yyyy OR yyyy-MM-dd)
        }
        return null;
    }

    private static Calendar[] parseTimeStamp(String s) {
        return null;
    }
}
