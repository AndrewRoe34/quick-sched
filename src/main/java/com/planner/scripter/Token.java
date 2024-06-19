package com.planner.scripter;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;
    final TokenColor color;

    Token(TokenType type, String lexeme, Object literal, int line, TokenColor color) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.color = color;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }

    public String getValue() {
        if (lexeme != null)
            return lexeme;

        if (literal != null)
            return literal.toString();

        return "";
    }

    public TokenColor getColor() {
        return this.color;
    }
}
