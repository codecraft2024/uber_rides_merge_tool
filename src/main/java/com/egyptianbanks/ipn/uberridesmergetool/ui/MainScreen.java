package com.egyptianbanks.ipn.uberridesmergetool.ui;

import com.egyptianbanks.ipn.uberridesmergetool.domain.service.UberMergeManager;
import com.egyptianbanks.ipn.uberridesmergetool.domain.util.StatusLoggerImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(24, 24));
        mainPanel.setBorder(new EmptyBorder(32, 32, 32, 32));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel header = new JLabel("Uber Rides Merge Tool", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(0, 0, 24, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel splitPanel = new JPanel(new BorderLayout(32, 0));
        splitPanel.setOpaque(false);

        // --- Left: Form Section ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel dirSection = new JPanel();
        dirSection.setLayout(new GridLayout(2, 1, 0, 18));
        dirSection.setOpaque(false);
        dirSection.setBorder(new EmptyBorder(0, 0, 24, 0));
        dirSection.add(createDirectorySelectionPanel("Rides Directory:"));
        dirSection.add(createDirectorySelectionPanel("Output Directory:"));
        formPanel.add(dirSection);

        // Process button (smaller, modern, bootstrap-like)
        JButton processButton = new JButton("Process");
        processButton.setBackground(new Color(40, 167, 69)); // Bootstrap success
        processButton.setForeground(Color.WHITE);
        processButton.setFocusPainted(false);
        processButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        processButton.setPreferredSize(new Dimension(120, 36));
        processButton.setMaximumSize(new Dimension(120, 36));
        processButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(34, 139, 34), 1, true),
                new EmptyBorder(6, 24, 6, 24)
        ));
        processButton.addActionListener(e -> {
            statusTextArea.setText("");
            String outputPath = outputDirLabel.getText();
            String status = uberMergeManager.Process(ridesDirLabel.getText(), outputPath);
            appendStatus(status);

            if (!status.contains("Failed") && !outputPath.equals("Not selected")) {
                openOutputDirectory(outputPath);
            }
        });
        JPanel processPanel = new JPanel();
        processPanel.setOpaque(false);
        processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.X_AXIS));
        processPanel.add(Box.createHorizontalGlue());
        processPanel.add(processButton);
        processPanel.add(Box.createHorizontalGlue());
        formPanel.add(processPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(52, 152, 219), 2, true),
                "Status",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(52, 73, 94)
        ));
        statusPanel.setBackground(new Color(250, 250, 250));

        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);
        statusTextArea.setLineWrap(true);
        statusTextArea.setWrapStyleWord(true);
        statusTextArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        statusTextArea.setBackground(new Color(245, 247, 250));
        statusTextArea.setForeground(new Color(44, 62, 80));

        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        scrollPane.setPreferredSize(new Dimension(200, 180));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        statusPanel.add(scrollPane, BorderLayout.CENTER);

        formPanel.add(statusPanel);

        splitPanel.add(formPanel, BorderLayout.CENTER);

        // --- Right: Scrap Rides Section ---
        JPanel scrapSection = new JPanel();
        scrapSection.setOpaque(false);
        scrapSection.setLayout(new BoxLayout(scrapSection, BoxLayout.Y_AXIS));
        scrapSection.setBorder(new EmptyBorder(0, 0, 0, 0));

        scrapSection.add(Box.createVerticalGlue());

        // Scrap Rides button (smaller, modern, bootstrap-like, in its own card)
        JPanel scrapCard = new JPanel();
        scrapCard.setBackground(new Color(255, 255, 255));
        scrapCard.setBorder(new LineBorder(new Color(52, 152, 219), 2, true));
        scrapCard.setLayout(new GridBagLayout());
        scrapCard.setMaximumSize(new Dimension(160, 100));
        scrapCard.setPreferredSize(new Dimension(160, 100));

        JButton scrapRidesButton = new JButton("<html>Scrap<br>Rides</html>");
        scrapRidesButton.setBackground(new Color(0, 123, 255)); // Bootstrap primary
        scrapRidesButton.setForeground(Color.WHITE);
        scrapRidesButton.setFocusPainted(false);
        scrapRidesButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        scrapRidesButton.setPreferredSize(new Dimension(120, 48));
        scrapRidesButton.setMaximumSize(new Dimension(120, 48));
        scrapRidesButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0, 105, 217), 1, true),
                new EmptyBorder(8, 24, 8, 24)
        ));
        scrapRidesButton.addActionListener(e -> {
            appendStatus("Scrap Rides button clicked.\n\nThis is a test\nin multiple lines.");
            // TODO: Implement scrap rides logic here
        });

        scrapCard.add(scrapRidesButton, new GridBagConstraints());
        scrapSection.add(scrapCard);

        scrapSection.add(Box.createVerticalGlue());

        splitPanel.add(scrapSection, BorderLayout.EAST);

        mainPanel.add(splitPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
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
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(52, 73, 94));
        panel.add(label, BorderLayout.WEST);

        JLabel pathLabel = new JLabel("Not selected");
        pathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pathLabel.setForeground(new Color(127, 140, 141));

        JButton selectButton = new JButton("Browse...");
        selectButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectButton.setBackground(new Color(236, 240, 241));
        selectButton.setForeground(new Color(44, 62, 80));
        selectButton.setFocusPainted(false);
        selectButton.setBorder(new EmptyBorder(8, 22, 8, 22));
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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(selectButton);
        rightPanel.add(Box.createRigidArea(new Dimension(12, 0)));
        rightPanel.add(pathLabel);

        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }
}