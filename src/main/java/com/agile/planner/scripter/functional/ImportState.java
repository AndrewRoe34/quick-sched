package com.agile.planner.scripter.functional;

import com.agile.planner.scripter.exception.InvalidPreProcessorException;

public class ImportState extends State {

    @Override
    protected void processFunc(String line) {
        if(!importPP) {
            throw new InvalidPreProcessorException("No imports are allowed due to the absence of the __IMPORT__ flag");
        }
        String[] token = processArguments(line, 1, null);
        scheduleManager.importJBinFile("data/" + token[0]);
        importPP = false;
//        LocalDate ld = LocalDate.parse("");
//        ld.get
    }
}
