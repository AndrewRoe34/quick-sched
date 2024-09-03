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
public class Card {

    private int id;
    /** Label for Card */
    private String name;
    /** Color for Card */
    private Color colorId;

    /**
     * Primary constructor for Card
     *
     * @param name title for Card
     */
    public Card(int id, String name, Color colorId) {
        this.id = id;
        setName(name);
        setColorId(colorId);
    }

    /**
     * Set of all available colors for a Card
     *
     * @author Andrew Roe
     */
    public enum Color {
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
     * @param name title for Card
     * @throws IllegalArgumentException if title is empty or null
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name for Card cannot be empty or whitespace");
        this.name = name;
    }

    /**
     * Sets color value for Card
     *
     * @param colorId color id for Card
     * @throws IllegalArgumentException if color id is null
     */
    public void setColorId(Color colorId) {
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
     * Gets the title for the Card
     *
     * @return title for Card
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the color id for the Card
     *
     * @return color id for Card
     */
    public Color getColorId() {
        return colorId;
    }

    @Override
    public String toString() {
        return name;
    }
}
