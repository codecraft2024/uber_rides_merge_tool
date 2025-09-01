package com.codeCraft.uber.uberridesmergetool.data;

import com.codeCraft.uber.uberridesmergetool.domain.model.ReceiptData;
import com.codeCraft.uber.uberridesmergetool.domain.util.StatusLogger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class PdfReader {

    public StatusLogger statusLogger;

    public PdfReader(StatusLogger statusLogger){
        this.statusLogger = statusLogger;
    }

    public List<ReceiptData> readAll(String directoryPath) throws IOException {
        List<ReceiptData> receipts = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.pdf")) {
            for (Path filePath : stream) {
                try {
                    ReceiptData data = new UberParser().parse(filePath.toString());
                    receipts.add(data);
                    statusLogger.log("Process File: " + filePath.getFileName());
                } catch (Exception e) {
                     statusLogger.log("Error processing " + filePath + ": " + e.getMessage());
                }
            }
        }
        return receipts;
    }
}
