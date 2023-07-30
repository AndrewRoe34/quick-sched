package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.io.IOProcessing;
import agile.planner.util.CheckList;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParserTest {

    private Parser p = new Parser();

    @Test
    public void parsePreProcessor() {
    }

    @Test
    public void parseCallCustomFunc() {
    }

    @Test
    public void parseIf() {
    }

    @Test
    public void parseForEach() {
    }

    @Test
    public void parseAddFunc() {
    }

    @Test
    public void parsePrintFunc() {
        String line = "c1: card(\"Math\")";
        String line2 = "c2: card(\"Chemistry\")";
        p.parseCard(line);
        p.parseCard(line2);
        String line3 = "println(\"c1=\", c1, \", c2=\", c2)";
        p.parsePrintFunc(line3);
        p.parsePrintFunc("println(\"It worked!\")");
    }

    @Test
    public void parseCard() {
        String line = "c1: card(\"Math\")";
        String line2 = "c1: card(\"Chemistry\")";
        Card c1 = p.parseCard(line);
        Card c2 = p.parseCard(line2);
        assertNotNull(c1);
        assertEquals("\"Math\"", c1.getTitle());
        assertNotNull(c2);
        assertEquals("\"Chemistry\"", c2.getTitle());
        Type t1 = p.getVariable(0);
        System.out.println(t1.getDatatype());
    }

    @Test
    public void parseCheckList() {
        String line = "cl: checklist(\"Math\", 3)";
        String line2 = "my_cl: checklist(\"Chemistry\", 4)";
        CheckList cl1 = p.parseCheckList(line);
        CheckList cl2 = p.parseCheckList(line2);
        assertNotNull(cl1);
        assertEquals("\"Math\"", cl1.getName());
        assertNotNull(cl2);
        assertEquals("\"Chemistry\"", cl2.getName());
        Type t1 = p.getVariable(1);
        System.out.println(t1.getVariableName());
        System.out.println(t1);
    }

    @Test
    public void parseLabel() {
    }

    @Test
    public void parseTask() {
    }
}