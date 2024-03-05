package com.agile.planner.scripter.exception;

 /**
 * The class {@code DereferenceNullException} is a subclass of {@link RuntimeException}, upon which it is thrown
 * if a {@code null} is dereferenced. This will occur if the user attempts to recall a particular value
 * outside its valid index or if an attempt to utilize the dynamic nature of the language fails if not one value
 * remains in the existing stack.
 * <p>
 * {@code Simple} takes advantage of the stack and allows constant recall of the most recently constructed class
 * instances. However, if nothing currently remains (either due to removal or never being added), an exception
 * will be thrown.
 * <blockquote><pre>
 *     card: Math
 *     add: _task, _card  <-- This will result in an exception
 * </pre></blockquote><p>
 * Another instance that could result in failure would be the user attempting to utilize a value outside the valid
 * index of the stack, thus resulting in this exception being thrown.
 * <blockquote><pre>
 *     task: hw, 3, 0
 *     card: Math
 *     add: _task 2, _card  <-- This will result in an exception
 * </pre></blockquote>
 * <p>
 * The {@code DereferenceNullException} class serves as a local replacement for {@link NullPointerException}, allowing it to
 * better cater to the language's specific needs.
 *
 * @author Andrew Roe
 * @since 1.0
 */
public class DereferenceNullException extends RuntimeException {
    /**
     * Constructs a new {@code DereferenceNullException} with a detailed message
     * @param s message for Exception
     */
    public DereferenceNullException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code DereferenceNullException} with {@code null} as its detailed message
     */
    public DereferenceNullException() {
        super("Attempts to call attribute function with a null reference");
    }
}
