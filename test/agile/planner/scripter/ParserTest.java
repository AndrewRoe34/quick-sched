package agile.planner.scripter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ParserTest {

    Parser parser = new Parser();

    @Test
    public void testParsePreProcessor() {
        //Valid test case
        String pp = "include: __CURR_CONFIG__, __DEBUG__, __LOG__, __IMPORT__, __EXPORT__, __BUILD__, __STATS__";
        PreProcessor preProcessor = parser.parsePreProcessor(pp);
        assertNotNull(preProcessor);
        assertFalse(preProcessor.isDefaultConfig());
        assertTrue(preProcessor.isLog());
        assertTrue(preProcessor.isLog());
        assertTrue(preProcessor.isImprt());
        assertTrue(preProcessor.isExprt());
        assertTrue(preProcessor.isBuild());
        assertTrue(preProcessor.isStats());

        //Invalid test cases
        String ppBad1 = "include __CURR_CONFIG__, __DEBUG__, __LOG__, __IMPORT__, __EXPORT__, __BUILD__, __STATS__";
        assertNull(parser.parsePreProcessor(ppBad1));
        String ppBad2 = "include: __CURR_CONFIG__ __DEBUG__";
        assertNull(parser.parsePreProcessor(ppBad2));
        String ppBad3 = "include: __CURR_CONFIG__, _DEBUG__";
        assertNull(parser.parsePreProcessor(ppBad3));

    }

    @Test
    public void typeOfOperation() {
        String pp = "include: __CURR_CONFIG__, __DEBUG__, __LOG__, __IMPORT__, __EXPORT__, __BUILD__, __STATS__";
        Parser.Operation operation = parser.typeOfOperation(pp);
        //todo need to finish
    }

    @Test
    public void parseCustomFunction() {
    }

    @Test
    public void parseStaticFunction() {
        //Valid test cases
        String func1 = "print(\"Hello World\")";
        StaticFunction sf = parser.parseStaticFunction(func1);
        assertEquals("print", sf.getFuncName());
        assertEquals(1, sf.getArgs().length);
        assertEquals("\"Hello World\"", sf.getArgs()[0]);

        String func2 = "build()";
        sf = parser.parseStaticFunction(func2);
        assertEquals("build", sf.getFuncName());
        assertNull(sf.getArgs());

        String func3 = "import()";
        sf = parser.parseStaticFunction(func3);
        assertEquals("import", sf.getFuncName());
        assertNull(sf.getArgs());

        String func4 = "export()";
        sf = parser.parseStaticFunction(func4);
        assertEquals("export", sf.getFuncName());
        assertNull(sf.getArgs());

        //Invalid test cases
        String func1Bad = "print(\"Hello World\"";
        sf = parser.parseStaticFunction(func1Bad);
        assertNull(sf);

        String func2Bad = "build)";
        sf = parser.parseStaticFunction(func2Bad);
        assertNull(sf);

        String func3Bad = "import";
        sf = parser.parseStaticFunction(func3Bad);
        assertNull(sf);
    }

    @Test
    public void parseAttributes() {
        String method1 = "c1.get_attr()";
        Attributes attr = parser.parseAttributes(method1);
        int x = 3;
        //todo NOTE: smpl won't allow calling functions as arguments except for getters
        //todo example: print(c1.get_name())
        //todo getters are not allowed to have function calls as their arguments
        //todo restrict to attribute method calls as function arguments
        //todo in short, static functions can have method calls as arguments (but those method calls cannot have anything else but simple types)
    }

    @Test
    public void parseClassInstance() {
        String inst1 = "t1: task(\"HW\", 3, 2)";
        ClassInstance ci1 = parser.parseClassInstance(inst1);
        int x = 3;
    }
}