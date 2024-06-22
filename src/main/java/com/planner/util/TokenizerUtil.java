package com.planner.util;

import com.planner.scripter.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to provide utility functions for Tokenizer.
 *
 * @author Abah Olotuche Gabriel
 */
public class TokenizerUtil {
    public static final Map<String, Token.TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("include",  Token.TokenType.INCLUDE);
        keywords.put("__CURR_CONFIG__",  Token.TokenType.INCLUDE_FLAG);
        keywords.put("__DEF_CONFIG__",  Token.TokenType.INCLUDE_FLAG);
        keywords.put("__HTML__",  Token.TokenType.INCLUDE_FLAG);
        keywords.put("__LOG__",  Token.TokenType.INCLUDE_FLAG);
        keywords.put("card",  Token.TokenType.BUILT_IN_OBJECT);
        keywords.put("task",  Token.TokenType.BUILT_IN_OBJECT);
        keywords.put("checklist",  Token.TokenType.BUILT_IN_OBJECT);
        keywords.put("label",  Token.TokenType.BUILT_IN_OBJECT);
        keywords.put("input_word",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("input_int",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("input_line",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("input_tasks",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("input_bool",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("create_event",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("import_schedule",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("export_schedule",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("import_google",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("export_google",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("export_excel",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("build",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("pause",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("display_board",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("display_schedule",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("display_subtasks",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("display_events",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("display_stack",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("display_card",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("add_task_card",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("print",  Token.TokenType.BUILT_IN_FUNC);
        keywords.put("println", Token.TokenType.BUILT_IN_FUNC);
        keywords.put("write_file", Token.TokenType.BUILT_IN_FUNC);
        keywords.put("set_schedule", Token.TokenType.BUILT_IN_FUNC);
        keywords.put("inject_code", Token.TokenType.BUILT_IN_FUNC);
        keywords.put("get_card", Token.TokenType.BUILT_IN_FUNC);
        keywords.put("avg", Token.TokenType.BUILT_IN_FUNC);
        keywords.put("var",  Token.TokenType.VAR);
        keywords.put("if",  Token.TokenType.IF);
        keywords.put("elif",  Token.TokenType.ELIF);
        keywords.put("else",  Token.TokenType.ELSE);
        keywords.put("true",  Token.TokenType.TRUE);
        keywords.put("false",  Token.TokenType.FALSE);
        keywords.put("func",  Token.TokenType.FUNC);
    }
}
