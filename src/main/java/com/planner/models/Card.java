package com.planner.models;

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

    private int id;
    /** Label for Card */
    private String title;
    /** List of Tasks for Card */
    private List<Task> cardTasks;
    private Colors colorId;

    /**
     * Primary constructor for Card
     *
     * @param title title for Card
     */
    public Card(int id, String title, Colors colorId) {
        this.id = id;
        setTitle(title);
        setColorId(colorId);
        cardTasks = new LinkedList<>();
    }

    public static enum Colors {
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        LIGHT_BLUE,
        BLUE,
        INDIGO,
        VIOLET,
        BLACK,
        LIGHT_GREEN,
        LIGHT_CORAL
    }

    public int getId() {
        return id;
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
        if (task.getColor() == null) task.setColor(colorId);
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

    public Colors getColorId() {
        return colorId;
    }

    public void setColorId(Colors colorId) {
        if (colorId == null) throw new IllegalArgumentException("Card color cannot be null");
        this.colorId = colorId;
    }

    @Override
    public boolean add(Linker o) {
        if (o instanceof Task) {
            return cardTasks.add((Task) o);
        }
        return false;
    }

    @Override
    public boolean remove(Linker o) {
        if (o instanceof Task) {
            return cardTasks.remove((Task) o);
        }
        return false;
    }
}
