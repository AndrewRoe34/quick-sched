package agile.planner.scripter.exception;

public class InvalidKeyWordException extends RuntimeException {
    public InvalidKeyWordException(String s) {
        super(s);
    }

    public InvalidKeyWordException() {
        super("Token matches existing keyword for already existing class or function");
    }
}
