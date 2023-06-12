package agile.planner.scripter.exception;

/**
 * {@code InvalidGrammarException} is a subclass of {@link RuntimeException}, upon which it is thrown if the user's code
 * violates the prescribed syntax for a given class or function. This can occur if the user provides too many or too
 * few arguments for a given class or function operation.
 * <p>
 * A typical example would be if the user mistakenly forgot a comma when initializing a class:
 * <blockquote><pre>
 *     task: hw 3, 0  <-- This will result in an exception
 * </pre></blockquote><p>
 * Another situation might be the user passing arguments to a custom function but in the incorrect order:
 * <blockquote><pre>
 *     foo: task, card, label
 *       add: _task, _card
 *       add: _label, _task
 *
 *     task: hw, 3, 0
 *     card: Math
 *     label: MA, blue
 *     foo: _task, _label, _card  <-- This will result in an exception
 * </pre></blockquote>
 * <p>
 *
 * The {@code InvalidGrammarException} class differs from {@link InvalidKeyWordException}, which is thrown only when
 * a duplicate occurs when defining a custom function. This, however, does not prevent the user from defining a custom
 * function with an already existing keyword (such as 'add'). Instead, it will throw an {@code InvalidGrammarException}
 * for violating the type or number of arguments based on the given keyword.
 *
 * @author Andrew Roe
 * @since 1.0
 */
public class InvalidGrammarException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidGrammarException} with a detailed message
     * @param s message for Exception
     */
    public InvalidGrammarException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code InvalidGrammarException} with {@code null} as its detailed message
     */
    public InvalidGrammarException() {
        super("Invalid grammar utilized for function or class");
    }
}
