package agile.planner.scripter;

import agile.planner.scripter.exception.InvalidPreProcessorException;

public class ExportState extends State {

    @Override
    protected void processFunc(String line) {
        if(!exportPP) {
            throw new InvalidPreProcessorException("No exports are allowed due to the absence of the __EXPORT__ flag");
        }
        String[] token = processArguments(line, 1, null);
//        scheduleManager.addCardList(cardList);
        scheduleManager.createJBinFile("data/" + token[0], cardList);
    }
}
