package agile.planner.scripter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ParserTest {

    Parser parser = new Parser();

    @Before
    public void setUp() {
    }

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
    public void parseMethod() {
    }

    @Test
    public void typeOfOperation() {
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
    }

    @Test
    public void parseClassInstance() {
    }
}