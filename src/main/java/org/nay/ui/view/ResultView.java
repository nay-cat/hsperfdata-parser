package org.nay.ui.view;

import org.nay.ui.view.model.KeyValueTableModel;
import org.nay.ui.view.model.render.ValueCellRenderer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import static org.nay.ui.view.model.KeyValueTableModel.getString;
import static org.nay.utils.StringUtils.copyToClipboard;
import static org.nay.utils.StringUtils.formatValue;

public class ResultView extends JPanel {

    private Map<String, Object> data;

    public ResultView(Map<String, Object> data) {
        this.data = data;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(),
                buttonPanel = new JPanel(new FlowLayout());

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JButton cAll = new JButton("COPY ALL");
        cAll.addActionListener(e -> copyAllData());

        buttonPanel.add(cAll);
        add(buttonPanel, BorderLayout.NORTH);

        /**
         * si alguien está leyendo esta reverenda mierda aquí la explicación de Map<String, List<Map.Entry<String, Object>>>
         * la primera <String> es el nombre de la categoría, ej "Java Propierties"
         * Luego la lista reune todas las key y valores que ha encontrado el parser en esa categoría como un único objeto
         * el Map.Entry es directamente ya el dato parseado siendo la primera String el nombre y el Object el valor
         */
        Map<String, List<Map.Entry<String, Object>>> categorizedData = categorizeData();

        System.out.println("DEBUG: "+categorizedData.toString());

        for (Map.Entry<String, List<Map.Entry<String, Object>>> category : categorizedData.entrySet()) {
            JPanel categoryPanel = createCategoryPanel(category.getKey(), category.getValue());
            mainPanel.add(categoryPanel);
            mainPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane, BorderLayout.CENTER);
    }


    private Map<String, List<Map.Entry<String, Object>>> categorizeData() {
        Map<String, List<Map.Entry<String, Object>>> categories = new LinkedHashMap<>();

        /**
         * https://github.com/openjdk/jdk/blob/master/src/hotspot/share/runtime/perfData.cpp
         */

        categories.put("Java Properties", new ArrayList<>());
        categories.put("Garbage Collection", new ArrayList<>());
        categories.put("Class Loading", new ArrayList<>());
        categories.put("Compilation", new ArrayList<>());
        categories.put("Runtime", new ArrayList<>());
        categories.put("Threading", new ArrayList<>());
        categories.put("Memory", new ArrayList<>());
        categories.put("Other", new ArrayList<>());

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey().toLowerCase();

            if (key.startsWith("java.property") || key.startsWith("com.sun.property")) {
                categories.get("Java Properties").add(entry);
            } else if (key.startsWith("sun.gc") || key.contains("gc.")) {
                categories.get("Garbage Collection").add(entry);
            } else if (key.startsWith("sun.cls") || key.startsWith("java.cls") ||
                    key.contains("class") || key.contains("loader")) {
                categories.get("Class Loading").add(entry);
            } else if (key.startsWith("sun.ci") || key.startsWith("java.ci") ||
                    key.contains("compil")) {
                categories.get("Compilation").add(entry);
            } else if (key.startsWith("sun.rt") || key.contains("runtime") ||
                    key.contains("vm")) {
                categories.get("Runtime").add(entry);
            } else if (key.startsWith("java.threads") || key.contains("thread") ||
                    key.contains("sync")) {
                categories.get("Threading").add(entry);
            } else if (key.contains("memory") || key.contains("metaspace") ||
                    key.contains("tlab")) {
                categories.get("Memory").add(entry);
            } else {
                categories.get("Other").add(entry);
            }
        }

        categories.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return categories;
    }

    private JPanel createCategoryPanel(String categoryName, List<Map.Entry<String, Object>> entries) {
        JPanel panel = new JPanel(new BorderLayout()),
                categoryButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                categoryName + " (" + entries.size() + " items)",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Verdana", Font.BOLD, 14)
        ));

        entries.sort(Map.Entry.comparingByKey());

        KeyValueTableModel model = new KeyValueTableModel(entries);
        JTable table = new JTable(model);

        setupTable(table);

        JButton copyCategoryButton = new JButton("Copy Categor"),
                toggleButton = new JButton("Collapse");

        Font buttonFont = new Font("Verdana", Font.PLAIN, 14);
        copyCategoryButton.setFont(buttonFont);
        toggleButton.setFont(buttonFont);

        copyCategoryButton.addActionListener(e -> copyCategory(entries));

        categoryButtonPanel.add(copyCategoryButton);
        categoryButtonPanel.add(toggleButton);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, Math.min(300, entries.size() * 25 + 50)));

        panel.add(categoryButtonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        toggleButton.addActionListener(new ActionListener() {
            private boolean collapsed = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (collapsed) {
                    scrollPane.setVisible(true);
                    toggleButton.setText("Collapse");
                    panel.revalidate();
                } else {
                    scrollPane.setVisible(false);
                    toggleButton.setText("Expand");
                    panel.revalidate();
                }
                collapsed = !collapsed;
            }
        });

        return panel;
    }

    private void setupTable(JTable table) {
        table.setFont(new Font("Verdana", Font.PLAIN, 14));
        table.setRowHeight(20);
        table.setShowGrid(true);
        table.setGridColor(Color.black);
        table.setSelectionMode(2);
        table.setCellSelectionEnabled(true);

        TableColumn key = table.getColumnModel().getColumn(0),
                value = table.getColumnModel().getColumn(1);

        key.setPreferredWidth(300);
        key.setMinWidth(200);

        value.setPreferredWidth(400);
        value.setMinWidth(200);
        value.setCellRenderer(new ValueCellRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    if (row >= 0 && col >= 0) {
                        String value = table.getValueAt(row, col).toString();
                        copyToClipboard(value);
                    }
                }
            }
        });
    }


    private void copyCategory(List<Map.Entry<String, Object>> entries) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : entries) {
            sb.append(entry.getKey()).append(": ").append(formatValue(entry.getValue())).append("\n");
        }
        copyToClipboard(sb.toString());
    }


    private void copyAllData() {
        StringBuilder copydata = new StringBuilder();
        Map<String, List<Map.Entry<String, Object>>> categorizedData = categorizeData();

        for (Map.Entry<String, List<Map.Entry<String, Object>>> category : categorizedData.entrySet()) {
            copydata.append(category.getKey()).append(":\n");

            category.getValue().sort(Map.Entry.comparingByKey());

            for (Map.Entry<String, Object> entry : category.getValue()) {
                copydata.append(entry.getKey()).append(": ").append(formatValue(entry.getValue())).append("\n");
            }
        }

        copyToClipboard(copydata.toString());
    }

    public void updateData(Map<String, Object> newData) {
        this.data = newData;
        removeAll();
        initializeUI();
        revalidate();
        repaint();
    }
}