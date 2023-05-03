package agile.planner.scripter;

public class LabelState extends BaseState {

    /**
     * Processes the Label function:
     * label: [_label: var] [{0..#}: set], [name: String]
     *
     * @param line code line being processed
     */
    public void processFunction(String line) {
        processToken(line);
        processSet(line);
        processToken(line);
        //this should be null
        processToken(line);
        //create the label
        //mark all the specified tasks with said label

        //TODO switch back to BaseState
    }
}
