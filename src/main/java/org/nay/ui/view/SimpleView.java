package org.nay.ui.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

import static org.nay.utils.StringUtils.copyToClipboard;
import static org.nay.utils.StringUtils.formatValue;

public class SimpleView extends JPanel {

    private Map<String, Object> data;
    private JTextArea textArea;
    private JTextField searchField;
    private String originalText;

    public SimpleView(Map<String, Object> data) {
        this.data = data;
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout()),
                searchPanel = new JPanel(new BorderLayout());

        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        searchPanel.add(new JLabel("SEARCH: "), BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(new Font("Verdana", Font.PLAIN, 14));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterText();
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton clearButton = new JButton("CLEAR"),
                copyButton = new JButton("COPY ALL");

        clearButton.addActionListener(e -> {
            searchField.setText("");
            textArea.setText(originalText);
        });

        copyButton.addActionListener(e -> copyToClipboard(textArea.getText()));

        buttonPanel.add(clearButton);
        buttonPanel.add(copyButton);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        textArea = new JTextArea();
        textArea.setFont(new Font("Verdana", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        textArea.setSelectionColor(new Color(184, 207, 229));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }



    private void loadData() {
        StringBuilder sb = new StringBuilder();

        data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    sb.append(entry.getKey())
                            .append(": ")
                            .append(formatValue(entry.getValue()))
                            .append("\n");
                });

        originalText = sb.toString();
        textArea.setText(originalText);
        textArea.setCaretPosition(0);
    }

    private void filterText() {
        String searchTerm = searchField.getText().toLowerCase().trim();

        if (searchTerm.isEmpty()) {
            textArea.setText(originalText);
            return;
        }

        StringBuilder filteredText = new StringBuilder();
        String[] lines = originalText.split("\n");

        for (String line : lines) {
            if (line.toLowerCase().contains(searchTerm)) {
                filteredText.append(line).append("\n");
            }
        }

        textArea.setText(filteredText.toString());
        textArea.setCaretPosition(0);
    }

    public void updateData(Map<String, Object> newData) {
        this.data = newData;
        loadData();
        searchField.setText("");
    }
}