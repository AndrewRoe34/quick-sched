package com.planner.ui;

public class Screen {

    public static void clearScreen() {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;

        try {
            if (os.contains("win")) {
                // Windows
                processBuilder = new ProcessBuilder("cmd", "/c", "cls");
            } else {
                // Unix-based (Linux/macOS)
                processBuilder = new ProcessBuilder("clear");
            }

            // Start the process and wait for it to complete
            processBuilder.inheritIO().start().waitFor();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear the screen", e);
        }
    }

}
