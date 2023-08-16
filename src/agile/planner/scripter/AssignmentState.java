package agile.planner.scripter;

public class AssignmentState implements Statement {

    private final String varName;

    private final String[] arguments;

    public AssignmentState(String varName, String[] arguments) {
        this.varName = varName;
        this.arguments = arguments;
    }

    public String getVarName() {
        return varName;
    }

    public String[] getArguments() {
        return arguments;
    }
}
