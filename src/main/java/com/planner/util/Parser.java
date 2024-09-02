package com.planner.util;

import com.planner.models.Card;
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
                    throw new IllegalArgumentException("Error: Strings must be closed by quotes.");
                } else if (start + 1 == end) {
                    throw new IllegalArgumentException("Error: Strings cannot be empty.");
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

    public static CardInfo parseCard(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Error: Invalid number of arguments provided for Card.");
        }

        Card.Color color = null;
        String name = null;
        for (int i = 1; i < args.length; i++) {
            if (args[i].charAt(0) == '"') {
                if (name != null) {
                    throw new IllegalArgumentException("Error: Cannot have multiple names for Card.");
                }
                name = args[i];
            } else {
                if (color != null) {
                    throw new IllegalArgumentException("Error: Cannot have multiple colors for Card.");
                }
                color = parseColor(args[i]);
                if (color == null) {
                    throw new IllegalArgumentException("Error: Invalid color provided for Card.");
                }
            }
        }
        return new CardInfo(name, color);
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

    private static Card.Color parseColor(String s) {
        switch (s.toUpperCase()) {
            case "RED":
                return Card.Color.RED;
            case "ORANGE":
                return Card.Color.ORANGE;
            case "YELLOW":
                return Card.Color.YELLOW;
            case "GREEN":
                return Card.Color.GREEN;
            case "BLUE":
                return Card.Color.BLUE;
            case "INDIGO":
                return Card.Color.INDIGO;
            case "VIOLET":
                return Card.Color.VIOLET;
            case "BLACK":
                return Card.Color.BLACK;
            case "LIGHT_CORAL":
                return Card.Color.LIGHT_CORAL;
            case "LIGHT_GREEN":
                return Card.Color.LIGHT_GREEN;
            case "LIGHT_BLUE":
                return Card.Color.LIGHT_BLUE;
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
                // todo parse specific date (e.g. dd-MM-yyyy OR yyyy-MM-dd) using sdf
        }
        return null;
    }

    private static Calendar[] parseTimeStamp(String s) {
        boolean hour = false;
        boolean colon = false;
        boolean minute = false;
        boolean am = false;
        boolean dash = false;
        int startHr = -1;
        int endHr = -1;
        int startMin = 0;
        int endMin = 0;
        String startFmt = "";
        String endFmt = "";

        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case ':':
                    if (!hour || minute) {
                        throw new IllegalArgumentException("Error: Colon is expected after hour, not minute.");
                    } else if (am) {
                        throw new IllegalArgumentException("Error: Colon can never occur after 'am' or 'pm'.");
                    }
                    else if (colon) {
                        throw new IllegalArgumentException("Error: Colons cannot be duplicated for same hour, minute combination");
                    }
                    colon = true;
                    break;
                case '-':
                    if (!hour || dash) {
                        throw new IllegalArgumentException("Error: Dashes require an hour and cannot be duplicated");
                    }
                    dash = true;
                    hour = false;
                    colon = false;
                    minute = false;
                    am = false;
                    break;
                case 'a':
                case 'A':
                case 'p':
                case 'P':
                    if (!hour || am) {
                        throw new IllegalArgumentException("Error: 'am'/'pm' cannot occur without an hour nor can there be duplicates");
                    }
                    if (i + 1 < s.length() && (s.charAt(i+1) == 'm' || s.charAt(i+1) == 'M')) {
                        String temp = s.substring(i, i + 2);
                        if (!dash) {
                            startFmt = temp;
                        } else {
                            endFmt = temp;
                        }
                    } else {
                        throw new IllegalArgumentException("Error: Provided time format besides valid 'am'/'pm'");
                    }
                    am = true;
                    i++;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (hour && !colon) {
                        throw new IllegalArgumentException("Error: Minutes must be separated by colon from hours");
                    } else if (minute) {
                        throw new IllegalArgumentException("Error: Minutes cannot be duplicated");
                    } else if (am) {
                        throw new IllegalArgumentException("Error: Hours and minutes cannot come after 'am'/'pm' signature");
                    } else if (hour) {
                        minute = true;
                        if (i+1 < s.length() && s.charAt(i + 1) >= '0' && s.charAt(i + 1) <= '9') {
                            int x = Integer.parseInt(s.substring(i, i + 2));
                            if (x > 59) {
                                throw new IllegalArgumentException("Error: Minutes cannot be greater than 59");
                            }

                            if (!dash) {
                                startMin = x;
                            } else {
                                endMin = x;
                            }
                            i++;
                        } else {
                            throw new IllegalArgumentException("Error: Minutes require 2 digits");
                        }
                    } else {
                        hour = true;
                        if (i+1 >= s.length() || s.charAt(i + 1) < '0' || s.charAt(i + 1) > '9') {
                            int x = Integer.parseInt(s.substring(i, i + 1));
                            if (x > 12) {
                                throw new IllegalArgumentException("Error: Hours cannot be greater than 12");
                            }

                            if (!dash) {
                                startHr = x;
                            } else {
                                endHr = x;
                            }
                        } else if (i+1 < s.length() && s.charAt(i + 1) >= '0' && s.charAt(i + 1) <= '9') {
                            int x = Integer.parseInt(s.substring(i, i + 2));
                            if (x > 12) {
                                throw new IllegalArgumentException("Error: Hours cannot be greater than 12");
                            }

                            if (!dash) {
                                startHr = x;
                            } else {
                                endHr = x;
                            }
                            i++;
                        } else {
                            throw new IllegalArgumentException("Error: Minutes require 2 digits");
                        }
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Error: Invalid time format provided");
            }
        }

        if ((startFmt.equals("pm") || startFmt.equals("PM")) && startHr != 12) {
            startHr += 12;
        } else if ((startFmt.equals("pm") || startFmt.equals("PM")) && startHr == 12) {
            startHr = 0;
        }

        Calendar[] calendars = new Calendar[2];

        if (endHr == -1) {
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR, startHr);
            start.set(Calendar.MINUTE, startMin);

            calendars[0] = start;

            return calendars;
        }

        if ((endFmt.equals("pm") || endFmt.equals("PM")) && endHr != 12) {
            endHr += 12;
        } else if ((endFmt.equals("am") || endFmt.equals("AM")) && endHr == 12) {
            endHr = 0;
        } else if (endFmt.isEmpty() && endHr < startHr) {
            endHr += 12;
        }

        if (startHr > endHr || (startHr == endHr && startMin >= endMin)) {
            return calendars;
        }

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR, startHr);
        start.set(Calendar.MINUTE, startMin);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR, endHr);
        end.set(Calendar.MINUTE, endMin);

        calendars[0] = start;
        calendars[1] = end;

        return calendars;
    }

    public static class CardInfo {
        private final String name;
        private final Card.Color color;

        public CardInfo(String name, Card.Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public Card.Color getColor() {
            return color;
        }
    }

    public static class TaskInfo {
        private final int id;
        private final String desc;
        private final Calendar start;
        private final Calendar end;
        private final int priority;

        public TaskInfo(int id, String desc, Calendar start, Calendar end, int priority) {
            this.id = id;
            this.desc = desc;
            this.start = start;
            this.end = end;
            this.priority = priority;
        }

        public int getId() { return id; }

        public String getDesc() { return desc; }

        public Calendar getStart() { return start; }

        public Calendar getEnd() { return end; }

        public int getPriority() { return priority; }
    }

    public static void main(String[] args) {
        parseTimeStamp("9:-2:60pm");
    }
}
