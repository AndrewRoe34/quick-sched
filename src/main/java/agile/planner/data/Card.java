package agile.planner.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds Tasks for a specific grouping or category <p>
 * View this as the columns on Trello holding cards (but in our case,
 * the column is the card, and the card is the task)
 *
 * @author Andrew Roe
 */
public class Card implements Linker{

    /** Label for Card */
    private String title;
    /** List of Tasks for Card */
    private List<Task> cardTasks;
    private List<Label> cardLabels;

    /**
     * Primary constructor for Card
     *
     * @param title title for Card
     */
    public Card(String title) {
        this.title = title;
        cardLabels = new ArrayList<>();
        cardTasks = new LinkedList<>();
    }

    /**
     * Gets title for Card
     *
     * @return title
     */
    public List<Label> getLabel() {
        return cardLabels;
    }

    /**
     * Sets title for Card
     *
     * @param title title for Card
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets List of Tasks for Card
     *
     * @param cardTasks List of Tasks
     */
    public void setCardTasks(List<Task> cardTasks) {
        this.cardTasks = cardTasks;
    }

    /**
     * Adds a Task to Card
     *
     * @param task Task being added
     * @return boolean status for success
     */
    public boolean addTask(Task task) {
        return cardTasks.add(task);
    }

    /**
     * Removes Task from Card
     *
     * @param idx index of Task
     * @return Task being removed
     */
    public Task removeTask(int idx) {
        return cardTasks.remove(idx);
    }

    /**
     * Removes Task from Card
     *
     * @param task Task being removed
     * @return boolean status for successful removal
     */
    public boolean removeTask(Task task) {
        return cardTasks.remove(task);
    }

    public boolean addLabel(Label label) {
        return cardLabels.add(label);
    }

    @Override
    public String toString() {
        return title;
    }

    public List<Task> getTask() {
        return cardTasks;
    }

    public String getTitle() {
        return title;
    }

    public boolean addLabelList(List<Label> labels) {
        return cardLabels.addAll(labels);
    }

    @Override
    public boolean add(Linker o) {
        if(o instanceof Task) {
            return cardTasks.add((Task) o);
        } else if(o instanceof Label) {
            return cardLabels.add((Label) o);
        }
        return false;
    }

    @Override
    public boolean remove(Linker o) {
        if(o instanceof Task) {
            return cardTasks.remove((Task) o);
        } else if(o instanceof Label) {
            return cardLabels.remove((Label) o);
        }
        return false;
    }
}
