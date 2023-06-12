package agile.planner.scripter.exception;

/**
 * {@code InvalidFunctionException} is a subclass of {@link RuntimeException}, upon which it is thrown
 * if the custom function calls another custom function inside itself. This can occur both iteratively and
 * recursively within the context of the {@code Simple} language.
 * <p>
 * A typical example would be if the user were to make a call to another custom function from the current custom
 * function:
 * <blockquote><pre>
 *     foo: task, card
 *       add: _task, _card
 *
 *     boo: task, card
 *       foo: _task, _card  <-- This will result in an exception
 *
 *     task: hw, 3, 0
 *     card: Math
 *     boo: _task, _card
 * </pre></blockquote><p>
 * Recursive attempts are also disallowed as can be seen here in this example:
 * <blockquote><pre>
 *     foo: task, card
 *       foo: _task, _card  <-- This will result in an exception
 *
 *     task: hw, 3, 0
 *     card: Math
 *     foo: _task, _card
 * </pre></blockquote>
 * <p>
 *
 * The {@code InvalidFunctionException} class does so to prevent potential infinite loops or never-ending recursive calls,
 * which would be a resource hog in all practical terms. This is subject to be changed as the scripting language progresses
 * towards more complex string processing and resource management.
 *
 * @author Andrew Roe
 * @since 1.0
 */
public class InvalidFunctionException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidFunctionException} with a detailed message
     * @param s message for Exception
     */
    public InvalidFunctionException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code InvalidFunctionException} with {@code null} as its detailed message
     */
    public InvalidFunctionException() {
        super("Attempts to make calls to other custom functions within custom function");
    }
}
