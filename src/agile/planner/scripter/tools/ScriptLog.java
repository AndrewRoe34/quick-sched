package agile.planner.scripter.tools;

public class ScriptLog {

    private StringBuilder sb;

    public ScriptLog() {
        sb = new StringBuilder();
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

    public void reportLabelCreation() {

    }

    public void reportCheckListCreation() {

    }

    public void reportCardCreation() {

    }
    //might remove Day state altogether

    public void reportTaskEdit() {

    }

    public void reportLabelEdit() {

    }

    public void reportCheckListEdit() {

    }

    public void reportCardEdit() {

    }

    public void reportAddFunc() {

    }

    public void reportEditFunc() {

    }

    public void reportRemoveFunc() {

    }

    public void reportPrintFunc() {

    }

    public void reportPreProcessorSetup() {

    }

    public void reportFunctionSetup() {

    }

    public void reportFunctionCall() {

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
