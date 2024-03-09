package com.agile.planner.util;

import com.agile.planner.models.Board;
import com.agile.planner.models.Card;
import com.agile.planner.models.Label;
import com.agile.planner.models.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHandler {
    //todo will need to reformat individual instances to make sure they properly link
        //todo e.g. 2 task "instances" that should just be 2 "references" to 1 object
    public static String createBoardJson(Board board) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(board);
    }

    public static Board readBoardJson(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Board.class);
    }

    public static String createCardJson(Card card) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(card);
    }

    public static Card readCardJson(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Card.class);
    }

    public static String createTaskJson(Task task) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(task);
    }

    public static Task readTaskJson(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Task.class);
    }

    public static String createLabelJson(Label label) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(label);
    }

    public static Label readLabelJson(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Label.class);
    }
}
