package agile.planner.scripter;

import java.util.Scanner;

public class ScriptContext {
    private State currState = new PreProcessorState();

    protected void updateState(State currState) {
        this.currState = currState;
    }

    protected void executeFunction(String func) {
        currState.determineState(this, func);
        currState.processFunc(func);
    }

    public void executeScript(String script) {
        Scanner scriptScanner = new Scanner(script);
        while(scriptScanner.hasNextLine()) {
            executeFunction(scriptScanner.nextLine());
        }
    }
}
