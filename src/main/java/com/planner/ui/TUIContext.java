package com.planner.ui;

import java.io.IOException;
import java.util.Scanner;

public class TUIContext {

    private TUIState tuiState;

    private static final TUIState sessionState = new SessionState();
    private static final TUIState editorState = new EditorState();
    private static final TUIState configState = new ConfigState();
    private static final TUIState dataAnalyticsState = new DataAnalyticsState();

    private static final String splashScreen = "\n" +
            "\n" +
            "                                ________  ________  ___  ___       _______           ________  ___       ________  ________   ________   _______   ________     \n" +
            "                                |\\   __  \\|\\   ____\\|\\  \\|\\  \\     |\\  ___ \\         |\\   __  \\|\\  \\     |\\   __  \\|\\   ___  \\|\\   ___  \\|\\  ___ \\ |\\   __  \\    \n" +
            "                                \\ \\  \\|\\  \\ \\  \\___|\\ \\  \\ \\  \\    \\ \\   __/|        \\ \\  \\|\\  \\ \\  \\    \\ \\  \\|\\  \\ \\  \\\\ \\  \\ \\  \\\\ \\  \\ \\   __/|\\ \\  \\|\\  \\   \n" +
            "                                 \\ \\   __  \\ \\  \\  __\\ \\  \\ \\  \\    \\ \\  \\_|/__       \\ \\   ____\\ \\  \\    \\ \\   __  \\ \\  \\\\ \\  \\ \\  \\\\ \\  \\ \\  \\_|/_\\ \\   _  _\\  \n" +
            "                                  \\ \\  \\ \\  \\ \\  \\|\\  \\ \\  \\ \\  \\____\\ \\  \\_|\\ \\       \\ \\  \\___|\\ \\  \\____\\ \\  \\ \\  \\ \\  \\\\ \\  \\ \\  \\\\ \\  \\ \\  \\_|\\ \\ \\  \\\\  \\| \n" +
            "                                   \\ \\__\\ \\__\\ \\_______\\ \\__\\ \\_______\\ \\_______\\       \\ \\__\\    \\ \\_______\\ \\__\\ \\__\\ \\__\\\\ \\__\\ \\__\\\\ \\__\\ \\_______\\ \\__\\\\ _\\ \n" +
            "                                    \\|__|\\|__|\\|_______|\\|__|\\|_______|\\|_______|        \\|__|     \\|_______|\\|__|\\|__|\\|__| \\|__|\\|__| \\|__|\\|_______|\\|__|\\|__|\n" +
            "\n" +
            "\n" +
            "                                                                                AGILE PLANNER v0.5.0 [RELEASE]\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                                                                                Start Session                S\n" +
            "                                                                                Editor                       E\n" +
            "                                                                                Config                       C\n" +
            "                                                                                Data Analytics               D\n" +
            "                                                                                Quit                         Q\n" +
            "\n" +
            "                                                                        user> ";

    public void setTuiState(TUIState tuiState) {
        this.tuiState = tuiState;
    }

    public void configurePage() {
        tuiState.setupAndDisplayPage();
    }


    public static void main(String... args) throws IOException, InterruptedException {
        TUIContext tuiContext = new TUIContext();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(splashScreen);
            if (scanner.hasNextLine()) {
                String userInput = scanner.nextLine();
                if (!userInput.isBlank()) {
                    switch (Character.toUpperCase(userInput.charAt(0))) {
                        case 'S':
                            tuiContext.setTuiState(sessionState);
                            tuiContext.configurePage();
                            break;
                        case 'E':
                            tuiContext.setTuiState(editorState);
                            tuiContext.configurePage();
                            break;
                        case 'C':
                            tuiContext.setTuiState(configState);
                            tuiContext.configurePage();
                            break;
                        case 'D':
                            tuiContext.setTuiState(dataAnalyticsState);
                            tuiContext.configurePage();
                            break;
                        case 'Q':
                            System.exit(0);
                    }
                }
            } else break;
            // clear the screen here to refresh the page
            TUIState.clearScreen();
        }
    }
}
