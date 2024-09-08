package com.planner.ui;

public class Doc {

    public static String getDoc() {
        return "Agile Planner is a dynamic scheduling platform that automates the process of creating a comprehensive schedule.\n\n" +
                "Command Categories:\n\n" +
                "Task Management:\n" +
                "  - task      Create a new Task or display all Task data\n" +
                "  - card      Create a new Card or display all Card data\n" +
                "  - event     Create a new Event or display all Event data\n" +
                "  - mod       Modify a scheduling item\n" +
                "  - delete    Delete a scheduling item\n\n" +
                "Scheduling Operations:\n" +
                "  - build     Builds a fresh schedule\n" +
                "  - sched     Display user schedule\n" +
                "  - subtask   Displays all scheduled SubTasks\n" +
                "  - report    Produce a report of all schedule data\n\n" +
                "File Management:\n" +
                "  - read      Display all serialization files or read in a file\n" +
                "  - save      Update the stored db with new scheduling data\n" +
                "  - google    Export schedule data to Google Calendar\n" +
                "  - excel     Export schedule data to a .xlsx\n" +
                "  - json      Export schedule data to a .json\n" +
                "  - doc       Display documentation for a command\n" +
                "  - ls        Display all available commands\n\n" +
                "System and Configuration:\n" +
                "  - config    View or modify user config settings\n" +
                "  - log       Display the system log to console\n" +
                "  - quit      Exit application\n\n" +
                "References:\n" +
                "  - date      List of all valid date formats\n" +
                "  - ts        List of all valid timestamp formats\n" +
                "  - color     List of all valid colors";
    }

    public static String getTaskDoc() {
        return "Creates a new Task to be stored by the system or displays all previously created Task data if no arguments are provided. Tasks are assignments that can be broken up over a series of days until their deadline.\n\n" +
                "Usage:\n" +
                "  task\n" +
                "  task <name> <hours> [cardId] @ <date>\n\n" +
                "Notes:\n" +
                "  - Use '@' to signal the start of a time expression. It must be on its own, followed by the date.\n\n" +
                "Required:\n" +
                "  - name    Name for the created Task (whitespace only is not permitted, quotes required).\n" +
                "  - hours   Number of hours for a given Task (decimal is allowed, but only 0.5).\n" +
                "  - date    Due date so the scheduling platform can assign it appropriate days.\n\n" +
                "Optional:\n" +
                "  - cardId  Id referencing a Card for its tag and color. Must prefix with '+C'.\n\n" +
                "Examples:\n" +
                "  task\n" +
                "  task \"finish hw3\" 3.5 @ eow\n" +
                "  task +C2 @ 01-11-2024 6.0 \"prep for exam\"";
    }

    public static String getSubtaskDoc() {
        return "Displays all generated SubTask data created by the 'build' command. SubTasks are subcomponents and reference their parent Tasks for name, hours, due date, and card for relevant info.\n\n" +
                "Usage:\n" +
                "  subtask\n\n" +
                "Examples:\n" +
                "  subtask";
    }

    public static String getCardDoc() {
        return "Creates a new Card to be stored by the system or displays all previously created Card data if no arguments are provided. Cards serve the role of both label and color for Tasks.\n\n" +
                "Usage:\n" +
                "  card\n" +
                "  card <name> <color>\n\n" +
                "Required:\n" +
                "  - name    Name for created Card (whitespace only is not permitted, quotes required).\n" +
                "  - color   Color for Card.\n\n" +
                "Examples:\n" +
                "  card\n" +
                "  card \"Supply Chain\" GREEN\n" +
                "  card blue \"Business Law\"";
    }

    public static String getEventDoc() {
        return "Creates a new Event to be stored by the system or displays all previously created Event data if no arguments are provided. Events serve the role of blocked timestamps.\n\n" +
                "Non-recurring Events can have only 1 date. Recurring Events can have up to 7 (which represent the days of the week, no duplicates allowed).\n\n" +
                "Usage:\n" +
                "  event\n" +
                "  event bool <name> [cardId] @ [date] <timestamp>\n\n" +
                "Notes:\n" +
                "  - Use '@' to signal the start of a time expression. It must be on its own, followed by the date/timestamp.\n\n" +
                "Required:\n" +
                "  - bool        Whether the event is recurring\n" +
                "  - name        Name for created Event (whitespace only is not permitted, quotes required).\n" +
                "  - timestamp   Timestamp for the Event that represents the start/end time for the day\n\n" +
                "Optional:\n" +
                "  - date        Days on which the Event is to be assigned\n" +
                "  - cardId      Id referencing a Card for its tag and color. Must prefix with '+C'.\n\n" +
                "Examples:\n" +
                "  event\n" +
                "  event false \"study\" @ 9-4\n" +
                "  event true @ mon wed fri 11-12:45 +C2 \"Class2\"";
    }

    public static String getBuildDoc() {
        return "Builds a fresh schedule that is ready to be displayed, exported, or saved via serialization.\n\n" +
                "Usage:\n" +
                "  build\n\n" +
                "Examples:\n" +
                "  build";
    }

    public static String getSchedDoc() {
        return "Displays a schedule chart of all scheduled SubTasks and Events throughout the upcoming weeks.\n\n" +
                "Usage:\n" +
                "  sched\n\n" +
                "Examples:\n" +
                "  sched";
    }

    public static String getConfigDoc() {
        return "Begins the Config Dialog to modify user settings so as to personalize your schedule.\n\n" +
                "Usage:\n" +
                "  config\n\n" +
                "Examples:\n" +
                "  config";
    }

    public static String getLogDoc() {
        return "Displays the system log of all user and internal events.\n\n" +
                "Usage:\n" +
                "  log\n\n" +
                "Examples:\n" +
                "  log";
    }

    public static String getQuitDoc() {
        return "Exits the application, prompts to save if any changes were made, and stores the system.log.\n\n" +
                "Usage:\n" +
                "  quit\n\n" +
                "Examples:\n" +
                "  quit";
    }

    public static String getDateDoc() {
        return "Here is a list of all valid date formats Agile Planner supports:\n\n" +
                "Date Formats:\n" +
                " - dd-MM-yyyy\n" +
                "   Example: 05-09-2024\n\n" +
                " - yyyy-MM-dd\n" +
                "   Example: 2024-09-05\n\n" +
                "Day Abbreviations:\n" +
                " - sun (Sunday)\n" +
                " - mon (Monday)\n" +
                " - tue (Tuesday)\n" +
                " - wed (Wednesday)\n" +
                " - thu (Thursday)\n" +
                " - fri (Friday)\n" +
                " - sat (Saturday)\n\n" +
                "Special Keywords:\n" +
                " - today - Refers to the current date.\n" +
                " - tmrw - Refers to the date of the next day (tomorrow).";
    }

    public static String getColorDoc() {
        return "Here is a list of all valid colors Agile Planner supports:\n\n" +
                "Colors:\n" +
                " - \u001B[38;2;97;97;97mBLACK\u001B[0m\n" +
                " - \u001B[38;2;244;81;30mORANGE\u001B[0m\n" +
                " - \u001B[38;2;230;124;115mLIGHT_CORAL\u001B[0m\n" +
                " - \u001B[38;2;213;0;0mRED\u001B[0m\n" +
                " - \u001B[38;2;246;191;38mYELLOW\u001B[0m\n" +
                " - \u001B[38;2;51;182;121mLIGHT_GREEN\u001B[0m\n" +
                " - \u001B[38;2;11;128;67mGREEN\u001B[0m\n" +
                " - \u001B[38;2;3;155;229mLIGHT_BLUE\u001B[0m\n" +
                " - \u001B[38;2;63;81;181mBLUE\u001B[0m\n" +
                " - \u001B[38;2;142;36;170mINDIGO\u001B[0m\n" +
                " - \u001B[38;2;121;134;203mVIOLET\u001B[0m\n";
    }

    public static String getTimestampDoc() {
        return "Here is a list of valid timestamp formats Agile Planner supports:\n\n" +
                "Timestamp Formats:\n" +
                " - 9-2                 (9:00am-2:00pm)\n" +
                " - 9-2:15              (9:00am-2:15pm)\n" +
                " - 9:-2:15             (9:00am-2:15pm)\n" +
                " - 09:00am-02:15pm     (9:00am-2:15pm)\n" +
                " - 09:am-2:15pm        (9:00am-2:15pm)\n" +
                " - 9:am-2:15pm         (9:00am-2:15pm)\n" +
                " - 9-2:15pm            (9:00am-2:15pm)\n\n" +
                "Rules:\n" +
                " - A dash (`-`) is required to separate the start and end times.\n" +
                " - Minutes must be specified with two digits (e.g., `:15`), or not at all.\n" +
                " - Colons (`:`) are only required when minutes are included.\n" +
                " - If the end hour is less than the start, the end hour is assumed to be pm.\n" +
                " - To mark the start hour as pm, \"pm\" must be included.\n" +
                " - 24-hour time formats are not permitted.\n" +
                " - Timestamps cover a single day and must start and end within the same day.\n\n" +
                "This allows for \"sloppy\" timestamp expressions with flexible formatting.";
    }

    public static String getGoogleDoc() {
        return "Exports your generated schedule to Google Calendar.\n\n" +
                "Usage:\n" +
                "  google\n\n" +
                "Examples:\n" +
                "  google";
    }

    public static String getExcelDoc() {
        return "Exports all your schedule data to a .xlsx\n\n" +
                "Usage:\n" +
                "  excel <name>\n\n" +
                "Required:\n" +
                " - name   Name of the excel file being passed\n\n" +
                "Examples:\n" +
                "  excel my_schedule";
    }

    public static String getJsonDoc() {
        return "Exports all your schedule data to a .json\n\n" +
                "Usage:\n" +
                "  json <name>\n\n" +
                "Required:\n" +
                " - name   Name of the json file being passed\n\n" +
                "Examples:\n" +
                "  json my_schedule";
    }

    public static String getLsDoc() {
        return "Displays a list of all available commands\n\n" +
                "Usage:\n" +
                "  ls\n\n" +
                "Examples:\n" +
                "  ls";
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
