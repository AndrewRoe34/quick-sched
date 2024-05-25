package com.planner.scripter.tools;

import com.planner.models.Card;
import com.planner.models.Task;
import com.planner.models.CheckList;
import com.planner.scripter.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * One of the built-in tooling options that provides all relevant logging for the {@code Simple} scripting language. This involves
 * providing a clear summary of all functions called, object instances created, and exceptions thrown when encountered.
 *
 * @author Andrew Roe
 */
public class ScriptLog {

    /** Manages the construction of the {@code ScriptLog} */
    private StringBuilder sb;
    private final SimpleDateFormat sdf;

    /**
     * Creates an instance of ScriptLog as a tooling option for the {@code Simple} scripting language
     */
    public ScriptLog() {
        sb = new StringBuilder();
        sb.append(new SimpleDateFormat("[dd-MM-yyyy]").format(Calendar.getInstance().getTime()))
                .append(" Log of all activities from current session: \n\n");
        sdf = new SimpleDateFormat("[HH:mm:ss]");
    }

    public void reportVariableAssignment(Type t) {
        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
        sb.append("VAR_SETUP: NAME=").append(t.getVariableName())
                .append(", VALUE=")
                .append(t)
                .append("\n");
    }

    /**
     * Reports the construction of one of the built-in {@link Type} for the {@code Simple} language
     *
     * @param t variable reference being reported
     */
    public void reportInstantiation(Type t) {
        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
        switch(t.getVariabTypeId()) {
            case BOARD:
                break;
            case TASK:
                reportTaskCreation(t);
                break;
            case CHECKLIST:
                reportCheckListCreation(t);
                break;
            case CARD:
                reportCardCreation(t);
                break;
            default:
                sb.append("CONST TYPE CREATED...\n");
        }
    }

    /**
     * Reports the creation of a {@link Task} via its core attributes for the {@code Simple} language
     *
     * @param t variable reference being reported
     */
    private void reportTaskCreation(Type t) {
        sb.append("TASK_CREATED: VAR=").append(t.getVariableName());
        Task task = (Task) t.getLinkerData();
        sb.append(", NAME=").append(task.getName());
        sb.append(", HOURS=").append(task.getTotalHours()).append("\n");
//        sb.append(", DUE=").append(task.).append("\n");
    }

    /**
     * Reports the creation of a {@link CheckList} via its attributes for the {@code Simple} language
     *
     * @param t variable reference being reported
     */
    private void reportCheckListCreation(Type t) {
        sb.append("CHECKLIST_CREATED: VAR=").append(t.getVariableName());
        CheckList cl = (CheckList) t.getLinkerData();
        sb.append(", NAME=").append(cl.getName()).append("\n");
    }

    /**
     * Reports the creation of a {@link Card} via its attributes for the {@code Simple} language
     *
     * @param t variable reference being reported
     */
    private void reportCardCreation(Type t) {
        sb.append("CARD_CREATED: VAR=").append(t.getVariableName());
        Card c = (Card) t.getLinkerData();
        sb.append(", NAME=").append(c.getTitle()).append("\n");
    }

    /**
     * Reports the linking of a file with regards to functional operations or attribute additions for the {@code Simple} language
     *
     * @param name filename
     */
    public void reportLinkFile(String name) {
        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
        sb.append("FILES_LINKED: FILE=").append(name).append("\n");
    }

//    /**
//     * Reports the {@code print} operation with regards to displaying the formatted String version of a reference or a String literal
//     *
//     * @param args variable references being reported
//     */
//    public void reportPrintFunc(Type[] args) {
//        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
//        sb.append("PRINTS: ARGS=[");
//        if(args.length > 1) {
////            sb.append(args[0].getVariableName() == null ? "NULL": args[0].getVariableName());
//            sb.append("\"");
//            sb.append(args[0].toString());
//            sb.append("\"");
//        }
//        for(int i = 1; i < args.length; i++) {
//            sb.append(", \"");
////            sb.append(args[i].getVariableName() == null ? "NULL": args[0].getVariableName());
//            sb.append(args[i].toString());
//            sb.append("\"");
//        }
//        sb.append("]\n");
//    }

    /**
     * Reports the creation of a {@link PreProcessor} via its core attributes for the {@code Simple} language
     *
     * @param preProcessor all the core prerequisite values needed for the script to run
     */
    public void reportPreProcessorSetup(PreProcessor preProcessor) {
        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
        sb.append("PREPROC_ATTR: DEF_CONFIG=").append(preProcessor.isDefaultConfig());
        sb.append(", LOG=").append(preProcessor.isLog());
        sb.append(", STATS=").append(preProcessor.isStats());
        sb.append(", HTML=").append(preProcessor.isHtml()).append("\n");
    }

    /**
     * Reports the creation of a {@link CustomFunction} via its given parameters for the {@code Simple} language
     *
     * @param customFunction possesses a function name, parameters, code statements, and a possible return option
     */
    public void reportFunctionSetup(CustomFunction customFunction) {
        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
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

    /**
     * Reports the call of a {@link StaticFunction} via its given arguments for the {@code Simple} language
     *
     * @param func possesses a function name and arguments
     */
    public void reportFunctionCall(StaticFunction func) {
        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
        sb.append("FUNC_CALLS: NAME=").append(func.getFuncName().trim());
        sb.append(", ARGS=[");
        if(func.getArgs().length > 0) {
            sb.append(func.getArgs()[0].trim());
        }
        for(int i = 1; i < func.getArgs().length; i++) {
            sb.append(", ");
            sb.append(func.getArgs()[i].trim());
        }
        sb.append("]\n");
    }

    /**
     * Reports the call of a {@link Attributes} via its given arguments for the {@code Simple} language
     *
     * @param attr possesses a method name and arguments
     */
    public void reportAttrFunc(Attributes attr) {
        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
        sb.append("ATTR_CALL: VAR_NAME=").append(attr.getVarName());
        sb.append(", NAME=").append(attr.getAttr());
        sb.append(", ARGS[");
        if(attr.getArgs().length > 0) {
            sb.append(attr.getArgs()[0].trim());
        }
        for(int i = 1; i < attr.getArgs().length; i++) {
            sb.append(", ");
            sb.append(attr.getArgs()[i].trim());
        }
        sb.append("]\n");
    }

    /**
     * Reports the contents of an If Condition along with its boolean status
     *
     * @param args if condition arguments
     * @param status boolean status of conditional test
     */
    public void reportIfCondition(String[] args, boolean status) {
        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
        sb.append("IF_CONDITION: ARGS=")
                .append(Arrays.toString(args))
                .append(", RESULT=")
                .append(status)
                .append("\n");
    }

    /**
     * Reports any exceptions thrown in the {@code Simple} language
     *
     * @param e {@code Exception} thrown
     */
    public void reportException(Throwable e) {
        sb.append(sdf.format(Calendar.getInstance().getTime())).append(" ");
        sb.append("EXCEPTION=").append(e.getMessage()).append("\n");
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
