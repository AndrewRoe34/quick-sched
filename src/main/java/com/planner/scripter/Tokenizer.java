package com.planner.scripter;

import com.planner.util.TokenizerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle the tokenization of {@code *.smpl} files.
 *
 * @author Abah Olotuche Gabriel
 */
public class Tokenizer {
    private List<Token> tokens;
    private String source;
    private int start;
    private int current;
    private int line;

    /**
     * Generates tokens from the contents of a {@code *.smpl} file.
     *
     * @param source The text to be tokenized.
     * @return A list of {@code Token} objects.
     */
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

        tokens.add(new Token(Token.TokenType.EOF, "", null, line, Token.TokenColor.DEFAULT));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(Token.TokenType.LEFT_PAREN, Token.TokenColor.DEFAULT);
                break;
            case ')':
                addToken(Token.TokenType.RIGHT_PAREN, Token.TokenColor.DEFAULT);
                break;
            case ',':
                addToken(Token.TokenType.COMMA, Token.TokenColor.DEFAULT);
                break;
            case '.':
                addToken(Token.TokenType.DOT, Token.TokenColor.DEFAULT);
                break;
            case ':':
                addToken(Token.TokenType.COLON, Token.TokenColor.DEFAULT);
                break;

            case '\r':
            case '\t':
                break;

            case ' ':
                while (peek() == ' ' && !isAtEnd())
                    advance();

                addToken(Token.TokenType.SPACE, Token.TokenColor.DEFAULT);
                break;
            case '\n':
                line++;

                addToken(Token.TokenType.NEW_LINE, Token.TokenColor.DEFAULT);
                break;

            case '#':
                while (peek() != '\n' && !isAtEnd())
                    advance();

                addToken(Token.TokenType.COMMENT, Token.TokenColor.GREEN);
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
        addToken(Token.TokenType.STRING_LITERAL, value, Token.TokenColor.PINK);
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        int value = Integer.parseInt(source.substring(start, current));
        addToken(Token.TokenType.INTEGER_LITERAL, value, Token.TokenColor.PINK);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        Token.TokenType type = TokenizerUtil.keywords.get(text);

        if (type == null)
            type = Token.TokenType.IDENTIFIER;

        if (type == Token.TokenType.INCLUDE)
            addToken(type, Token.TokenColor.YELLOW);
        else if (type == Token.TokenType.INCLUDE_FLAG)
            addToken(type, Token.TokenColor.GREEN);
        else if (type == Token.TokenType.BUILT_IN_OBJECT)
            addToken(type, Token.TokenColor.LIGHT_GRAY);
        else if (type == Token.TokenType.BUILT_IN_FUNC)
            addToken(type, Token.TokenColor.ORANGE);
        else if (type == Token.TokenType.IF || type == Token.TokenType.ELIF || type == Token.TokenType.ELSE)
            addToken(type, Token.TokenColor.PURPLE);
        else if (type == Token.TokenType.FUNC || type == Token.TokenType.TRUE || type == Token.TokenType.FALSE)
            addToken(type, Token.TokenColor.BLUE);
        else
            addToken(type, Token.TokenColor.LIGHT_BLUE);
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

    private void addToken(Token.TokenType type, Token.TokenColor color) {
        addToken(type, null, color);
    }

    private void addToken(Token.TokenType type, Object literal, Token.TokenColor color) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, color));
    }
}