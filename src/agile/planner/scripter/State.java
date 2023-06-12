package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.scripter.exception.UnknownClassException;
import agile.planner.manager.ScheduleManager;
import agile.planner.schedule.day.Day;
import agile.planner.scripter.tools.ScriptLog;
import agile.planner.user.UserConfig;
import agile.planner.util.CheckList;

import java.util.*;

/**
 * Abstract State for holding all core data (e.g. stack, heap, etc.) for the scripting language
 *
 * @author Andrew Roe
 */
public abstract class State {

    /** Instance of ScheduleManager (separate from the main scheduler) */
    protected static ScheduleManager scheduleManager = ScheduleManager.getScheduleManager();
    /** UserConfig copy for local scheduling */
    protected static UserConfig userConfig = null;
    /** Scripts matched by keyword function */
    protected static Map<String, String> scripts;
    /** List of all Task variables */
    protected static List<Task> taskList = new ArrayList<>();
    /** List of all Day variables */
    protected static List<Day> dayList = new ArrayList<>();
    /** List of all Card variables */
    protected static List<Card> cardList = new ArrayList<>();
    /** List of all Checklist variables */
    protected static List<CheckList> clList = new ArrayList<>();
    /** List of all Label variables */
    protected static List<Label> labelList = new ArrayList<>();
    /** Set of all keywords within the system */
    protected static Set<String> keyWords = new HashSet<>(Set.of("add:", "board:", "card:", "checklist:",
            "day:", "edit:", "label:", "print:", "remove:", "task:"));
    /** Map for func name to function statements/arguments */
    protected static Map<String, String> funcMap = new HashMap<>();
    /** Boolean value for whether line is a comment */
    protected static boolean comment = false;
    /** Boolean value for whether preprocessor has been started (REQUIRED) */
    protected static boolean startPP = false;
    /** Boolean value for whether config preprocessor has been initialized (REQUIRED) */
    protected static boolean configPP = false;
    /** Boolean value for whether current config settings preprocessor has been initialized (REQUIRED) */
    protected static boolean currConfig = false;
    /** Boolean value for whether default config settings preprocessor has been initialized (REQUIRED) */
    protected static boolean defConfig = false;
    /** Boolean value for whether log preprocessor has been initialized (OPTIONAL) */
    protected static boolean logPP = false;
    /** Boolean value for whether debug preprocessor has been initialized (OPTIONAL) */
    protected static boolean debugPP = false;
    /** Boolean value for whether build preprocessor has been initialized (OPTIONAL) */
    protected static boolean buildPP = false;
    /** Boolean value for whether export preprocessor has been initialized (OPTIONAL) */
    protected static boolean exportPP = false;
    /** Boolean value for whether import preprocessor has been initialized (OPTIONAL) */
    protected static boolean importPP = false;
    /** Holds a log of the script being processed */
    protected static ScriptLog scriptLog = new ScriptLog();

    /**
     * Determines the state for the given line of code
     *
     * @param context provides ScriptContext for State Pattern
     * @param line line of code being processed
     * @throws UnknownClassException if class or function is not detected
     */
    protected void determineState(ScriptContext context, String line) throws UnknownClassException {
        Scanner strScanner = new Scanner(line);
        if("".equals(line)) {
            comment = true;
            return;
        }
        String type = strScanner.next();
        if("task:".equals(type)) {
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
        } else if(funcMap.containsKey(type)) {
            context.updateState(new FunctionState());
        } else {
            throw new UnknownClassException();
        }
        //TODO will need to include a check for any custom function calls with our last condition (which will defer to the function state as well)
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
     * @param maxArgs maximum number of arguments possible
     * @param delimiter pattern for separating arguments
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
                throw new InvalidGrammarException();
            }
        }
        return tokens;
    }

    /**
     * Processes all the tokens in a given string by its delimiter
     *
     * @param line string being processed
     * @param numTokens maximum number of tokens possible
     * @param delimiter pattern for separating tokens
     * @return string array of all arguments
     */
    protected String[] processTokens(String line, int numTokens, String delimiter) {
        String[] tokens = new String[numTokens];
        Scanner strScanner = new Scanner(line);
        strScanner.useDelimiter(delimiter);
        int i = 0;
        while(strScanner.hasNext()){
            if(i < numTokens) {
                tokens[i++] = strScanner.next().trim();
            } else {
                throw new InvalidGrammarException();
            }
        }
        return tokens;
    }

    /**
     * Determines whether provided input is a new valid function
     *
     * @param statement string being processed
     * @return boolean value for whether function is both new and valid
     */
    public boolean isNewValidFunction(String statement) {
        Scanner strScanner = new Scanner(statement);
        String funcKey = strScanner.next();
        return funcKey.charAt(funcKey.length() - 1) == ':' && !keyWords.contains(funcKey)
                && !funcMap.containsKey(funcKey);

        //TODO need to update so that if the function tries to reuse the same name, it will throw an InvalidKeyWordException
    }
}
