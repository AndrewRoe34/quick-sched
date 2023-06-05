package agile.planner.scripter;

public class Test {

    public static void main(String[] args) {
        ScriptContext sc = new ScriptContext();
        String script = "task: test, 3, 2\n" +
                "task: foo, 8, 1\n" +
                "day: 8, 0\n" +
                "print: task\n" +
                "day: 6, 1\n" +
                "print: day";
        sc.executeScript(script);
    }
}