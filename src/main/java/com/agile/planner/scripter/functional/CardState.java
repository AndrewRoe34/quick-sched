package com.agile.planner.scripter.functional;

import com.agile.planner.models.Card;
import com.agile.planner.scripter.exception.InvalidGrammarException;

/**
 * Class --> card: [title:string] <br>
 * Creates a new instance of a Card while utilizing dynamic variable usage
 *
 * @author Andrew Roe
 */
public class CardState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 1, ",");
        try {
            int id = scheduleManager.getLastCardId() + cardList.size();
            String name = tokens[0];
            cardList.add(new Card(name));
//            scriptLog.reportCardCreation(id, name);
            System.out.println("Card added.. [C" + (scheduleManager.getLastCardId() + cardList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[card: <title: string>]");
        }
    }
}
