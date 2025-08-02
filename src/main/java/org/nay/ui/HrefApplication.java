package org.nay.ui;

import mdlaf.MaterialLookAndFeel;
import org.nay.HsPerfDataParser;
import org.nay.ui.view.ResultView;
import org.nay.ui.view.SimpleView;
import org.nay.utils.PIDUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class HrefApplication extends JFrame {

    private int selectedPid;
    private int viewSelection;
    private String currentPath = null;
    private JPanel viewer = null;

    public HrefApplication(){
        setTitle("HsPerfParser");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setupDesign();
        initializeComponents();
        setVisible(true);
    }

    public void initializeComponents(){
        JPanel mainPanel = new JPanel(new FlowLayout());
        JComboBox<String> jComboBox = new JComboBox<>(),
                selectedView = new JComboBox<>();

        JButton importButton = new JButton("Import File");
        PIDUtils.getPids().forEach(jComboBox::addItem);
        selectedView.addItem("Categorized View");
        selectedView.addItem("All In One View");
        jComboBox.setSelectedItem(null);
        JLabel label = new JLabel("Import by Java PID: ");
        mainPanel.add(label);
        mainPanel.add(jComboBox);
        mainPanel.add(selectedView, BorderLayout.PAGE_END);
        mainPanel.add(importButton);

        this.add(mainPanel, BorderLayout.WEST);

        selectedView.addActionListener(e -> {
             viewSelection = selectedView.getSelectedIndex();
            System.out.println("debug view: "+selectedView.getSelectedIndex());
            if (currentPath != null){
                parseAndView(currentPath);
            }
        });

        importButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select file to import");

            int selection = fc.showOpenDialog(this);
            if (selection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();

                try {
                    String originalTitle = this.getTitle();
                    this.setTitle("Processing: " + selectedFile.getAbsolutePath());

                    parseAndView(selectedFile.getAbsolutePath());

                    currentPath = selectedFile.getAbsolutePath();

                    System.out.println("debug: "+selectedFile.getAbsolutePath());

                    this.setTitle(originalTitle);

                } catch (Exception ex) {
                    this.setTitle(this.getTitle().replace("Processing: " + selectedFile.getAbsolutePath(), ""));
                }
            }
        });

        jComboBox.addActionListener(e -> {
            selectedPid = Integer.parseInt(jComboBox.getSelectedItem().toString());
            String tempDir = System.getProperty("java.io.tmpdir"),
                    userName = System.getProperty("user.name");

            Path sourcePath = Paths.get(tempDir, "hsperfdata_" + userName, String.valueOf(selectedPid)),
                    parserDir = Paths.get(tempDir, "refparser");
            try {
                Files.createDirectories(parserDir);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            Path destination = parserDir.resolve(String.valueOf(selectedPid));

            try {
                Files.copy(sourcePath, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            System.out.println(destination);

            currentPath = String.valueOf(destination);

            parseAndView(String.valueOf(destination));
        });
    }

    private void setupDesign() {
        try {
            UIManager.setLookAndFeel(new MaterialLookAndFeel());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private void parseAndView(String path){

        HsPerfDataParser parser = new HsPerfDataParser();

        Map<String, Object> data = parser.parseHsPerfDataFromFile(String.valueOf(path));

        if (data == null) {
            System.out.println("Cant parse " + path);
            return;
        }

        System.out.println("Entries: " + data.size());

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        if (viewer != null){
            this.remove(viewer);
        }

        switch (viewSelection) {
            case 0:
                viewer = new ResultView(data);
                ((ResultView) viewer).updateData(data);
                break;
            case 1:
                this.remove(viewer);
                viewer = new SimpleView(data);
                ((SimpleView) viewer).updateData(data);
                break;
        }
        this.add(viewer, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

}