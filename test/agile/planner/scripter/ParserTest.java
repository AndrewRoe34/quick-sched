package agile.planner.scripter;

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
        assertEquals(Parser.Operation.PRE_PROCESSOR, parser.typeOfOperation(pp));
        String str = "\"Test\"";
        assertEquals(Parser.Operation.CONSTANT, parser.typeOfOperation(str));
        String num = " 34 ";
        assertEquals(Parser.Operation.CONSTANT, parser.typeOfOperation(num));
        String attr = "c1.foo()";
        assertEquals(Parser.Operation.ATTRIBUTE, parser.typeOfOperation(attr));
        String fals = "false";
        assertEquals(Parser.Operation.CONSTANT, parser.typeOfOperation(fals));
        String tru = "true";
        assertEquals(Parser.Operation.CONSTANT, parser.typeOfOperation(tru));
        String inst = "c1: f";
        assertEquals(Parser.Operation.INSTANCE, parser.typeOfOperation(inst));
        String func = "func foo()";
        assertEquals(Parser.Operation.SETUP_CUST_FUNC, parser.typeOfOperation(func));
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
        String method1 = "c1.get_attr(t1.get_attr(x, y, z), 3, true)";
        Attributes attr = parser.parseAttributes(method1);
        int x = 3;

    }

    @Test
    public void parseClassInstance() {
        String inst1 = "t1: task(\"HW\", 3, 2)";
        ClassInstance ci1 = parser.parseClassInstance(inst1);
        int x = 3;
    }

    @Test
    public void parseConstant() {
        String num = " 34";
        assertEquals(34, (int) parser.parseConstant(num).getIntConstant());
        String tru = "true";
        assertTrue(parser.parseConstant(tru).getBoolConstant());
        String fal = "false";
        assertFalse(parser.parseConstant(fal).getBoolConstant());
        String str = "\"hello\"";
        assertEquals("\"hello\"", parser.parseConstant(str).getStringConstant());
    }

    @Test
    public void determineAttrFunc() {

    }
}