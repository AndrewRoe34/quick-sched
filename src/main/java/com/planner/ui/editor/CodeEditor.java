package com.planner.ui.editor;

import com.planner.scripter.Tokenizer;
import com.planner.scripter.Token;
import com.planner.scripter.TokenColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class CodeEditor extends JFrame {
    private JTextPane textPane;
    private JLabel openLabel, saveLabel, quitLabel;
    private boolean hasSaved;
    private File currentFile;
    private Tokenizer tokenizer;

    public CodeEditor() {
        createEditor();
        createLabels();
        setKeyBindings();
        setTitle("Simple Script Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        ImageIcon imageIcon = new ImageIcon("images/icon.png");
        setIconImage(imageIcon.getImage());
        tokenizer = new Tokenizer();
    }

    private void createEditor() {
        textPane = new JTextPane();
        textPane.setBackground(Color.DARK_GRAY);
        textPane.setForeground(Color.WHITE);
        textPane.setCaretColor(Color.WHITE);
        textPane.setBorder(new EmptyBorder(0, 30, 0, 0));

        LineNumbering lineNumbering = new LineNumbering(textPane);
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setRowHeaderView(lineNumbering);

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                lineNumbering.repaint();
            }
            public void removeUpdate(DocumentEvent e) {
                lineNumbering.repaint();
            }
            public void changedUpdate(DocumentEvent e) {
                lineNumbering.repaint();
            }
        });

        add(scrollPane, BorderLayout.CENTER);
    }

    private void createLabels() {
        JPanel labelPanel = new JPanel(new GridLayout(1, 3));
        openLabel = new JLabel(" F1 - Open File ");
        saveLabel = new JLabel(" F2 - Save ");
        quitLabel = new JLabel(" F3 - Quit ");

        labelPanel.add(openLabel);
        labelPanel.add(saveLabel);
        labelPanel.add(quitLabel);

        add(labelPanel, BorderLayout.SOUTH);
    }

    private void setKeyBindings() {
        textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "openFile");
        textPane.getActionMap().put("openFile", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File currentDirectory = new File("data/scripts/");
                File[] files = currentDirectory.listFiles();
                if (files != null && files.length > 0) {
                    String[] fileNames = new String[files.length];
                    for (int i = 0; i < files.length; i++) {
                        fileNames[i] = files[i].getName();
                    }
                    String selectedFile = (String) JOptionPane.showInputDialog(null, "Choose a script:", "Open File",
                            JOptionPane.PLAIN_MESSAGE, null, fileNames, fileNames[0]);
                    if (selectedFile != null) {
                        currentFile = new File(currentDirectory, selectedFile);
                        try {
                            byte[] bytes = Files.readAllBytes(currentFile.toPath());
                            String content = new String(bytes, StandardCharsets.UTF_8);

                            List<Token> tokens = tokenizer.scanTokens(content);

                            textPane.setDocument(colorizeTokens(tokens));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        hasSaved = true;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No scripts are available.");
                }
            }
        });

        textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "saveFile");
        textPane.getActionMap().put("saveFile", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = "";

                if (!hasSaved) {
                    String fileName = JOptionPane.showInputDialog(null, "Enter script name:", "Save File",
                            JOptionPane.PLAIN_MESSAGE);
                    if (fileName != null && !fileName.isBlank()) {
                        if (fileName.length() > 5 && ".smpl".equals(fileName.substring(fileName.length() - 5))) {
                            // do nothing
                        } else {
                            fileName += ".smpl";
                        }

                        currentFile = new File("data/scripts/" + fileName);
                        content = textPane.getText();
                        try {
                            Files.write(currentFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        hasSaved = true;
                    }
                } else {
                    content = textPane.getText();

                    try {
                        Files.write(
                                currentFile.toPath(),
                                content.getBytes(StandardCharsets.UTF_8)
                        );
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                // Applies coloring to text of the saved file
                List<Token> tokens = tokenizer.scanTokens(content);

                textPane.setDocument(colorizeTokens(tokens));
            }
        });

        textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "quitEditor");
        textPane.getActionMap().put("quitEditor", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the CodeEditor window
            }
        });
    }

    public StyledDocument colorizeTokens(List<Token> tokens) {
        StyledDocument doc = textPane.getStyledDocument();

        // Clear the document
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // Add each token with its own style
        for (Token token : tokens) {
            Style style = textPane.addStyle("Token Style", null);
            StyleConstants.setForeground(style, convertColorStringToAWTColor(token.getColor()));

            try {
                doc.insertString(doc.getLength(), token.getValue(), style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        return doc;
    }

    private Color convertColorStringToAWTColor(TokenColor color) {
        switch (color) {
            case GREEN:
                return Color.GREEN;
            case RED:
                return Color.RED;
            case PURPLE:
                return Color.MAGENTA;
            case LIGHT_GREEN:
                return Color.DARK_GRAY;
            case LIGHT_BLUE:
                return Color.CYAN;
            case BLUE:
                return Color.BLUE;
            case YELLOW:
                return Color.YELLOW;
            case ORANGE:
                return Color.ORANGE;
            case PINK:
                return Color.PINK;
            default:
                return Color.WHITE;
        }
    }
}

class LineNumbering extends JComponent {
    private final JTextPane textPane;

    public LineNumbering(JTextPane textPane) {
        this.textPane = textPane;
        textPane.setFont(new Font("monospaced", Font.PLAIN, 12));
        setPreferredSize(new Dimension(30, textPane.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        int lineHeight = textPane.getFontMetrics(textPane.getFont()).getHeight();
        int startOffset = textPane.getInsets().top + lineHeight / 2;

        int lineCount = textPane.getStyledDocument().getDefaultRootElement().getElementCount();
        for (int i = 0; i < lineCount; i++) {
            String lineNumber = String.valueOf(i + 1);
            g.drawString(lineNumber, 10, startOffset + i * lineHeight);
        }
    }
}
