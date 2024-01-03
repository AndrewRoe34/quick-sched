package agile.planner.scripter;

public class BoolInstance extends ClassInstance {

    private final boolean val;

    public BoolInstance(boolean val) {
        this.val = val;
    }

    public boolean isVal() {
        return val;
    }
}
