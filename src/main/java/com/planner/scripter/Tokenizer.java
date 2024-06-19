package com.planner.scripter;

import com.planner.util.TokenizerUtil;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private List<Token> tokens;
    private String source;
    private int start;
    private int current;
    private int line;

    public List<Token> scanTokens(String source) {
        this.tokens = new ArrayList<>();
        this.source = source;
        this.start = 0;
        this.current = 0;
        this.line = 1;

        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line, TokenColor.DEFAULT));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN, TokenColor.DEFAULT);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN, TokenColor.DEFAULT);
                break;
            case ',':
                addToken(TokenType.COMMA, TokenColor.DEFAULT);
                break;
            case '.':
                addToken(TokenType.DOT, TokenColor.DEFAULT);
                break;
            case ':':
                addToken(TokenType.COLON, TokenColor.DEFAULT);
                break;

            case '\r':
            case '\t':
                break;

            case ' ':
                while (peek() == ' ' && !isAtEnd())
                    advance();

                addToken(TokenType.SPACE, TokenColor.DEFAULT);
                break;
            case '\n':
                line++;

                addToken(TokenType.NEW_LINE, TokenColor.DEFAULT);
                break;

            case '#':
                while (peek() != '\n' && !isAtEnd())
                    advance();

                addToken(TokenType.COMMENT, TokenColor.GREEN);
                break;

            case '"':
                string();
                break;

            default:
                if (isDigit(c))
                    number();
                else if (isAlpha(c))
                    identifier();
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            System.out.println("Unterminated string on line " + line);
            return;
        }

        // The closing ".
        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING_LITERAL, value, TokenColor.PINK);
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        int value = Integer.parseInt(source.substring(start, current));
        addToken(TokenType.INTEGER_LITERAL, value, TokenColor.PINK);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = TokenizerUtil.keywords.get(text);

        if (type == null)
            type = TokenType.IDENTIFIER;

        if (type == TokenType.INCLUDE)
            addToken(type, TokenColor.YELLOW);
        else if (type == TokenType.INCLUDE_FLAG)
            addToken(type, TokenColor.GREEN);
        else if (type == TokenType.BUILT_IN_OBJECT)
            addToken(type, TokenColor.LIGHT_GREEN);
        else if (type == TokenType.BUILT_IN_FUNC)
            addToken(type, TokenColor.ORANGE);
        else if (type == TokenType.IF || type == TokenType.ELIF || type == TokenType.ELSE)
            addToken(type, TokenColor.PURPLE);
        else if (type == TokenType.FUNC || type == TokenType.TRUE || type == TokenType.FALSE)
            addToken(type, TokenColor.BLUE);
        else
            addToken(type, TokenColor.LIGHT_BLUE);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return c == '_' || isAlpha(c) || isDigit(c);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private void addToken(TokenType type, TokenColor color) {
        addToken(type, null, color);
    }

    private void addToken(TokenType type, Object literal, TokenColor color) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, color));
    }
}