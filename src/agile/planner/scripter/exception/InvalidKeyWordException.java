package agile.planner.scripter.exception;

/**
 * {@code InvalidKeyWordException} is a subclass of {@link RuntimeException}, upon which it is thrown if
 * the user's custom function utilizes another custom function's signature (note: this does not include
 * the function's arguments, only the function name).
 * <p>
 * A typical example would be if the user prescribes different arguments, expecting it to be valid similar
 * to C-family languages:
 * <blockquote><pre>
 *     foo: task, card
 *       add: _task, _card
 *
 *     foo: task, label  <-- This will result in an exception
 *       add: _label, _task
 * </pre></blockquote><p>
 * {@code InvalidKeyWordException} will not, however, prevent a user from defining a currently existing keyword
 * for a custom function such as this:
 * <blockquote><pre>
 *     add: task, card, label  <-- This will result in a <em>different</em> exception
 *       add: _task, _card
 *       add: _label, _task
 * </pre></blockquote><p>
 * A more detailed explanation can be found in {@link InvalidGrammarException}, but to summarise, Simple does not
 * attempt to prevent the creation of new functions with core keywords with {@code InvalidKeyWordException} since
 * the language does not recognize the difference between a prebuilt function and a normal pre-built function call.
 *
 * @author Andrew Roe
 * @since 1.0
 */
public class InvalidKeyWordException extends RuntimeException {
    public InvalidKeyWordException(String s) {
        super(s);
    }

    public InvalidKeyWordException() {
        super("Token matches existing keyword for already existing class or function");
    }
}
