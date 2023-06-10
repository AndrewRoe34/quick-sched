package agile.planner.scripter;

import agile.planner.scripter.exception.InvalidPreProcessorException;

import java.util.Scanner;

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
            line = strScanner.nextLine();
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
        String funcName = strScanner.next();
        sb.append(strScanner.nextLine()); //TODO need to check if that just skips to next line
        String line = null;
        boolean flag = false;
        while(strScanner.hasNextLine()) {
            line = strScanner.nextLine();
            if(line.startsWith("  ")) {
                sb.append(line);
            } else {
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
