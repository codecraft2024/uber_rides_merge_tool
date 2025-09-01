package com.codeCraft.uber.uberridesmergetool.data;

import com.codeCraft.uber.uberridesmergetool.domain.model.ReceiptData;
import java.io.FileWriter;
import java.util.List;

public class FullDataCSVExtractor {
    public void writeDetailedCsv(List<ReceiptData> receipts, String csvPath) {
        try (FileWriter writer = new FileWriter(csvPath)) {
            writer.append("FileName,");
            writer.append(ReceiptData.csvHeader());
            writer.append(",FromLocation,ToLocation\n");
            for (ReceiptData receipt : receipts) {
                writer.append(receipt.getFileName());
                writer.append(",");
                writer.append(receipt.toCsvRow());
                writer.append(",");
                writer.append(receipt.getFromLocation());
                writer.append(",");
                writer.append(receipt.getToLocation());
                writer.append("\n");
            }
        } catch (Exception e) {
            System.err.println("Error writing detailed expenses CSV: " + e.getMessage());
        }
    }
}
