package com.planner.models.web;

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
            "    <h1>Simple Script v0.4.0</h1>\n" +
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
