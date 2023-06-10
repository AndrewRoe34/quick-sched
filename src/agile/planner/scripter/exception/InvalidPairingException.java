package agile.planner.scripter.exception;

public class InvalidPairingException extends RuntimeException {
    public InvalidPairingException(String s) {
        super(s);
    }

    public InvalidPairingException() {
        super("Invalid pairing of classes provided for function call");
    }
}
