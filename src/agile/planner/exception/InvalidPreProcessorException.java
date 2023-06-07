package agile.planner.exception;

public class InvalidPreProcessorException extends RuntimeException {
    public InvalidPreProcessorException(String s) {
        super(s);
    }

    public InvalidPreProcessorException() {
        super("Invalid action or property was included in PreProcessor for script");
    }
}
