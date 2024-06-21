package com.planner.scripter;

/**
 * Class that represents a token of scanned text.
 *
 * @author Abah Olotuche Gabriel
 */
public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;
    final TokenColor color;

    /**
     * Set of all available types for a Token
     *
     * @author Abah Olotuche Gabriel
     */
    public enum TokenType {
        COMMENT,
        SPACE,
        NEW_LINE,
        IDENTIFIER,
        BUILT_IN_OBJECT,
        BUILT_IN_FUNC,
        VAR,
        IF,
        ELIF,
        ELSE,
        TRUE,
        FALSE,
        INCLUDE,
        INCLUDE_FLAG,
        FUNC,
        STRING_LITERAL,
        INTEGER_LITERAL,
        LEFT_PAREN,
        RIGHT_PAREN,
        COMMA,
        DOT,
        COLON,
        EQUAL_EQUAL,
        EOF
    }

    /**
     * Set of all available colors for a Token
     *
     * @author Abah Olotuche Gabriel
     */
    public enum TokenColor {
        DEFAULT,
        GREEN,
        RED,
        PURPLE,
        LIGHT_GRAY,
        LIGHT_BLUE,
        BLUE,
        YELLOW,
        ORANGE,
        PINK
    }

    Token(TokenType type, String lexeme, Object literal, int line, TokenColor color) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.color = color;
    }

    /**
     * Returns a string representation of a {@code Token}'s contents.
     */
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }

    /**
     * Returns the raw value represented by the {@code Token}
     */
    public String getValue() {
        if (lexeme != null)
            return lexeme;

        if (literal != null)
            return literal.toString();

        return "";
    }

    /**
     * Returns the assigned color for the {@code Token}
     */
    public TokenColor getColor() {
        return this.color;
    }
}
