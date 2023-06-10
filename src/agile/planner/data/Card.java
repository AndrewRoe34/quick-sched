package agile.planner.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds Tasks for a specific grouping or category <p>
 * View this as the columns on Trello holding cards (but in our case,
 * the column is the card, and the card is the task)
 *
 * @author Andrew Roe
 */
public class Card {

    /** Label for Card */
    private String title;
    /** List of Tasks for Card */
    private List<Task> list;
    private List<Label> labelList;

    /**
     * Primary constructor for Card
     *
     * @param title title for Card
     */
    public Card(String title) {
        this.title = title;
        labelList = new ArrayList<>();
    }

    /**
     * Gets title for Card
     *
     * @return title
     */
    public String getLabel() {
        return title;
    }

    /**
     * Sets title for Card
     *
     * @param title title for Card
     */
    public void setLabel(String title) {
        this.title = title;
    }

    /**
     * Gets List of Tasks for Card
     *
     * @return List of Tasks
     */
    public List<Task> getList() {
        return list;
    }

    /**
     * Sets List of Tasks for Card
     *
     * @param list List of Tasks
     */
    public void setList(List<Task> list) {
        this.list = list;
    }

    /**
     * Adds a Task to Card
     *
     * @param task Task being added
     * @return boolean status for success
     */
    public boolean addTask(Task task) {
        return list.add(task);
    }

    /**
     * Removes Task from Card
     *
     * @param idx index of Task
     * @return Task being removed
     */
    public Task removeTask(int idx) {
        return list.remove(idx);
    }

    public void addLabel(Label label) {
        labelList.add(label);
    }
}
