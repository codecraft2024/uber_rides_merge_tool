package com.egyptianbanks.ipn.uberridesmergetool.util;

import com.egyptianbanks.ipn.uberridesmergetool.util.StatusLogger;
import org.springframework.stereotype.Component;

import javax.swing.*;


@Component
public class StatusLoggerImpl implements StatusLogger {
    private JTextArea statusTextArea;

    public void setStatusTextArea(JTextArea statusTextArea) {
        this.statusTextArea = statusTextArea;
    }

    @Override
    public void logStatus(String message) {
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