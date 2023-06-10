package agile.planner.scripter.exception;

public class InvalidFunctionException extends RuntimeException {
    public InvalidFunctionException(String s) {
        super(s);
    }

    public InvalidFunctionException() {
        super("Attempts to make calls to other custom functions within custom function");
    }
}
