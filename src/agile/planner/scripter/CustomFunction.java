package agile.planner.scripter;

import java.util.ArrayList;
import java.util.List;

public class CustomFunction extends StaticFunction {

    private List<String> lines;
    public CustomFunction(String funcName, String[] args) {
        super(funcName, args);
        this.lines = new ArrayList<>();
    }

    public List<String> getLines() {
        return lines;
    }


    public boolean addLine(String line) {
        return lines.add(line);
    }
}
