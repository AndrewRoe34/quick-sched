package agile.planner.scripter;

import agile.planner.scripter.exception.InvalidFunctionException;
import agile.planner.scripter.exception.InvalidPreProcessorException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static agile.planner.scripter.State.scriptLog;

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

    private final Set<String> funcSet = new HashSet<>();

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
            switch (line.charAt(0)) {
                case '#':
                    break;
                case 'S':
                    if ("START:".equals(line) && !State.startPP) {
                        State.startPP = true;
                    } else {
                        throw new InvalidPreProcessorException();
                    }
                    break;
                case 'E':
                    if (State.startPP && State.configPP && "END:".equals(line)) {
                        return;
                    } else {
                        throw new InvalidPreProcessorException();
                    }
                case '_':
                    if (State.startPP) {
                        switch (line) {
                            case "__DEF_CONFIG__":
                                if(!State.configPP && !State.defConfig) {
                                    State.configPP = true;
                                    State.defConfig = true;
                                    currState.processFunc(line);
                                } else {
                                    throw new InvalidPreProcessorException();
                                }
                                break;
                            case "__CURR_CONFIG__":
                                if(!State.configPP && !State.currConfig) {
                                    State.configPP = true;
                                    State.currConfig = true;
                                    currState.processFunc(line);
                                } else {
                                    throw new InvalidPreProcessorException();
                                }
                                break;
                            case "__LOG__":
                                if(!State.logPP) {
                                    State.logPP = true;
                                    currState.processFunc(line);
                                } else {
                                    throw new InvalidPreProcessorException();
                                }
                                break;
                            case "__DEBUG__":
                                if(!State.debugPP) {
                                    State.debugPP = true;
                                    currState.processFunc(line);
                                } else {
                                    throw new InvalidPreProcessorException();
                                }
                                break;
                            case "__IMPORT__":
                                if(!State.importPP) {
                                    State.importPP = true;
                                    currState.processFunc(line);
                                } else {
                                    throw new InvalidPreProcessorException();
                                }
                                break;
                            case "__EXPORT__":
                                if(!State.exportPP) {
                                    State.exportPP = true;
                                    currState.processFunc(line);
                                } else {
                                    throw new InvalidPreProcessorException();
                                }
                                break;
                            case "__BUILD__":
                                if(!State.buildPP) {
                                    State.buildPP = true;
                                    currState.processFunc(line);
                                } else {
                                    throw new InvalidPreProcessorException();
                                }
                                break;
                            default:
                                throw new InvalidPreProcessorException();
                        }
                    } else {
                        throw new InvalidPreProcessorException();
                    }
                    break;
                default:
                    throw new InvalidPreProcessorException();
            }

        }
    }

    /**
     * Sets up a new custom function with its provided parameters and statements
     *
     * @param strScanner Scanner for parsing new function
     * @param funcDefinition function's definition (including name and parameters)
     * @param funcFile boolean flag for whether a function file is being processed
     */
    private void preFunctionSetup(Scanner strScanner, String funcDefinition, boolean funcFile) {
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
        } else {
            sb.append("none\n");
        }

        String line = null;
        boolean flag = false;
        while(strScanner.hasNextLine()) {
            line = strScanner.nextLine();
            if(line.startsWith(" ")) {
                sb.append(line).append('\n');
            } else if(!"".equals(line)){
                flag = true;
                break;
            }
        }

        scriptLog.reportFunctionSetup(funcDefinition);

        State.funcMap.put(funcName, sb.toString());
        if(flag && !funcFile && !currState.isNewValidFunction(line)) {
            executeFunction(line);
        } else if(flag && line.charAt(0) != '#') {
            preFunctionSetup(strScanner, line, false);
        } else {
            return;
        }
    }

    /**
     * Executes the script
     *
     * @param script script input to be processed
     */
    public void executeScript(String script) {
        System.out.println("Simple Script 1.0");
        Scanner scriptScanner = new Scanner(script);
        try {
            preProcessorSetup(scriptScanner);
            scriptLog.reportPreProcessorSetup();

            while(scriptScanner.hasNextLine()) {
                String statement = scriptScanner.nextLine();
                if(currState.isNewValidFunction(statement)) {
                    preFunctionSetup(scriptScanner, statement, false);
                } else if(isNewValidFuncScript(statement)) {
                    preScriptSetup(statement);
                } else {
                    executeFunction(statement);
                }
            }
        } catch(Exception e) {
            scriptLog.reportException(e);
        }
        if(State.buildPP) {
            System.out.println("\nSCHEDULE:");
            State.buildSchedule();
        }
        if(State.logPP) {
            System.out.println(scriptLog.toString());
        }
    }

    private void preScriptSetup(String statement) {
        funcSet.add(statement);
        scriptLog.reportLinkFile(statement);
        try {
            Scanner funcScanner = new Scanner(new File("data/" + statement));
            while(funcScanner.hasNextLine()) {
                String line = funcScanner.nextLine().trim();
                if(currState.isNewValidFunction(line)) {
                    preFunctionSetup(funcScanner, line, true);
                } else if (line.charAt(0) != '#'){
                    throw new InvalidFunctionException();
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNewValidFuncScript(String filename) {
        return !funcSet.contains(filename) && filename.length() > 5
                && ".func".equals(filename.substring(filename.length() - 5))
                && filename.charAt(0) != '#';
    }
}
