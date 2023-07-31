package agile.planner.scripter;

public class StaticFunction {

    private final String funcName;

    private final String[] args;

    public StaticFunction(String funcName, String[] args) {
        this.funcName = funcName;
        this.args = args;
    }

    public String getFuncName() {
        return funcName;
    }

    public String[] getArgs() {
        return args;
    }
}
