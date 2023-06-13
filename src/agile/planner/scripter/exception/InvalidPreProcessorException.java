package agile.planner.scripter.exception;

/**
 * {@code InvalidPreProcessorException} is a subclass of {@link RuntimeException}, upon which it is thrown if
 * the provided preprocessor block is invalid. The preprocessor for {@code Simple} serves to provide an easy setup
 * with all prebuilt mechanisms to build, log, import, export, and debug. Thus, this exception serves to find any violations
 * of this syntax.
 * <p>
 * A prime example that would involve this exception being thrown would be the omission of either the start or end flags:
 * <blockquote><pre>
 *     START:
 *     __DEF_CONFIG__
 *     task: hw, 3, 0  <-- This will result in an exception
 * </pre></blockquote></p>
 * Another instance would be the absence of the configuration option (a core requirement for any scheduling to be possible):
 * <blockquote><pre>
 *     START:
 *     __LOG__
 *     END:  <-- This will result in an exception
 * </pre></blockquote></p>
 * Other cases might involve the inclusion of functions or classes within the preprocessor block:
 * <blockquote><pre>
 *     START:
 *     __DEF_CONFIG__
 *     task: hw, 3, 0  <-- This will result in an exception
 *     END:
 * </pre></blockquote></p>
 * Finally, one of the last reasons why this exception might occur is if a repeating flag is spotted throughout the block:
 * <blockquote><pre>
 *     START:
 *     __DEF_CONFIG__
 *     __CUR_CONFIG__  <-- This will result in an exception (despite the different name)
 *     END:
 * </pre></blockquote>
 * </p>
 * The {@code InvalidPreProcessorException} class is utilized at the exclusion of {@link InvalidGrammarException} since all
 * flags processed within said code block are purely preprocessor attributes.
 *
 * @author Andrew Roe
 * @since 1.0
 */
public class InvalidPreProcessorException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidPreProcessorException} with a detailed message
     * @param s message for Exception
     */
    public InvalidPreProcessorException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code InvalidPreProcessorException} with {@code null} as its detailed message
     */
    public InvalidPreProcessorException() {
        super("Invalid action or property was included in PreProcessor for script");
    }
}
