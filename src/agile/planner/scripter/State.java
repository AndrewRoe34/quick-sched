package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.exception.UnknownClassException;
import agile.planner.manager.ScheduleManager;
import agile.planner.schedule.day.Day;
import agile.planner.user.UserConfig;
import agile.planner.util.CheckList;

import java.util.*;

/**
 * Abstract State for holding all core data (e.g. stack, heap, etc.) for the scripting language
 *
 * @author Andrew Roe
 */
public abstract class State {

    protected static ScheduleManager scheduleManager = ScheduleManager.getScheduleManager();
    protected static UserConfig userConfig = null;
    protected static Map<String, String> scripts;
    protected static boolean preProcessor = false;
    protected static List<Task> taskList = new ArrayList<>();
    protected static List<Day> dayList = new ArrayList<>();
    protected static List<Card> cardList = new ArrayList<>();
    protected static List<CheckList> clList = new ArrayList<>();
    protected static List<Label> labelList = new ArrayList<>();
    protected static boolean comment = false;
    protected static boolean config = false;
    protected static boolean logPP = false;
    protected static boolean debugPP = false;
    protected static boolean buildPP = false;
    protected static boolean exportPP = false;
    protected static boolean imprtPP = false;

    /**
     * Determines the state for the given line of code
     *
     * @param context provides ScriptContext for State Pattern
     * @param line line of code being processed
     * @throws UnknownClassException if class or function is not detected
     */
    protected void determineState(ScriptContext context, String line) throws UnknownClassException {
        Scanner strScanner = new Scanner(line);
        String type = strScanner.next();
        if("__DEF_CONFIG__".equals(type)) {
            context.updateState(new PreProcessorState());
        } else if("task:".equals(type)) {
            context.updateState(new TaskState());
        } else if("print:".equals(type)) {
            context.updateState(new PrintState());
        } else if("label:".equals(type)) {
            context.updateState(new LabelState());
        } else if("day:".equals(type)) {
            context.updateState(new DayState());
        } else if(type.charAt(0) == '#') {
            comment = true;
        } else if("checklist:".equals(type)) {
            context.updateState(new CheckListState());
        } else if("card:".equals(type)) {
            context.updateState(new CardState());
        } else if("board:".equals(type)) {
            context.updateState(new BoardState());
        } else if("add:".equals(type)) {
            context.updateState(new AddState());
        } else if("edit:".equals(type)) {
            context.updateState(new EditState());
        } else if("remove:".equals(type)) {
            context.updateState(new RemoveState());
        } else {
            throw new UnknownClassException();
        }
    }

    /**
     * Processes the function given via the String input
     *
     * @param line line of code being processed
     */
    protected abstract void processFunc(String line);

    /**
     * Processes arguments to a class or function in the script
     *
     * @param line line of code being processed
     * @param maxArgs maximum number of tokens possible
     * @param delimiter pattern for separating tokens
     * @return string array of all arguments
     */
    protected String[] processArguments(String line, int maxArgs, String delimiter) {
        String[] tokens = new String[maxArgs];
        Scanner strScanner = new Scanner(line);
        strScanner.next();
        strScanner.useDelimiter(delimiter);
        int i = 0;
        while(strScanner.hasNext()){
            if(i < maxArgs) {
                tokens[i++] = strScanner.next().trim();
            } else {
                throw new InputMismatchException("Invalid expression for function");
            }
        }
        return tokens;
    }
}
