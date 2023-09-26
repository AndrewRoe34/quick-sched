package agile.planner.scripter.tools;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.scripter.*;
import agile.planner.util.CheckList;

public class ScriptLog {

    private StringBuilder sb;

    public ScriptLog() {
        sb = new StringBuilder();
        sb.append("SCRIPT_LOG:\n");
    }

    public void reportInstantiation(Type t) {
        switch(t.getVariabTypeId()) {
            case BOARD:
                break;
            case TASK:
                reportTaskCreation(t);
                break;
            case CHECKLIST:
                reportCheckListCreation(t);
                break;
            case LABEL:
                reportLabelCreation(t);
                break;
            case CARD:
                reportCardCreation(t);
                break;
            default:
                sb.append("Constant data type\n");
        }
    }

    /**
     * Reports the creation of a {@code Task} via its core attributes
     *
     * @param id identification number
     * @param name name for activity
     * @param hours number of hours
     * @param dueDate number of days till due date
     */
    private void reportTaskCreation(Type t) {
        sb.append("TASK_CREATED: VAR=").append(t.getVariableName());
        Task task = (Task) t.getLinkerData();
        sb.append(", NAME=").append(task.getName());
        sb.append(", HOURS=").append(task.getTotalHours()).append("\n");
//        sb.append(", DUE=").append(task.).append("\n");
    }

    private void reportLabelCreation(Type t) {
        sb.append("LABL_CREATED: VAR=").append(t.getVariableName());
        Label l = (Label) t.getLinkerData();
        sb.append(", NAME=").append(l.getName());
        sb.append(", COLOR=").append(l.getColor()).append("\n");
    }

    private void reportCheckListCreation(Type t) {
        sb.append("CHECKLIST_CREATED: VAR=").append(t.getVariableName());
        CheckList cl = (CheckList) t.getLinkerData();
        sb.append(", NAME=").append(cl.getName()).append("\n");
    }

    private void reportCardCreation(Type t) {
        sb.append("CARD_CREATED: VAR=").append(t.getVariableName());
        Card c = (Card) t.getLinkerData();
        sb.append(", NAME=").append(c.getTitle()).append("\n");
    }

    public void reportLinkFile(String name) {
        sb.append("FILES_LINKED: FILE=").append(name).append("\n");
    }

    public void reportPrintFunc(Type[] args) {
        sb.append("PRINTS: ARGS=[");
        if(args.length > 1) {
//            sb.append(args[0].getVariableName() == null ? "NULL": args[0].getVariableName());
            sb.append("\"");
            sb.append(args[0].toString());
            sb.append("\"");
        }
        for(int i = 1; i < args.length; i++) {
            sb.append(", \"");
//            sb.append(args[i].getVariableName() == null ? "NULL": args[0].getVariableName());
            sb.append(args[i].toString());
            sb.append("\"");
        }
        sb.append("]\n");
    }

    public void reportPreProcessorSetup(PreProcessor preProcessor) {
        sb.append("PREPROC_ATTR: DEF_CONFIG=").append(preProcessor.isDefaultConfig());
        sb.append(", IMPORT=").append(preProcessor.isImprt());
        sb.append(", EXPORT=").append(preProcessor.isExprt());
        sb.append(", LOG=").append(preProcessor.isLog());
        sb.append(", BUILD=").append(preProcessor.isBuild());
        sb.append(", STATS=").append(preProcessor.isStats()).append("\n");
    }

    public void reportFunctionSetup(CustomFunction customFunction) {
        sb.append("FUNC_SETUP: NAME=").append(customFunction.getFuncName());
        sb.append(", PARAM=[");
        if(customFunction.getArgs().length > 1) {
            sb.append(customFunction.getArgs()[0].trim());
        }
        for(int i = 1; i < customFunction.getArgs().length; i++) {
            sb.append(", ");
            sb.append(customFunction.getArgs()[i].trim());
        }
        sb.append("]\n");
    }

    public void reportFunctionCall(StaticFunction func) {
        sb.append("FUNC_CALLS: NAME=").append(func.getFuncName().trim());
        sb.append(", ARGS=[");
        if(func.getArgs().length > 1) {
            sb.append(func.getArgs()[0].trim());
        }
        for(int i = 1; i < func.getArgs().length; i++) {
            sb.append(", ");
            sb.append(func.getArgs()[i].trim());
        }
        sb.append("]\n");
    }

    public void reportAttrFunc(Attributes attr) {
        sb.append("ATTR_CALL: VAR_NAME=").append(attr.getVarName());
        sb.append(", NAME=").append(attr.getAttr());
        sb.append(", ARGS[");
        if(attr.getArgs().length > 1) {
            sb.append(attr.getArgs()[0].trim());
        }
        for(int i = 1; i < attr.getArgs().length; i++) {
            sb.append(", ");
            sb.append(attr.getArgs()[i].trim());
        }
        sb.append("]\n");
    }

    /**
     * Reports any exceptions thrown in the scripting language
     *
     * @param e {@code Exception} thrown
     */
    public void reportException(Throwable e) {
        sb.append("EXCEPTION=").append(e.getMessage()).append("\n");
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
