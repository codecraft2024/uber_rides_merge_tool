package com.egyptianbanks.ipn.uberridesmergetool.ui;

import com.egyptianbanks.ipn.uberridesmergetool.domain.service.UberMergeManager;
import com.egyptianbanks.ipn.uberridesmergetool.domain.util.StatusLoggerImpl;
 import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.File;

@org.springframework.stereotype.Component
public class MainScreen extends JFrame {
    private final UberMergeManager uberMergeManager;
    private JLabel ridesDirLabel;
    private JLabel outputDirLabel;
    private JTextArea statusTextArea;
    private final StatusLoggerImpl statusLogger;

    @Autowired
    public MainScreen(UberMergeManager uberMergeManager, StatusLoggerImpl statusLogger) {
        this.uberMergeManager = uberMergeManager;
        this.statusLogger = statusLogger;
    }

    @PostConstruct
    public void init() {
        initializeUI();
        statusLogger.setStatusTextArea(statusTextArea);
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Uber Rides Merge Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BorderLayout(10, 10));

        // Header
        JLabel header = new JLabel("Uber Rides Merge Tool");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Rides Directory Selection
        JPanel ridesPanel = createDirectorySelectionPanel("Rides Directory:");
        contentPanel.add(ridesPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Output Directory Selection
        JPanel outputPanel = createDirectorySelectionPanel("Output Directory:");
        contentPanel.add(outputPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Process Button
        JButton processButton = new JButton("Process");
        processButton.setBackground(new Color(70, 130, 180));
        processButton.setForeground(Color.WHITE);
        processButton.setFocusPainted(false);
        processButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        processButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        processButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        processButton.addActionListener(e -> {
            statusTextArea.setText("");
            String outputPath = outputDirLabel.getText();
            String status = uberMergeManager.Process(ridesDirLabel.getText(), outputPath);
            appendStatus(status);

            // Open output directory after successful processing
            if (!status.contains("Failed") && !outputPath.equals("Not selected")) {
                openOutputDirectory(outputPath);
            }
        });

        contentPanel.add(processButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));

        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);
        statusTextArea.setLineWrap(true);
        statusTextArea.setWrapStyleWord(true);
        statusTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        statusTextArea.setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        scrollPane.setPreferredSize(new Dimension(200, 150));
        statusPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(statusPanel);
        panel.add(contentPanel, BorderLayout.CENTER);
        add(panel);
    }

    private void openOutputDirectory(String path) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                File dir = new File(path);
                if (dir.exists()) {
                    desktop.open(dir);
                    appendStatus("Opened output directory: " + path);
                } else {
                    appendStatus("Output directory doesn't exist: " + path);
                }
            } else {
                appendStatus("Desktop operations not supported on this platform");
            }
        } catch (Exception ex) {
            appendStatus("Error opening output directory: " + ex.getMessage());
        }
    }

    private void appendStatus(String text) {
        SwingUtilities.invokeLater(() -> {
            if (!statusTextArea.getText().isEmpty()) {
                statusTextArea.append("\n");
            }
            statusTextArea.append(text);
            statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
        });
    }

    private JPanel createDirectorySelectionPanel(String labelText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pathLabel = new JLabel("Not selected");
        pathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton selectButton = new JButton("Browse...");
        selectButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                pathLabel.setText(selectedFile.getAbsolutePath());

                if (labelText.equals("Rides Directory:")) {
                    ridesDirLabel = pathLabel;
                } else {
                    outputDirLabel = pathLabel;
                }
            }
        });

        buttonPanel.add(selectButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(pathLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonPanel);

        return panel;
    }
}