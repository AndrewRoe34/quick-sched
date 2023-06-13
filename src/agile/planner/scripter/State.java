package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.scripter.exception.UnknownClassException;
import agile.planner.scripter.exception.DereferenceNullException;
import agile.planner.manager.ScheduleManager;
import agile.planner.schedule.day.Day;
import agile.planner.scripter.tools.ScriptLog;
import agile.planner.user.UserConfig;
import agile.planner.util.CheckList;

import java.util.*;

/**
 * The class {@code State} serves as parent class to all functions and classes within the {@code Simple}
 * language. It is responsible for managing the various states, stacks for variable usage, and string processing
 * of provided code snippets. It utilizes {@link ScriptContext} as its preprocess handler when dealing with the
 * construction of custom functions and updating flags for built-in operations such as logging or debug support
 * to the user. {@code State} utilizes a modified version of State Pattern as a means of performing context switches amongst
 * various classes, functions, and preprocessor flags.
 * <p>
 * {@code Simple} utilizes dynamic variable storage with its constant recall from the Stack of the most recently created
 * instance of a class. While users can specify a certain index, it is not necessary as long as they utilize a '_' before the class
 * type. This saves time and reduces error as can be seen here:
 * <blockquote><pre>
 *     task: hw, 3, 0  <-- [T0]
 *     task: rd, 4, 0  <-- [T1]
 *     print: _task    <-- prints [T1]
 *     print: _task 0  <-- prints [T0]
 * </pre></blockquote><p>
 * The {@code State} class also serves as a context handler with regards to managing states and storing all relevant data. The core
 * identifier for a type is the ':'. It follows the very first token and is accompanied by arguments provided if any. Below demonstrates
 * the three kinds of types a user will typically encounter:
 * <blockquote><pre>
 *     __LOG__              <-- preprocessor flag
 *     print: _task         <-- pre-built function
 *     card: Math           <-- class
 *     foo: _task, _card    <-- custom function
 * </pre></blockquote><p>
 * String processing is also provided via {@code State} to offer all child classes the option to call one of its
 * utility methods. This includes parsing arguments for a class/function or parsing tokens from an entire code statement:
 * <blockquote><pre>
 *     processArguments("task: hw, 3, 0") --> ["hw", "3", "0"]
 *     processTokens("print: _task")      --> ["print", "_task"]
 * </pre></blockquote><p>
 * The {@code State} class has all its fields protected and static to ensure ease of access for all children classes to
 * the stack and preprocessor attributes.
 * <p>
 * As for exceptions, there are various circumstances that will result in a {@link RuntimeException} being thrown. The first deals
 * with a case of the user attempting to recall a {@link Task} from an empty stack, resulting in a {@link DereferenceNullException}
 * being thrown. Another situation involving an unknown type will result in an {@link UnknownClassException}. Finally, when the
 * string processing method encounters more tokens/arguments than previously expected, an {@link InvalidGrammarException} will occur.
 *
 * @author Andrew Roe
 * @since 1.0
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
     * Determines the next context switch for the scripting language {@code Simple}
     *
     * @param context provides ScriptContext for State Pattern
     * @param line line of code being processed
     */
    protected void determineState(ScriptContext context, String line) {
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
    }

    /**
     * Processes the given function via the correct {@code State} implementation
     *
     * @param line line of code being processed
     */
    protected abstract void processFunc(String line);

    /**
     * Processes arguments to a given class or function in the code statement
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
     * Processes all the tokens in a given string provided by its delimiter
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
     * Determines whether the given input is a new valid custom function
     *
     * @param statement string being processed
     * @return boolean value for whether function is both new and valid
     */
    public boolean isNewValidFunction(String statement) {
        if("".equals(statement) || statement == null) {
            return false;
        }
        Scanner strScanner = new Scanner(statement);
        String funcKey = strScanner.next();
        return funcKey.charAt(0) != '#' && funcKey.charAt(funcKey.length() - 1) == ':' && !keyWords.contains(funcKey)
                && !funcMap.containsKey(funcKey);
    }
}
