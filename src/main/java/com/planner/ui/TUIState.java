package com.planner.ui;

public interface TUIState {

    void setupAndDisplayPage();

    static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
