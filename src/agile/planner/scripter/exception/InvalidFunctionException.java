package agile.planner.scripter.exception;

/**
 * {@code InvalidFunctionException} is a subclass of {@link RuntimeException}, upon which it is thrown
 * if the custom function calls another custom function inside itself (iterative/recursive) or if there
 * are any errors with the function definition (name and parameters included).
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
 * </pre></blockquote><p>
 * The {@code InvalidFunctionException} class does so to prevent potential infinite loops or never-ending recursive calls,
 * which would be a resource hog in all practical terms. This is subject to be changed as the scripting language progresses
 * towards more complex string processing and resource management.
 * <p>
 * Another aspect involves the user passing invalid function names that either reuse existing keywords or are illegal due
 * to how Simple defines valid function naming. A proper name must possess at least 2 characters and only numbers, letters, or underscore.
 * Here are some examples of valid names:
 * <blockquote><pre>
 *     func_name
 *     testing:
 *     setup2:
 *     1update:
 * </pre></blockquote><p>
 * The final way in which {@code InvalidFunctionException} might be thrown is if the arguments provided do not match with existing ones. At the moment,
 * Simple does not require parameters for the creation of a custom function, but those provided must be valid. An example below showcases a few situations:
 * <blockquote><pre>
 *     func_name:  <-- valid
 *       print: task
 *
 *     func_name: tasks  <-- This will result in an exception
 *       print: tasks
 *
 *     func_name: _task  <-- This will result in an exception
 *       print: _task
 * </pre></blockquote><p>
 * Only the class type can be utilized as a parameter (not a variable instance). This allows users to pass whatever instance or grouping as they see fit.
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
        super("Attempts to call other custom functions, possesses incorrect parameters, offers invalid function name, or arguments do not match");
    }
}
