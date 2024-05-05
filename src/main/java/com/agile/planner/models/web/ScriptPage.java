package com.agile.planner.models.web;

public class ScriptPage implements Page {
    private final String scriptLog;
    private final String sysLog;
    private final String script;
    private final String scriptName;

    private static final String htmlHead = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>Agile Planner Simple Script</title>\n" +
            "    <style>\n" +
            "        html, body {\n" +
            "            font-family: 'Segoe UI', sans-serif;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "            background-color: #eaeaea;\n" +
            "        }\n" +
            "        h1 {\n" +
            "\t    margin: 0;\n" +
            "\t    margin-top: 0;\n" +
            "            text-align: center;\n" +
            "            background-color: #dcd0ff; /* Light purple background */\n" +
            "            color: #333;\n" +
            "            padding: 10px 0;\n" +
            "            margin-bottom: 20px; /* Added space below the header */\n" +
            "\t    font-size: 2em;\n" +
            "        }\n" +
            "        .container {\n" +
            "            display: flex;\n" +
            "            justify-content: space-around;\n" +
            "            margin-top: 20px;\n" +
            "        }\n" +
            "        .box {\n" +
            "            border: 1px solid #ddd;\n" +
            "            border-radius: 4px;\n" +
            "            padding: 20px;\n" +
            "            margin: 10px;\n" +
            "            background-color: #f2f2f2; /* Light gray background for boxes */\n" +
            "            box-shadow: 0 2px 4px rgba(0,0,0,0.1);\n" +
            "            width: 30%;\n" +
            "            overflow: auto;\n" +
            "            height: 800px; /* Adjust this value as needed */\n" +
            "        }\n" +
            "\t.box:hover {\n" +
            "    \t\tbox-shadow: 0 4px 8px rgba(0,0,0,0.2);\n" +
            "\t}\n" +
            "\n" +
            "        h2 {\n" +
            "\t    font-size: 1.5em;\n" +
            "\t    margin-top: 0;\n" +
            "            border-bottom: 2px solid #5b9bd5;\n" +
            "            color: #5b9bd5;\n" +
            "        }\n" +
            "        code {\n" +
            "    \tfont-family: 'Consolas', 'Courier New', monospace; /* This is a monospace font */\n" +
            "    \tbackground-color: #f3f3f3; /* Light grey background */\n" +
            "    \tborder-left: 3px solid #5b9bd5; /* A blue border on the left */\n" +
            "    \tpadding: 2px 10px;\n" +
            "    \tdisplay: block; /* Makes it a block element */\n" +
            "    \tcolor: #333; /* Darker text for better contrast */\n" +
            "    \tmargin: 5px 0; /* Adds spacing above and below the code block */\n" +
            "    \toverflow-x: auto; /* Allows horizontal scrolling if needed */\n" +
            "\t}\n" +
            "    </style>\n" +
            "</head>";
    private static final String scriptBody = "<body>\n" +
            "    <h1>Simple Script v0.3.10</h1>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"box\">\n" +
            "            <h2>";
    private static final String scriptBodyPostName = "</h2>\n" +
            "            <pre>\n" +
            "                <code>\n";
    private static final String scriptLogBody = "                </code>\n" +
            "            </pre>\n" +
            "        </div>\n" +
            "        <div class=\"box\">\n" +
            "            <h2>Script Log</h2>\n" +
            "            <pre>\n" +
            "                <code>\n";
    private static final String sysLogBody = "                </code>\n" +
            "            </pre>\n" +
            "        </div>\n" +
            "        <div class=\"box\">\n" +
            "            <h2>System Log:</h2>\n" +
            "            <pre>\n" +
            "                <code>\n";
    private static final String closeBody = "                </code>\n" +
            "            </pre>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>\n";

    public ScriptPage(String scriptLog, String sysLog, String script, String scriptName) {
        this.scriptLog = scriptLog;
        this.sysLog = sysLog;
        this.script = script;
        this.scriptName = scriptName;
    }

    @Override
    public String buildPage() {
        StringBuilder sb = new StringBuilder(htmlHead);
        sb.append(scriptBody)
                .append(scriptName)
                .append(scriptBodyPostName)
                .append(script)
                .append(scriptLogBody)
                .append(scriptLog)
                .append(sysLogBody)
                .append(sysLog)
                .append(closeBody);
        return sb.toString();
    }
}

/*
<!DOCTYPE html>
<html>
<head>
    <title>Python Code and Stacktrace Log</title>
    <style>
        .container {
            display: flex;
            justify-content: space-between;
        }
        .box {
            border: 2px solid black;
            padding: 10px;
            margin: 5px;
            background-color: #f8f8f8;
            width: 25%;
            overflow-x: auto;
        }
    </style>
</head>
<body>
    <h1>Python Code and Stacktrace Log:</h1>
    <div class="container">
        <div class="box">
            <h2>Python Code:</h2>
            <pre>
                <code>
include: __CURR_CONFIG__, __LOG__, __IMPORT__, __BUILD__

google_import()

func setup_cl(cl1)
  in: input_word("Add an Item(y/n) ")
  if(in.==("y"))
    in: input_line("Description -> ")
    cl1.add_item(in)
    setup_cl(cl1)

func setup_task(t1)
  in: input_word("Create a Checklist(y/n) ")
  if(in.==("y"))
    in: input_line("Title -> ")
    _cl: checklist(in)
    t1.add(_cl)
    setup_cl(_cl)

func setup_schedule()
  in: input_word("Create a Task(y/n) ")
  if(in.==("y"))
    _name: input_line("Name -> ")
    _hours: input_int("Hours -> ")
    _due: input_int("Due -> ")
    _task: task(_name, _hours, _due)
    setup_task(_task)
    setup_schedule()

option: input_int("1.Dynamic 2.Compact 3.Default -> ")
set_schedule(option)
setup_schedule()

add_all_tasks()
build()

google_export()
                </code>
            </pre>
        </div>
        <div class="box">
            <h2>Script Log</h2>
            <pre>
                <code>
[09-03-2024] Log of all activities from current session:

[19:47:29] PREPROC_ATTR: DEF_CONFIG=false, IMPORT=true, EXPORT=false, LOG=true, BUILD=true, STATS=false
[19:47:29] FUNC_CALLS: NAME=google_import, ARGS=[]
[19:47:29] FUNC_SETUP: NAME= setup_cl, PARAM=[]
[19:47:29] FUNC_SETUP: NAME= setup_task, PARAM=[]
[19:47:29] FUNC_SETUP: NAME= setup_schedule, PARAM=[]
[19:47:32] FUNC_CALLS: NAME=set_schedule, ARGS=[option]
[19:47:33] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[19:47:33] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[19:47:40] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[19:47:40] IF_CONDITION: ARGS=[in.==("y")], RESULT=false
[19:47:40] FUNC_CALLS: NAME=setup_task, ARGS=[_task]
[19:47:43] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[19:47:43] IF_CONDITION: ARGS=[in.==("y")], RESULT=false
[19:47:43] FUNC_CALLS: NAME=setup_schedule, ARGS=[]
[19:47:43] FUNC_CALLS: NAME=setup_schedule, ARGS=[]
[19:47:43] FUNC_CALLS: NAME=add_all_tasks, ARGS=[]
[19:47:43] FUNC_CALLS: NAME=build, ARGS=[]
[19:47:45] FUNC_CALLS: NAME=google_export, ARGS=[]
                </code>
            </pre>
        </div>
        <div class="box">
            <h2>Stacktrace Log:</h2>
            <pre>
                <code>
[09-03-2024] Log of all activities from current session:

[19:47:13] [INFO] CURRENT SESSION HAS BEGUN...
[19:47:13] [INFO] Reading Config: FILE=profile.cfg
[19:47:13] [INFO] USERNAME=null ,EMAIL=null ,WEEK_HOURS=[8, 8, 8, 8, 8, 8, 8] ,MAX_DAYS=14 ,ARCHIVE_DAYS=14 ,PRIORITY=false ,OVERFLOW=true ,FIT_SCHEDULE=false ,SCHEDULE_ALGO=1 ,MIN_HOURS=1
[19:47:20] [INFO] GOOGLE CALENDAR AUTHORIZATION PROCESSED...
[19:47:29] [INFO] SCRIPT_NAME=C:\Users\Student\Desktop\agile-planner-0.3.8\data\scripts\google.smpl , SCRIPT INSTANCE HAS BEGUN...
[19:47:29] [INFO] SCHEDULE IMPORTED FROM GOOGLE CALENDAR...
[19:47:43] [INFO] ADD(TASK):  ID=0, NAME=Study Math, HOURS=10, DUE_DATE=03-13-2024
[19:47:43] [INFO] Scheduling has begun...
[19:47:43] [INFO] DAY_ID=0, CAPACITY=8, HOURS_REMAINING=6, HOURS_FILLED=2, TASK ADDED=0, OVERFLOW=false
[19:47:43] [INFO] DAY_ID=1, CAPACITY=8, HOURS_REMAINING=6, HOURS_FILLED=2, TASK ADDED=0, OVERFLOW=false
[19:47:43] [INFO] DAY_ID=2, CAPACITY=8, HOURS_REMAINING=6, HOURS_FILLED=2, TASK ADDED=0, OVERFLOW=false
[19:47:43] [INFO] DAY_ID=3, CAPACITY=8, HOURS_REMAINING=6, HOURS_FILLED=2, TASK ADDED=0, OVERFLOW=false
[19:47:43] [INFO] DAY_ID=4, CAPACITY=8, HOURS_REMAINING=6, HOURS_FILLED=2, TASK ADDED=0, OVERFLOW=false
[19:47:43] [INFO] Scheduling has finished...
[19:47:43] [INFO] 1 TASKS REMOVED FROM GOOGLE CALENDAR...
[19:47:45] [INFO] SCHEDULE EXPORTED TO GOOGLE CALENDAR...
[19:47:45] [INFO] Display Schedule: DAYS=5, NUM_TASKS=1, STDOUT=true
[19:47:45] [INFO] SCRIPT_NAME=C:\Users\Student\Desktop\agile-planner-0.3.8\data\scripts\google.smpl , SCRIPT INSTANCE HAS ENDED...
                </code>
            </pre>
        </div>
    </div>
</body>
</html>

 */