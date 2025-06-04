package com.egyptianbanks.ipn.uberridesmergetool.domain.util;

import org.springframework.stereotype.Component;

import javax.swing.*;


@Component
public class StatusLoggerImpl implements StatusLogger {
    private JTextArea statusTextArea;

    public void setStatusTextArea(JTextArea statusTextArea) {
        this.statusTextArea = statusTextArea;
    }

    @Override
    public void log(String message) {
        if (statusTextArea != null) {
            SwingUtilities.invokeLater(() -> {
                if (!statusTextArea.getText().isEmpty()) {
                    statusTextArea.append("\n");
                }
                statusTextArea.append(message);
                statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
            });
        }
    }
}