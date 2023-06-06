package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.exception.InvalidGrammarException;

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
            cardList.add(new Card(tokens[0]));
            System.out.println("Card added..");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[card: <title: string>]");
        }
    }
}
