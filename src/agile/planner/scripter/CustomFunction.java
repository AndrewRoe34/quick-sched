package agile.planner.scripter;

import java.util.ArrayList;
import java.util.List;

public class CustomFunction extends StaticFunction {

    private List<String> lines;
    private int numSpaces;
    public CustomFunction(String funcName, String[] args, int numSpaces) {
        super(funcName, args, false);
        this.lines = new ArrayList<>();
        this.numSpaces = numSpaces;
    }

    public List<String> getLines() {
        return lines;
    }


    public boolean addLine(String line) {
        return lines.add(line);
    }

    public int getNumSpaces() {
        return numSpaces;
    }
}
