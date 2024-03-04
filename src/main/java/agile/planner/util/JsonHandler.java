package agile.planner.util;

import com.google.gson.Gson;

import java.util.List;

public class JsonHandler {

    public static void processJsonCommand(String jsonStr) {
        Gson gson = new Gson();
        Board board = gson.fromJson(jsonStr, Board.class);

    }

    private static class Board {
        private int id;
        private String boardName;
        private List<Card> cardList;

        public int getId() {
            return id;
        }

        public String getBoardName() {
            return boardName;
        }

        public List<Card> getCardList() {
            return cardList;
        }
    }

    private static class Card {
        private int id;
        private String cardName;
        private List<Label> labelList;
        private List<Task> taskList;

        public int getId() {
            return id;
        }

        public String getCardName() {
            return cardName;
        }

        public List<Label> getLabelList() {
            return labelList;
        }

        public List<Task> getTaskList() {
            return taskList;
        }
    }

    private static class Label {
        private int id;
        private int color;

        public int getId() {
            return id;
        }

        public int getColor() {
            return color;
        }
    }

    private static class Task {
        private int id;
        private String name;
        private int hours;
        private String dueDate;
        private List<Label> labelList;
        private Checklist checklist;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getHours() {
            return hours;
        }

        public String getDueDate() {
            return dueDate;
        }

        public List<Label> getLabelList() {
            return labelList;
        }

        public Checklist getChecklist() {
            return checklist;
        }
    }

    private static class Checklist {
        private int id;
        private List<ChecklistItem> checklistItemList;
        private int percentComplete;

        public int getId() {
            return id;
        }

        public List<ChecklistItem> getChecklistItemList() {
            return checklistItemList;
        }

        public int getPercentComplete() {
            return percentComplete;
        }
    }

    private static class ChecklistItem {
        private int id;
        private String descr;
        private boolean complete;

        public int getId() {
            return id;
        }

        public String getDescr() {
            return descr;
        }

        public boolean isComplete() {
            return complete;
        }
    }
}
