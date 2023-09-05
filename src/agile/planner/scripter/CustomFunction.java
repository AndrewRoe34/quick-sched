package agile.planner.scripter;

import java.util.ArrayList;
import java.util.List;

public class CustomFunction extends StaticFunction {

    private List<String> lines;
    public CustomFunction(String funcName, String[] args, List<String> lines) {
        super(funcName, args);
        this.lines = lines;
    }

    public List<String> getLines() {
        return lines;
    }
}
