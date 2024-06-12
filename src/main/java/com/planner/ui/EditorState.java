package com.planner.ui;

import com.planner.ui.editor.CodeEditor;

import javax.swing.*;

public class EditorState implements TUIState {
    @Override
    public void setupAndDisplayPage() {
        // opens code editor (Swing)
        SwingUtilities.invokeLater(() -> {
            CodeEditor editor = new CodeEditor();
            editor.setSize(800, 600);
            editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            editor.setLocationRelativeTo(null);
            editor.setVisible(true);
        });
    }
}
