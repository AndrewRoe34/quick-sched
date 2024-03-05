package com.agile.planner.scripter.exception;

/**
 * {@code InvalidPairingException} is a subclass of {@link RuntimeException}, upon which it is thrown if
 * an invalid paring of arguments is provided to a prebuilt mutable function. While all core functions allow any
 * class to be passed as an argument, the specific ordering/passing for these types of functions is in effect.
 * <p>
 * A typical example would be if the user wishes to perform composition but in reverse order as seen here:
 * <blockquote><pre>
 *     task: hw, 3, 0
 *     label: MA, blue
 *     add: _task, _label  <-- This will result in an exception
 * </pre></blockquote><p>
 * This simply does not make sense at all (how can a task be added to a label). Thus, the ordering must be in line
 * with how Simple defines associations.
 * <p>
 * In cases where a function is not mutable, {@code InvalidPairingException} will not be thrown as no modification
 * has been made to any attributes of the class. A good example is the print operation:
 * <blockquote><pre>
 *     task: hw, 3, 0
 *     print: _task  <-- Completely valid
 * </pre></blockquote><p>
 * However, if a user does attempt to pass a function as an argument to one of said mutable functions, this will result
 * in {@code InvalidPairingException} being thrown as well as exhibited here:
 * <blockquote><pre>
 *     task: hw, 3, 0
 *     add: _task, print  <-- This will result in an exception
 * </pre></blockquote>
 * <p>
 *
 * The {@code InvalidPairingException} class helps prevent any occasions of invalid associations or illegal input such as
 * passing functions (which is never an occurrence within Simple). This not only helps ensure the structural integrity of
 * the grammar, but it also provides improved feedback for the user.
 *
 * @author Andrew Roe
 * @since 1.0
 */
public class InvalidPairingException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidPairingException} with a detailed message
     * @param s message for Exception
     */
    public InvalidPairingException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code InvalidPairingException} with {@code null} as its detailed message
     */
    public InvalidPairingException() {
        super("Invalid pairing of classes provided for function call");
    }
}
