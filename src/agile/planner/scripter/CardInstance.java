package agile.planner.scripter;

public class CardInstance extends ClassInstance {

    private final String title;

    public CardInstance(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
