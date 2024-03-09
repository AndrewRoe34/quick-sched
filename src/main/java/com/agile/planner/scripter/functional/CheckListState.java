package com.agile.planner.scripter.functional;

import com.agile.planner.scripter.exception.InvalidGrammarException;
import com.agile.planner.models.CheckList;

public class CheckListState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 1, ",");
        try {
            int id = scheduleManager.getLastCLId() + clList.size();
            String name = tokens[0];
            clList.add(new CheckList(id, name));
//            scriptLog.reportCheckListCreation(id, name);
            System.out.println("Checklist added.. [C" + (scheduleManager.getLastCLId() + clList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[checklist: <name: string>]");
        }
    }
}
