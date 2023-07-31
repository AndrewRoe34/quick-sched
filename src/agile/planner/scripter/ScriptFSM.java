package agile.planner.scripter;

import agile.planner.data.DataAttr;
import agile.planner.data.Task;

public class ScriptFSM {

    //this class will take the parser data and "interpret" it to execute the code

    public static void main(String[] args) {
        Object retVal = test();
        if(retVal instanceof String) {
            // ...
        }
    }

    public static Object test() {
        Parser parser = new Parser();
        Attributes attr = parser.parseAttributes("t1.get_title()");
        //determine the correct value
        Type var = parser.lookupVariable(attr.getVarName());
        Object retVal = null;
        switch(attr.getAttr()) {
            case "get_hours":
            case "get_title":
                retVal = var.attrSet(DataAttr.GET_TITLE, attr.getArguments());
        }
        return retVal;
    }
}
