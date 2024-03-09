package com.agile.planner.models.web;

import com.agile.planner.scripter.Type;

import java.util.List;

public class ScriptPage {
    private String scriptLog;
//    private String sysLog; todo will be used in SchedulePage
    private String script;
    private List<Type> globalStack;
    private String stdoutLog;
    private String html;
    private String css;

    /*
    Will need to have some of the following CSS attributes:
    -Lucida Console size 9
    -Code box with line numbers on side
    -Red highlight for any errors in scriptLog
    -List of global stack variables
     */
}
