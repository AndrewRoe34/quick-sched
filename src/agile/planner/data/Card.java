package agile.planner.data;

import agile.planner.util.CheckList;

/**
 * Holds Tasks for a specific grouping or category
 *
 * @author Andrew Roe
 */
public class Card {

    private String name;

    private CheckList checkList;

    //TODO will need to make CheckList use generics
    //TODO look at Trello and see the types of things a Card has (e.g. name, notes, date, etc.)
}
