package agile.planner.util;

import com.google.gson.Gson;

import java.util.List;

public class JsonHandler {

    public static void readJsonFile(String jsonStr) {
        Gson gson = new Gson();
        Board board = gson.fromJson(jsonStr, Board.class);
        System.out.println("Board\nid=" + board.getId() + ", name=" + board.getBoardName() + ", cards\n");
        for(Card c1 : board.getCardList()) {

        }
    }

    public static void writeJsonFile() {

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

        public void setId(int id) {
            this.id = id;
        }

        public void setBoardName(String boardName) {
            this.boardName = boardName;
        }

        public void setCardList(List<Card> cardList) {
            this.cardList = cardList;
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

        public void setId(int id) {
            this.id = id;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public void setLabelList(List<Label> labelList) {
            this.labelList = labelList;
        }

        public void setTaskList(List<Task> taskList) {
            this.taskList = taskList;
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

        public void setId(int id) {
            this.id = id;
        }

        public void setColor(int color) {
            this.color = color;
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

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public void setLabelList(List<Label> labelList) {
            this.labelList = labelList;
        }

        public void setChecklist(Checklist checklist) {
            this.checklist = checklist;
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

        public void setId(int id) {
            this.id = id;
        }

        public void setChecklistItemList(List<ChecklistItem> checklistItemList) {
            this.checklistItemList = checklistItemList;
        }

        public void setPercentComplete(int percentComplete) {
            this.percentComplete = percentComplete;
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

        public void setId(int id) {
            this.id = id;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }

        public void setComplete(boolean complete) {
            this.complete = complete;
        }
    }
}
