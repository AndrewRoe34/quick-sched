package agile.planner.scripter;

import java.util.ArrayList;

public class BaseState {

    protected String processToken(String line) {
        return null;
    }

    protected ArrayList<Integer> processSet(String token) {
        return null;
    }

    public void processFunction(String line) {
        //TODO if("task"), else ...
    }
}
