package com.planner.ui;

public class Doc {

    public static String getDoc() {
        return """
    Quick Sched is a dynamic scheduling platform that automates the process of creating a comprehensive schedule.

    Command Categories:

    Task Management:
      - task      Create a new Task or display all Task data
      - card      Create a new Card or display all Card data
      - event     Create a new Event or display all Event data
      - mod       Modify a scheduling item
      - delete    Delete a scheduling item

    Scheduling Operations:
      - build     Builds a fresh schedule
      - sched     Display user schedule
      - subtask   Displays all scheduled SubTasks
      - report    Produce a report of all schedule data

    File Management:
      - read      Display all serialization files or read in a file
      - save      Update the stored db with new scheduling data
      - google    Export schedule data to Google Calendar
      - doc       Display documentation for a command
      - ls        Display all available commands

    System and Configuration:
      - config    View or modify user config settings
      - log       Display the system log to console
      - quit      Exit application

    References:
      - date      List of all valid date formats
      - ts        List of all valid timestamp formats
      - color     List of all valid colors

    Use 'doc <topic>' to view detailed information.
    """;
    }

    public static String getTaskDoc() {
        return """
                Creates a new Task to be stored by the system or displays all previously created Task data if no arguments are provided. Tasks are assignments that can be broken up over a series of days until their deadline.

                Usage:
                  task
                  task <name> <hours> [cardId] @ <date>

                Notes:
                  - Use '@' to signal the start of a time expression. It must be on its own, followed by the date.

                Required:
                  - name    Name for the created Task (whitespace only is not permitted, quotes required).
                  - hours   Number of hours for a given Task (decimal is allowed, but only 0.5).
                  - date    Due date so the scheduling platform can assign it appropriate days.

                Optional:
                  - cardId  Id referencing a Card for its tag and color. Must prefix with '+C'.

                Examples:
                  task
                  task "finish hw3" 3.5 @ eow
                  task +C2 @ 01-11-2024 6.0 "prep for exam\"""";
    }

    public static String getSubtaskDoc() {
        return """
                Displays all generated SubTask data created by the 'build' command. SubTasks are subcomponents and reference their parent Tasks for name, hours, due date, and card for relevant info.

                Usage:
                  subtask

                Examples:
                  subtask""";
    }

    public static String getCardDoc() {
        return """
                Creates a new Card to be stored by the system or displays all previously created Card data if no arguments are provided. Cards serve the role of both label and color for Tasks.

                Usage:
                  card
                  card <name> <color>

                Required:
                  - name    Name for created Card (whitespace only is not permitted, quotes required).
                  - color   Color for Card.

                Examples:
                  card
                  card "Supply Chain" GREEN
                  card blue "Business Law\"""";
    }

    public static String getEventDoc() {
        return """
                Creates a new Event to be stored by the system or displays all previously created Event data if no arguments are provided. Events serve the role of blocked timestamps.

                Usage:
                  event
                  event bool <name> [cardId] @ [date] <timestamp>

                Notes:
                  - Use '@' to signal the start of a time expression. It must be on its own, followed by the date/timestamp.
                  - Recurring Events allow multiple dates for 'days of the week'. However, individual Events allow only 1 date.

                Required:
                  - bool        Whether the event is recurring
                  - name        Name for created Event (whitespace only is not permitted, quotes required).
                  - timestamp   Timestamp for the Event that represents the start/end time for the day

                Optional:
                  - date        Days on which the Event is to be assigned
                  - cardId      Id referencing a Card for its tag and color. Must prefix with '+C'.

                Examples:
                  event
                  event false "study" @ 9-4
                  event true @ mon wed fri 11-12:45 +C2 "Class2\"""";
    }

    public static String getBuildDoc() {
        return """
                Builds a fresh schedule that is ready to be displayed, exported, or saved via serialization.

                Usage:
                  build

                Examples:
                  build""";
    }

    public static String getSchedDoc() {
        return """
                Displays a schedule chart of all scheduled SubTasks and Events throughout the upcoming weeks.

                Usage:
                  sched

                Examples:
                  sched""";
    }

    public static String getConfigDoc() {
        return """
                Begins the Config Dialog to modify user settings so as to personalize your schedule.

                Usage:
                  config

                Examples:
                  config""";
    }

    public static String getLogDoc() {
        return """
                Displays the system log of all user and internal events.

                Usage:
                  log

                Examples:
                  log""";
    }

    public static String getClearDoc() {
        return """
                Clears the terminal of all text.

                Usage:
                  clear

                Examples:
                  clear""";
    }

    public static String getQuitDoc() {
        return """
                Exits the application, prompts to save if any changes were made, and stores the system.log.

                Usage:
                  quit

                Examples:
                  quit""";
    }

    public static String getDateDoc() {
        return """
                Here is a list of all valid date formats Agile Planner supports:

                Date Formats:
                 - dd-MM-yyyy
                   Example: 05-09-2024

                 - yyyy-MM-dd
                   Example: 2024-09-05

                Day Abbreviations:
                 - sun (Sunday)
                 - mon (Monday)
                 - tue (Tuesday)
                 - wed (Wednesday)
                 - thu (Thursday)
                 - fri (Friday)
                 - sat (Saturday)

                Special Keywords:
                 - today - Refers to the current date.
                 - tmrw - Refers to the date of the next day (tomorrow).""";
    }

    public static String getColorDoc() {
        return """
                Here is a list of all valid colors Agile Planner supports:

                Colors:
                 - \u001B[38;2;97;97;97mBLACK\u001B[0m
                 - \u001B[38;2;244;81;30mORANGE\u001B[0m
                 - \u001B[38;2;230;124;115mLIGHT_CORAL\u001B[0m
                 - \u001B[38;2;213;0;0mRED\u001B[0m
                 - \u001B[38;2;246;191;38mYELLOW\u001B[0m
                 - \u001B[38;2;51;182;121mLIGHT_GREEN\u001B[0m
                 - \u001B[38;2;11;128;67mGREEN\u001B[0m
                 - \u001B[38;2;3;155;229mLIGHT_BLUE\u001B[0m
                 - \u001B[38;2;63;81;181mBLUE\u001B[0m
                 - \u001B[38;2;142;36;170mINDIGO\u001B[0m
                 - \u001B[38;2;121;134;203mVIOLET\u001B[0m
                """;
    }

    public static String getTimestampDoc() {
        return """
                Here is a list of valid timestamp formats Agile Planner supports:

                Timestamp Formats:
                 - 9-2                 (9:00am-2:00pm)
                 - 9-2:15              (9:00am-2:15pm)
                 - 9:-2:15             (9:00am-2:15pm)
                 - 09:00am-02:15pm     (9:00am-2:15pm)
                 - 09:am-2:15pm        (9:00am-2:15pm)
                 - 9:am-2:15pm         (9:00am-2:15pm)
                 - 9-2:15pm            (9:00am-2:15pm)

                Rules:
                 - A dash (`-`) is required to separate the start and end times.
                 - Minutes must be specified with two digits (e.g., `:15`), or not at all.
                 - Colons (`:`) are only required when minutes are included.
                 - If the end hour is less than the start, the end hour is assumed to be pm.
                 - To mark the start hour as pm, "pm" must be included.
                 - 24-hour time formats are not permitted.
                 - Timestamps cover a single day and must start and end within the same day.

                This allows for "sloppy" timestamp expressions with flexible formatting.""";
    }

    public static String getGoogleDoc() {
        return """
                Exports your generated schedule to Google Calendar.

                Usage:
                  google

                Examples:
                  google""";
    }

    public static String getExcelDoc() {
        return """
                Exports all your schedule data to a .xlsx

                Usage:
                  excel <name>

                Required:
                 - name   Name of the excel file being passed

                Examples:
                  excel my_schedule""";
    }

    public static String getJsonDoc() {
        return """
                Exports all your schedule data to a .json

                Usage:
                  json <name>

                Required:
                 - name   Name of the json file being passed

                Examples:
                  json my_schedule""";
    }

    public static String getLsDoc() {
        return """
                Displays a list of all available commands

                Usage:
                  ls

                Examples:
                  ls""";
    }

    public static String getReadDoc() {
        return "Displays all serialization files from the sched folder or reads in a file to update the system. " +
                "Reading in a new serialization file will result in fresh schedule data.\n\n" +
                "Usage:\n" +
                "  read\n" +
                "  read <name>\n\n" +
                "Required:\n" +
                "  - name   Name of the serialization file being read\n\n" +
                "Examples:\n" +
                "  read my_data";
    }

    public static String getSaveDoc() {
        return "Updates the serialization file with the current schedule data. If no file was read in, the user will be prompted for a filename.\n\n" +
                "Usage:\n" +
                "  save <name>\n\n" +
                "Required:\n" +
                "  - name   Name of the serialization file being saved\n\n" +
                "Examples:\n" +
                "  save my_data";
    }

    public static String getModDoc() {
        return "Modifies an existing task, card, or event by index. The arguments are the same as the ones used for creating a task, card, or event.\n\n" +
                "Usage:\n" +
                "  mod task <id> [args]\n" +
                "  mod card <id> [args]\n" +
                "  mod event <id> [args]\n\n" +
                "Notes:\n" +
                "  - For 'event', the user cannot modify whether it is recurring (no 'true' or 'false' allowed).\n\n" +
                "Examples:\n" +
                "  mod task 1 \"new name\" 5.0 @ 05-09-2024\n" +
                "  mod card 2 \"Business\" BLUE\n" +
                "  mod event 3 @ 9-11 \"Meeting\"";
    }

    public static String getDeleteDoc() {
        return "Deletes one or more tasks, cards, or events by ID. More than one ID can be provided.\n\n" +
                "Usage:\n" +
                "  delete task <id>\n" +
                "  delete card <id>\n" +
                "  delete event <id>\n\n" +
                "Required:\n" +
                "  - id   ID referencing the specified type\n\n" +
                "Examples:\n" +
                "  delete task 1 2 3\n" +
                "  delete card 4 5\n" +
                "  delete event 6";
    }
}
