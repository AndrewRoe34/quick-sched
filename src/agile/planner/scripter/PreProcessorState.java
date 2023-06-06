package agile.planner.scripter;

public class PreProcessorState extends State {
    protected static boolean defConfig = false;
    protected static boolean logging = false;
    protected static boolean debug = false;
    protected static boolean buildSched = false;
    protected static boolean exportSchd = false;
    protected static boolean importSchd = false;

    @Override
    protected void processFunc(String line) {

    }
}
