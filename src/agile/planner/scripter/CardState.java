package agile.planner.scripter;

import agile.planner.data.Card;

import java.util.InputMismatchException;

public class CardState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 3, ",");
        try {
            cardList.add(new Card(tokens[0]));
            System.out.println("Card added..");
        } catch(Exception e) {
            throw new InputMismatchException("Invalid input. Expected[card: <title: string>]");
        }
    }
}
