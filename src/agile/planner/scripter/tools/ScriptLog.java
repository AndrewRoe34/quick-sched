package agile.planner.scripter.tools;

import agile.planner.scripter.functional.State;

public class ScriptLog {

    private StringBuilder sb;

    public ScriptLog() {
        sb = new StringBuilder();
        sb.append("\nSCRIPT_LOG:\n");
    }

    /**
     * Reports the creation of a {@code Task} via its core attributes
     *
     * @param id identification number
     * @param name name for activity
     * @param hours number of hours
     * @param dueDate number of days till due date
     */
    public void reportTaskCreation(int id, String name, int hours, int dueDate) {
        sb.append("TASK_CREATED: ID=").append(id);
        sb.append(", NAME=").append(name);
        sb.append(", HOURS=").append(hours);
        sb.append(", DUE=").append(dueDate).append("\n");
    }

    public void reportLabelCreation(int id, String name, int color) {
        sb.append("LABL_CREATED: ID=").append(id);
        sb.append(", NAME=").append(name);
        sb.append(", COLOR=").append(color).append("\n");
    }

    public void reportCheckListCreation(int id, String name) {
        sb.append("CHECKLIST_CREATED: ID=").append(id);
        sb.append(", NAME=").append(name).append("\n");
    }

    public void reportCardCreation(int id, String name) {
        sb.append("CARD_CREATED: ID=").append(id);
        sb.append(", NAME=").append(name).append("\n");
    }

    public void reportTaskAttr(int id, String attr, String[] args) {
        sb.append("TASK_ID: ID=").append(id);
        sb.append(", ATTR=").append(attr);
        sb.append(", ARGS=[");
        for(int i = 0; i < args.length; i++) {
            if(i > 0) {
                sb.append(", ");
            }
            sb.append(args[i]);
        }
        sb.append("]\n");
    }

    public void reportLabelAttr(int id, String attr, String[] args) {
        sb.append("LABEL_ID: ID=").append(id);
        sb.append(", ATTR=").append(attr);
        sb.append(", ARGS=[");
        for(int i = 0; i < args.length; i++) {
            if(i > 0) {
                sb.append(", ");
            }
            sb.append(args[i]);
        }
        sb.append("]\n");
    }

    public void reportCheckListEdit() {

    }

    public void reportCardEdit() {

    }

    public void reportAddFunc() {

    }

    public void reportRemoveFunc() {

    }

    public void reportLinkFile(String name) {
        sb.append("FILES_LINKED: FILE=").append(name).append("\n");
    }

    public void reportPrintFunc(boolean type, String data) {
        if(type) {
            sb.append("PRINTS_CLASS: CLASS=").append(data).append("\n");
        } else {
            sb.append("PRINTS_STRNG: STRING=").append("\"").append(data).append("\"").append("\n");
        }
    }

    public void reportPreProcessorSetup() {
        sb.append("PREPROC_ATTR: DEF_CONFIG=").append(State.isDefConfigPP());
        sb.append(", DEBUG=").append(State.isDebugPP());
        sb.append(", LOG=").append(State.isLogPP());
        sb.append(", BUILD=").append(State.isBuildPP()).append("\n");
    }

    public void reportFunctionSetup(String definition) {
        sb.append("FUNCTN_SETUP: DEF=").append(definition).append("\n");
    }

    public void reportFunctionCall(String definition) {
        sb.append("FUNCTN_CALLS: DEF=").append(definition).append("\n");
    }

    /**
     * Reports any exceptions thrown in the scripting language
     *
     * @param e {@code Exception} thrown
     */
    public void reportException(Exception e) {
        sb.append("EXCEPTION=").append(e.getMessage()).append("\n");
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
