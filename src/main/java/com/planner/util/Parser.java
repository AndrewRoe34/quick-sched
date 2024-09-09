package com.planner.util;

import com.planner.models.Card;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Parser {

    private static final SimpleDateFormat DAY_MONTH_YEAR = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat YEAR_MONTH_DAY = new SimpleDateFormat("yyyy-MM-dd");

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
                    throw new IllegalArgumentException("Strings must be closed by quotes.");
                } else if (start + 1 == end) {
                    throw new IllegalArgumentException("Strings cannot be empty.");
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

    // [DONE]
    public static TaskInfo parseTask(String[] args) {
        String name = null;
        Calendar due = null;
        Double hours = null;
        Integer cardId = null;

        for (int i = 1; i < args.length; i++) {
            if (args[i].charAt(0) == '"' && name == null) {
                name = args[i].substring(1, args[i].length() - 1);
            } else if (args[i].charAt(0) == '+' && cardId == null && args[i].length() > 2
                    && (args[i].charAt(1) == 'c' || args[i].charAt(1) == 'C')) {
                try {
                    cardId = Integer.parseInt(args[i].substring(2));
                } catch (NumberFormatException e) {
                    throwAddTaskParsingError();
                }
            } else if ("@".equals(args[i]) && due == null && i + 1 < args.length) {
                i++;
                due = parseDate(args[i]);
            } else if (hours == null) {
                try {
                    hours = Double.parseDouble(args[i]);
                } catch (NumberFormatException e) {
                    throwAddTaskParsingError();
                }
            } else {
                throwAddTaskParsingError();
            }
        }

        if (name == null || hours == null || due == null) {
            throwAddTaskParsingError();
        }

        return new TaskInfo(-1, name, due, hours, cardId);
    }

    // [DONE]
    public static CardInfo parseCard(String[] args) {
        Card.Color color = null;
        String name = null;
        for (int i = 1; i < args.length; i++) {
            if (args[i].charAt(0) == '"' && name == null) {
                name = args[i].substring(1, args[i].length() - 1);
            } else if (color == null) {
                color = parseColor(args[i]);
                if (color == null) {
                    throwAddCardParsingError();
                }
            } else {
                throwAddCardParsingError();
            }
        }

        if (name == null || color == null) {
            throwAddCardParsingError();
        }

        return new CardInfo(null, name, color);
    }

    public static EventInfo parseEvent(String[] args) {
        if (args.length < 5) {
            throw new IllegalArgumentException("Invalid number of arguments provided for Event.");
        }
        String name = null;
        int cardId = -1;
        boolean recurring = false;
        List<Calendar> dates = null;
        Calendar[] timestamp = null;

        if ("true".equalsIgnoreCase(args[1])) {
            recurring = true;
        } else if (!"false".equalsIgnoreCase(args[1])) {
            throw new IllegalArgumentException("Expected bool as second arg for Event.");
        }

        for (int i = 2; i < args.length; i++) {
            if (args[i].charAt(0) == '"' && name == null) {
                name = args[i];
            } else if (args[i].charAt(0) == '+' && cardId == -1 && args[i].length() > 2
                    && (args[i].charAt(1) == 'c' || args[i].charAt(1) == 'C')) {
                try {
                    cardId = Integer.parseInt(args[i].substring(2));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid card id provided.");
                }
            } else if ("@".equals(args[i]) && timestamp == null) {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException("No time arguments after '@'.");
                }
                for (i = i + 1; i < args.length; i++) {
                    try {
                        Calendar d = parseDate(args[i]);
                        if (dates == null) {
                            dates = new ArrayList<>();
                        }
                        dates.add(d);
                    } catch (IllegalArgumentException e) {
                        try {
                            Calendar[] ts = parseTimeStamp(args[i]);
                            if (timestamp != null) {
                                throw new IllegalArgumentException("Cannot have duplicate timestamps.");
                            }
                            timestamp = ts;
                        } catch (IllegalArgumentException f) {
                            if ("Cannot have duplicate timestamps.".equals(f.getMessage())) {
                                throw new IllegalArgumentException(f.getMessage());
                            }
                            i = i - 1;
                            break;
                        }
                    }
                }
                if (timestamp == null) {
                    throw new IllegalArgumentException("No timestamp was provided.");
                }
            } else {
                throw new IllegalArgumentException("Invalid argument provided.");
            }
        }

        return new EventInfo(-1, name, recurring, dates, timestamp, cardId);
    }

    public static DayInfo parseDay(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Error: Must at least have a date");
        }

        String dateString = args[0];

        Calendar date = Time.getFormattedCalendarInstance(0);
        Date due = null;
        try {
            due = DAY_MONTH_YEAR.parse(dateString);
        } catch (ParseException e) {
            try {
                due = YEAR_MONTH_DAY.parse(dateString);
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Invalid date format provided.");
            }
        }
        date.setTime(due);

        HashMap<Integer, Time.TimeStamp> taskTimeStampMap = new HashMap<>();

        List<Integer> eventIds = new ArrayList<>();

        for (int i = 1; i < args.length; i++) {
            if (args[i].charAt(0) == 'T') {
                int id = -1;
                try {
                    id = Integer.parseInt(args[i].split("T")[1]);
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Error: T most be followed by task ID number");
                }

                Calendar[] parsedTimestamp = parseTimeStamp(args[i + 1]);

                parsedTimestamp[0].set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                parsedTimestamp[0].set(Calendar.MONTH, date.get(Calendar.MONTH));
                parsedTimestamp[0].set(Calendar.YEAR, date.get(Calendar.YEAR));

                parsedTimestamp[1].set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                parsedTimestamp[1].set(Calendar.MONTH, date.get(Calendar.MONTH));
                parsedTimestamp[1].set(Calendar.YEAR, date.get(Calendar.YEAR));

                Time.TimeStamp timestamp = new Time.TimeStamp(parsedTimestamp[0], parsedTimestamp[1]) ;

                taskTimeStampMap.put(id, timestamp);
                i++;
            } else if (args[i].charAt(0) == 'E') {
                if (i != args.length - 1 && !( args[i + 1].charAt(0) == 'T' || args[i + 1].charAt(0) == 'E' )) {
                    throw new IllegalArgumentException("Error: Events most be followed by tasks or events");
                }

                int id = -1;
                try {
                    id = Integer.parseInt(args[i].split("E")[1]);
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Error: E most be followed by event ID number");
                }

                eventIds.add(id);
            } else {
                throw new IllegalArgumentException("Error: Days can only include tasks and events");
            }
        }

        return new DayInfo(date, taskTimeStampMap, eventIds);
    }

    public static TaskInfo parseModTask(String[] args) {
        if (args.length < 4) {
            throwModTaskParsingError();
        }
        Integer id = null;
        String name = null;
        Calendar due = null;
        Double hours = null;
        Integer cardId = null;

        try {
            id = Integer.parseInt(args[2]);
        } catch (Exception e) {
            throwModTaskParsingError();
        }

        for (int i = 1; i < args.length; i++) {
            if (args[i].charAt(0) == '"' && name == null) {
                name = args[i].substring(1, args[i].length() - 1);
            } else if (args[i].charAt(0) == '+' && cardId == null && args[i].length() > 2
                    && (args[i].charAt(1) == 'c' || args[i].charAt(1) == 'C')) {
                try {
                    cardId = Integer.parseInt(args[i].substring(2));
                } catch (NumberFormatException e) {
                    throwModTaskParsingError();
                }
            } else if ("@".equals(args[i]) && due == null && i + 1 < args.length) {
                i++;
                due = parseDate(args[i]);
            } else if (hours == null) {
                try {
                    hours = Double.parseDouble(args[i]);
                } catch (NumberFormatException e) {
                    throwModTaskParsingError();
                }
            } else {
                throwModTaskParsingError();
            }
        }

        return new TaskInfo(id, name, due, hours, cardId);
    }

    public static void parseModEvent(String[] args) {

    }

    public static CardInfo parseModCard(String[] args) {
        if (args.length < 4) {
            throwModCardParsingError();
        }

        Integer id = null;
        Card.Color color = null;
        String name = null;

        try {
            id = Integer.parseInt(args[2]);
        } catch (Exception e) {
            throwModCardParsingError();
        }

        for (int i = 3; i < args.length; i++) {
            if (args[i].charAt(0) == '"' && name == null) {
                name = args[i].substring(1, args[i].length() - 1);
            } else if (color == null) {
                color = parseColor(args[i]);
                if (color == null) {
                    throwAddCardParsingError();
                }
            } else {
                throwAddCardParsingError();
            }
        }

        return new CardInfo(id, name, color);
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
            case "TODAY":
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
                Date due = null;
                try {
                    due = DAY_MONTH_YEAR.parse(s);
                } catch (ParseException e) {
                    try {
                        due = YEAR_MONTH_DAY.parse(s);
                    } catch (ParseException ex) {
                        throwDateParsingError();
                    }
                }
                curr.setTime(due);
        }
        return curr;
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
                        throwTimestampParsingError();
                    } else if (am) {
                        throwTimestampParsingError();
                    }
                    else if (colon) {
                        throwTimestampParsingError();
                    }
                    colon = true;
                    break;
                case '-':
                    if (!hour || dash) {
                        throwTimestampParsingError();
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
                        throwTimestampParsingError();
                    }
                    if (i + 1 < s.length() && (s.charAt(i+1) == 'm' || s.charAt(i+1) == 'M')) {
                        String temp = s.substring(i, i + 2);
                        if (!dash) {
                            startFmt = temp;
                        } else {
                            endFmt = temp;
                        }
                    } else {
                        throwTimestampParsingError();
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
                        throwTimestampParsingError();
                    } else if (minute) {
                        throwTimestampParsingError();
                    } else if (am) {
                        throwTimestampParsingError();
                    } else if (hour) {
                        minute = true;
                        if (i+1 < s.length() && s.charAt(i + 1) >= '0' && s.charAt(i + 1) <= '9') {
                            int x = Integer.parseInt(s.substring(i, i + 2));
                            if (x > 59) {
                                throwTimestampParsingError();
                            }

                            if (!dash) {
                                startMin = x;
                            } else {
                                endMin = x;
                            }
                            i++;
                        } else {
                            throwTimestampParsingError();
                        }
                    } else {
                        hour = true;
                        if (i+1 >= s.length() || s.charAt(i + 1) < '0' || s.charAt(i + 1) > '9') {
                            int x = Integer.parseInt(s.substring(i, i + 1));
                            if (x > 12) {
                                throwTimestampParsingError();
                            }

                            if (!dash) {
                                startHr = x;
                            } else {
                                endHr = x;
                            }
                        } else if (i+1 < s.length() && s.charAt(i + 1) >= '0' && s.charAt(i + 1) <= '9') {
                            int x = Integer.parseInt(s.substring(i, i + 2));
                            if (x > 12) {
                                throwTimestampParsingError();
                            }

                            if (!dash) {
                                startHr = x;
                            } else {
                                endHr = x;
                            }
                            i++;
                        } else {
                            throwTimestampParsingError();
                        }
                    }
                    break;
                default:
                    throwTimestampParsingError();
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
        private final Integer id;
        private final String name;
        private final Card.Color color;

        public CardInfo(Integer id, String name, Card.Color color) {
            this.id = id;
            this.name = name;
            this.color = color;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Card.Color getColor() {
            return color;
        }
    }

    public static class TaskInfo {
        private final Integer taskId;
        private final String desc;
        private final Calendar due;
        private final Double hours;
        private final Integer cardId;

        public TaskInfo(Integer taskId, String desc, Calendar due, Double hours, Integer cardId) {
            this.taskId = taskId;
            this.desc = desc;
            this.due = due;
            this.hours = hours;
            this.cardId = cardId;
        }

        public Integer getTaskId() { return taskId; }

        public String getDesc() { return desc; }

        public Calendar getDue() { return due; }

        public Double getHours() {
            return hours;
        }

        public Integer getCardId() {
            return cardId;
        }
    }

    public static class EventInfo {
        private final int id;
        private final String name;
        private final boolean recurring;
        private final List<Calendar> dates;
        private final Calendar[] timestamp;
        private final int cardId;

        public EventInfo(int id, String name, boolean recurring, List<Calendar> dates, Calendar[] timestamp, int cardId) {
            this.id = id;
            this.name = name;
            this.recurring = recurring;
            this.dates = dates;
            this.timestamp = timestamp;
            this.cardId = cardId;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isRecurring() {
            return recurring;
        }

        public List<Calendar> getDates() {
            return dates;
        }

        public Calendar[] getTimestamp() {
            return timestamp;
        }

        public int getCardId() {
            return cardId;
        }
    }

    public static class DayInfo {
        private final Calendar date;
        private final HashMap<Integer, Time.TimeStamp> taskTimeStampsMap;
        private final List<Integer> eventIds;

        public DayInfo(Calendar date, HashMap<Integer, Time.TimeStamp> taskTimeStampsMap, List<Integer> eventIds) {
            this.date = date;
            this.taskTimeStampsMap = taskTimeStampsMap;
            this.eventIds = eventIds;
        }

        public Calendar getDate() { return date; }
        public HashMap<Integer, Time.TimeStamp> getTaskTimeStampsMap() { return taskTimeStampsMap; }
        public List<Integer> getEventIds() { return eventIds; }
    }

    private static void throwAddTaskParsingError() {
        throw new IllegalArgumentException("Invalid input. Expected format is:\n" +
                "       task <name> <hours> [cardId] @ <date>");
    }

    private static void throwAddCardParsingError() {
        throw new IllegalArgumentException("Error: Invalid input. Expected format is:\n" +
                "       card <name> <color>");
    }

    private static void throwModCardParsingError() {
        throw new IllegalArgumentException("Invalid input. Expected format is:\n" +
                "       mod card <id> [name] [color]");
    }

    private static void throwModTaskParsingError() {
        throw new IllegalArgumentException("Invalid input. Expected format is:\n" +
                "       mod task <id> [name] [hours] [cardId] @ [date]");
    }

    private static void throwDateParsingError() {
        throw new IllegalArgumentException("Invalid date format. Expected format:\n" +
                "   dd-MM-yyyy (e.g., 05-09-2024)\n" +
                "   yyyy-MM-dd (e.g., 2024-09-05)\n" +
                "   Day abbreviations: sun, mon, tue, wed, thu, fri, sat\n" +
                "   Special keywords: today, tmrw");
    }

    private static void throwTimestampParsingError() {
        throw new IllegalArgumentException("Invalid timestamp format. Expected format:\n" +
                "   HH[:mm]-HH[:mm] (e.g., 9-2, 9:30-2:15)\n" +
                "   Optional: AM/PM (e.g., 9AM-2PM, 9:30AM-2:15PM)\n" +
                "   24-hour formats are not allowed.");
    }

    public static void main(String[] args) {
        String[] arr = {"task", "@", "03-09-2024", "+C0", "4.0", "\"finish ch12\""};
        TaskInfo ti = parseTask(arr);
        System.out.println(ti.getDue().getTime());

        Parser.parseDay(new String[]{"10-09-2004", "T0", "10-12pm", "E0", "T1", "1-2am", "E1"});
        Parser.parseDay(new String[]{"10-09-2004", "T0", "10-12pm", "E0", "E2", "T1", "1-2am", "E1"});

        Parser.parseTimeStamp("12:-4pm");
        Parser.parseDate("tdy");


        // Error
        // Parser.parseDay(new String[]{"10-09-2004", "T0", "T1", "E0", "T2", "1-2am", "E1"});
        // Parser.parseDay(new String[]{"10-09-2004", "T0", "12--2am", "E0", "T2", "1-2am", "E1"});
        // Parser.parseDay(new String[]{"10-09-2004", "T0", "10-12pm", "E0", "10-12pm", "T1", "1-2am", "E1"});
        // Parser.parseDay(new String[]{"T0", "10-12pm", "E0", "10-12pm", "T1", "1-2am", "E1"});
        // Parser.parseDay(new String[]{"10-09-2004", "T0", "10-12pm", "10-11pm", "E0", "T1", "1-2am", "E1"});
    }
}
