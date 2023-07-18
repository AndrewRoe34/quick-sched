package agile.planner.scripter;

import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.scripter.exception.InvalidPreProcessorException;

public class ImportState extends State {

    @Override
    protected void processFunc(String line) {
        if(!importPP) {
            throw new InvalidPreProcessorException("No imports are allowed due to the absence of the __IMPORT__ flag");
        }
        String[] token = processArguments(line, 1, null);
        if(token[0].length() < 5 || !".jbin".equals(token[0].substring(token[0].length() - 5))) {
            throw new InvalidGrammarException("Expected .jbin file extension but was not");
        }
        scheduleManager.processJBinFile("data/" + token[0]);
    }
}
