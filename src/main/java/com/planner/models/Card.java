package com.planner.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds Tasks for a specific grouping or category <p>
 * View this as the columns on Trello holding cards (but in our case,
 * the column is the card, and the card is the task)
 *
 * @author Andrew Roe
 * @author Abah Olotuche Gabriel
 */
public class Card implements Linker{

    private int id;
    /** Label for Card */
    private String title;
    /** List of Tasks for Card */
    private List<Task> cardTasks;
    /** Color for Card */
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
        cardTasks = new ArrayList<>();
    }

    /**
     * Set of all available colors for a Card
     *
     * @author Andrew Roe
     */
    public enum Colors {
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

    /**
     * Sets title for Card
     *
     * @param title title for Card
     * @throws IllegalArgumentException if title is empty or null
     */
    public void setTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Name for Card cannot be empty or whitespace");
        this.title = title;
    }

    /**
     * Sets List of Tasks for Card
     *
     * @param cardTasks List of Tasks
     * @throws IllegalArgumentException if list of Tasks is empty or null
     */
    public void setCardTasks(List<Task> cardTasks) {
        if (cardTasks == null || cardTasks.isEmpty()) throw new IllegalArgumentException("Task list cannot be empty or null for Card");
        this.cardTasks = cardTasks;
    }

    /**
     * Sets color value for Card
     *
     * @param colorId color id for Card
     * @throws IllegalArgumentException if color id is null
     */
    public void setColorId(Colors colorId) {
        if (colorId == null) throw new IllegalArgumentException("Card color cannot be null");
        this.colorId = colorId;
    }

    /**
     * Gets the ID for the Card
     *
     * @return ID for Card
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the list of Tasks from Card
     *
     * @return list of Tasks from Card
     */
    public List<Task> getTask() {
        return cardTasks;
    }

    /**
     * Gets the title for the Card
     *
     * @return title for Card
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the color id for the Card
     *
     * @return color id for Card
     */
    public Colors getColorId() {
        return colorId;
    }

    /**
     * Adds a Task to Card
     *
     * @param task Task being added
     * @return boolean status for success
     * @throws IllegalArgumentException if Task is null
     */
    public boolean addTask(Task task) {
        if (task == null) throw new IllegalArgumentException("Task cannot be null for Card");
        if (task.getColor() == null) task.setColor(colorId);
        return cardTasks.add(task);
    }

    /**
     * Removes Task from Card given specified index
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
     * @throws IllegalArgumentException if Task provided is null
     */
    public boolean removeTask(Task task) {
        if (task == null) throw new IllegalArgumentException("Task cannot be null when attempting to remove from Card");
        return cardTasks.remove(task);
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean add(Linker o) {
        if (o instanceof Task) {
            return addTask((Task) o);
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
