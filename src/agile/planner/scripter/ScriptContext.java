package agile.planner.scripter;

import java.util.Scanner;

public class ScriptContext {
    private State currState = new PreProcessorState();

    protected void updateState(State currState) {
        this.currState = currState;
    }

    protected void executeFunction(String func) {
        currState.determineState(this, func);
        if(!State.comment) {
            currState.processFunc(func);
        }
        State.comment = false;
    }

    public void executeScript(String script) {
        System.out.println("Simple Script V0.1.0...");
        Scanner scriptScanner = new Scanner(script);
        while(scriptScanner.hasNextLine()) {
            executeFunction(scriptScanner.nextLine());
        }
    }
}
