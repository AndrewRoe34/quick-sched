package com.planner.util;

import com.planner.models.Card;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void tokenize() {
        String s = "     doc task ";
        System.out.println(Arrays.toString(Parser.tokenize(s)));
    }

    @Test
    void parseTask() {
    }

    @Test
    void parseCard() {
        String s = "card \"some stuff\" blue";
        Card c = Parser.parseCard(Parser.tokenize(s), 1);
        System.out.println(c);
    }

    @Test
    void parseEvent() {
    }

    @Test
    void parseDay() {
    }

    @Test
    void parseModTask() {
    }

    @Test
    void parseModEvent() {
    }

    @Test
    void parseModCard() {
    }

    @Test
    void parseDelete() {
    }
}