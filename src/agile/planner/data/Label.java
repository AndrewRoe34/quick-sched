package agile.planner.data;

public class Label {

    private int id;
    private String name;
    private int color;

    public Label(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    //TODO each label needs a string and a color
}
