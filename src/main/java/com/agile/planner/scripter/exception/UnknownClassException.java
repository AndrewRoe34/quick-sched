package com.agile.planner.scripter.exception;

/**
 * {@code UnknownClassException} is a subclass of {@link RuntimeException}, upon which it is thrown
 * if an unknown class or function (prebuilt or not) is identified. This can occur if the user commits
 * a spelling error, omits the core separator (i.e. ':'), or makes a function call before it is defined.
 * <p>
 * An example of the first error would be the following scenario involving an attempt to create an instance
 * of a Task but fails due to a slight misspelling:
 * <blockquote><pre>
 *     tasks: hw, 3, 0  <-- This will result in an exception
 * </pre></blockquote><p>
 * Another situation that might occur would be the absence of the ':' from the class/function. It serves not
 * only as a visual separator, but it also indicates a certain operation is occurring (whether it be the creation
 * of a class or the execution of a function):
 * <blockquote><pre>
 *     task hw, 3, 0  <-- This will result in an exception
 * </pre></blockquote><p>
 * Finally, similar to Python, Simple is a dynamic scripting language and as such must pre-define custom functions
 * before they are used (else, an exception will occur):
 * <blockquote><pre>
 *     foo: _task, _card  <-- This will result in an exception
 *
 *     foo: task, card
 *       add: _task, _card
 * </pre></blockquote>
 * <p>
 * The {@code UnknownClassException} class differs from {@link InvalidKeyWordException}, which is thrown only when
 * a duplicate in a custom function signature (i.e. the name of the function) occurs.
 *
 * @author Andrew Roe
 * @since 1.0
 */
public class UnknownClassException extends RuntimeException {

    /**
     * Constructs a new {@code UnknownClassException} with a detailed message
     * @param s message for Exception
     */
    public UnknownClassException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code UnknownClassException} with {@code null} as its detailed message
     */
    public UnknownClassException() {
        super("Unknown class or function being utilized");
    }
}
