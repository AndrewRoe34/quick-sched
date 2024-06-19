package com.planner.util;

import com.planner.scripter.TokenType;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to provide utility functions for Tokenizer.
 *
 * @author Abah Olotuche Gabriel
 */
public class TokenizerUtil {
    public static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("include",  TokenType.INCLUDE);
        keywords.put("__CURR_CONFIG__",  TokenType.INCLUDE_FLAG);
        keywords.put("__DEF_CONFIG__",  TokenType.INCLUDE_FLAG);
        keywords.put("__HTML__",  TokenType.INCLUDE_FLAG);
        keywords.put("__LOG__",  TokenType.INCLUDE_FLAG);
        keywords.put("card",  TokenType.BUILT_IN_OBJECT);
        keywords.put("task",  TokenType.BUILT_IN_OBJECT);
        keywords.put("checklist",  TokenType.BUILT_IN_OBJECT);
        keywords.put("label",  TokenType.BUILT_IN_OBJECT);
        keywords.put("input_word",  TokenType.BUILT_IN_FUNC);
        keywords.put("input_int",  TokenType.BUILT_IN_FUNC);
        keywords.put("input_line",  TokenType.BUILT_IN_FUNC);
        keywords.put("input_tasks",  TokenType.BUILT_IN_FUNC);
        keywords.put("input_bool",  TokenType.BUILT_IN_FUNC);
        keywords.put("create_event",  TokenType.BUILT_IN_FUNC);
        keywords.put("import_schedule",  TokenType.BUILT_IN_FUNC);
        keywords.put("export_schedule",  TokenType.BUILT_IN_FUNC);
        keywords.put("import_google",  TokenType.BUILT_IN_FUNC);
        keywords.put("export_google",  TokenType.BUILT_IN_FUNC);
        keywords.put("export_excel",  TokenType.BUILT_IN_FUNC);
        keywords.put("build",  TokenType.BUILT_IN_FUNC);
        keywords.put("pause",  TokenType.BUILT_IN_FUNC);
        keywords.put("display_board",  TokenType.BUILT_IN_FUNC);
        keywords.put("display_schedule",  TokenType.BUILT_IN_FUNC);
        keywords.put("display_subtasks",  TokenType.BUILT_IN_FUNC);
        keywords.put("display_events",  TokenType.BUILT_IN_FUNC);
        keywords.put("display_stack",  TokenType.BUILT_IN_FUNC);
        keywords.put("display_card",  TokenType.BUILT_IN_FUNC);
        keywords.put("add_task_card",  TokenType.BUILT_IN_FUNC);
        keywords.put("print",  TokenType.BUILT_IN_FUNC);
        keywords.put("println", TokenType.BUILT_IN_FUNC);
        keywords.put("write_file", TokenType.BUILT_IN_FUNC);
        keywords.put("set_schedule", TokenType.BUILT_IN_FUNC);
        keywords.put("inject_code", TokenType.BUILT_IN_FUNC);
        keywords.put("get_card", TokenType.BUILT_IN_FUNC);
        keywords.put("avg", TokenType.BUILT_IN_FUNC);
        keywords.put("var",  TokenType.VAR);
        keywords.put("if",  TokenType.IF);
        keywords.put("elif",  TokenType.ELIF);
        keywords.put("else",  TokenType.ELSE);
        keywords.put("true",  TokenType.TRUE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("func",  TokenType.FUNC);
    }
}
