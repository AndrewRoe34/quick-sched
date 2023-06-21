package agile.planner.scripter;

import agile.planner.scripter.exception.InvalidFunctionException;
import agile.planner.scripter.exception.InvalidPreProcessorException;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class {@code ScriptContext} serves as a manager of the {@link State} through its focus on
 * context switches, preprocessing setup, and building custom functions for later use. {@code Simple}
 * utilizes a preprocessor inspired by that of the likes of C with its macros and directives at the top
 * before code execution. {@code ScriptContext} links all the necessary files, scripts, custom functions,
 * and preprocess flags in order to provide a seamless experience from the user's point of view.
 * <p>
 * At the beginning of every {@code Simple} program, the user provides a preprocessor code block that includes
 * the names of all the tools being utilized before code execution. A valid block has a {@code Start:}, an {@code End:},
 * and a type of configuration flag listed (either {@code __DEF_CONFIG__} or {@code __CUR_CONFIG__}) as can be seen here:
 * <blockquote><pre>
 *     START:
 *       __DEF_CONFIG__
 *       __LOG__
 *       __DEBUG__
 *     END:
 * </pre></blockquote><p>
 * If either the start/end tags are missing, a duplicate flag occurs, or code statements are included inside the preprocessor code
 * block, an {@link InvalidPreProcessorException} will being thrown.
 * <p>
 * After the preprocessor tools have been initialized, the {@code ScriptContext} class proceeds by passing each line of
 * code to the {@link State} class to perform its next context switch for possible code processing.
 * <p>
 * When an unknown keyword appears with the signature ':' operator at the end of the first token, an attempt is made to
 * process it as a custom function. In this scripting language, custom functions are approached in a dynamic nature, resulting
 * in it having to be preprocessed first and then being able to execute it. If a violation to this occurs, an {@link InvalidFunctionException}
 * exception will be thrown. Furthermore, all code statements within the function block are required to have at least 1 indentation
 * space to signify being part of the function (including comments). The following demonstrates a valid approach to writing a custom function:
 * <blockquote><pre>
 *     foo: task, card
 *       add: _task, _card
 *
 *     foo: _task, _card
 * </pre></blockquote><p>
 * In the situation where a custom function possesses an invalid name (e.g. special characters), an {@link InvalidFunctionException} will be thrown.
 * Below contains some good examples of the style for custom functions in Simple:
 * <blockquote><pre>
 *     task_setup:
 *     fix:
 *     update_classes:
 * </pre></blockquote>
 * <p>
 * At the moment, the {@code ScriptContext} class lacks the ability to link separate scripting files when processing the user's program. This is feature is
 * planned on being integrated in the near future once Simple possesses its full capabilities on its own.
 *
 * @author Andrew Roe
 * @since 1.0
 */
public class ScriptContext {
    /** Beginning State for scripting processor */
    private State currState = new PreProcessorState();

    /**
     * Updates the State for the scripting processor
     *
     * @param currState State to be adopted
     */
    protected void updateState(State currState) {
        this.currState = currState;
    }

    /**
     * Executes the function from the script
     *
     * @param func function to be processed
     */
    private void executeFunction(String func) {
        currState.determineState(this, func);
        if(!State.comment) {
            currState.processFunc(func);
        }
        State.comment = false;
    }

    /**
     * Sets up the PreProcessor with all its values and configurations
     *
     * @param strScanner Scanner for parsing PreProcessor
     */
    private void preProcessorSetup(Scanner strScanner) {
        String line = null;
        while(strScanner.hasNextLine()) {
            line = strScanner.nextLine().trim();
            if(line.charAt(0) == '#') {
                //do nothing here
            } else if("START:".equals(line)) {
                State.startPP = true;
            } else if(State.startPP && State.configPP && "END:".equals(line)) {
                break;
            } else if(State.startPP && !State.configPP && !State.defConfig && line.equals("__DEF_CONFIG__")) {
                State.configPP = true;
                State.defConfig = true;
                currState.processFunc(line);
            } else if(State.startPP && !State.configPP && !State.currConfig && line.equals("__CURR_CONFIG__")) {
                State.configPP = true;
                State.currConfig = true;
                currState.processFunc(line);
            } else if(State.startPP && !State.logPP && line.equals("__LOG__")) {
                State.logPP = true;
                currState.processFunc(line);
            } else if(State.startPP && !State.debugPP && line.equals("__DEBUG__")) {
                State.debugPP = true;
                currState.processFunc(line);
            } else if(State.startPP && !State.importPP && line.equals("__IMPORT__")) {
                State.importPP = true;
                currState.processFunc(line);
            } else if(State.startPP && !State.exportPP && line.equals("__EXPORT__")) {
                State.exportPP = true;
                currState.processFunc(line);
            } else if(State.startPP && !State.buildPP && line.equals("__BUILD__")) {
                State.buildPP = true;
                currState.processFunc(line);
            } else {
                throw new InvalidPreProcessorException();
            }
        }
    }

    /**
     * Sets up a new custom function with its provided parameters and statements
     *
     * @param strScanner Scanner for parsing new function
     * @param funcDefinition function's definition (including name and parameters)
     */
    private void preFunctionSetup(Scanner strScanner, String funcDefinition) {
        StringBuilder sb = new StringBuilder();
        Scanner funcScanner = new Scanner(funcDefinition);
        String funcName = funcScanner.next();

        String regex = "^(?!.*:.*[^:])\\w{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(funcName);
        if(!matcher.matches()) {
            //throw new InvalidFunctionException();
        }

        if(funcScanner.hasNextLine()) {
            String line = funcScanner.nextLine();
            String[] args = currState.processTokens(line, 5, ",");
            boolean taskFlag = false;
            boolean clFlag = false;
            boolean cardFlag = false;
            boolean boardFlag = false;
            boolean labelFlag = false;
            for(String s : args) {
                if("task".equals(s) && !taskFlag) {
                    taskFlag = true;
                } else if("checklist".equals(s) && !clFlag) {
                    clFlag = true;
                } else if("card".equals(s) && !cardFlag) {
                    cardFlag = true;
                } else if("board".equals(s) && !boardFlag) {
                    boardFlag = true;
                } else if("label".equals(s) && !labelFlag) {
                    labelFlag = true;
                } else if(s != null) {
                    throw new InvalidFunctionException();
                }
            }
            sb.append(line).append('\n');
        }


        String line = null;
        boolean flag = false;
        while(strScanner.hasNextLine()) {
            line = strScanner.nextLine();
            if(line.startsWith(" ")) {
                sb.append(line).append('\n');
            } else if("".equals(line)) {
                //do nothing
            } else  {
                flag = true;
                break;
            }
        }
        State.funcMap.put(funcName, sb.toString());
        if(flag) {
            executeFunction(line);
        }
    }

    /**
     * Executes the script
     *
     * @param script script input to be processed
     */
    public void executeScript(String script) {
        System.out.println("Simple Script V0.1.0...");
        Scanner scriptScanner = new Scanner(script);

        preProcessorSetup(scriptScanner);

        while(scriptScanner.hasNextLine()) {
            String statement = scriptScanner.nextLine();
            if(currState.isNewValidFunction(statement)) {
                preFunctionSetup(scriptScanner, statement);
            } else {
                executeFunction(statement);
            }
        }
    }
}
