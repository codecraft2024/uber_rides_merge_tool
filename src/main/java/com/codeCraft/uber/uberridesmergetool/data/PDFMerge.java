package com.codeCraft.uber.uberridesmergetool.data;

import com.codeCraft.uber.uberridesmergetool.domain.model.ReceiptData;
import com.codeCraft.uber.uberridesmergetool.domain.util.StatusLogger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PDFMerge {

    private final String inputDirectory;
    private final StatusLogger statusLogger;
    public PDFMerge(String inputDirectory, StatusLogger statusLogger) {

        this.inputDirectory = inputDirectory;
        this.statusLogger = statusLogger;
    }

    public void merge(List<ReceiptData> receipts, String outputPath) throws IOException {
        if (receipts == null || receipts.isEmpty()) {
            throw new IllegalArgumentException("No receipts to merge");
        }

        // Sort receipts by date
        Collections.sort(receipts, new ReceiptDateComparator());

        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(outputPath);

        // Add each PDF in sorted order
        for (ReceiptData receipt : receipts) {
            File pdfFile = new File(inputDirectory, receipt.getFileName());
            if (!pdfFile.exists()) {
                statusLogger.log("PDF file not found: " + pdfFile.getAbsolutePath());
                continue;
            }

            try (PDDocument document = PDDocument.load(pdfFile)) {
                merger.addSource(pdfFile);
            } catch (IOException e) {
                statusLogger.log("Error adding PDF: " + pdfFile.getAbsolutePath() + " - " + e.getMessage());
            }
        }

        // Merge all PDFs
        try {
            merger.mergeDocuments(null);
            statusLogger.log("Successfully merged " + receipts.size() + " PDFs to " + outputPath);
        } catch (IOException e) {
            throw new IOException("Failed to merge PDFs: " + e.getMessage(), e);
        }
    }

    public static class ReceiptDateComparator implements Comparator<ReceiptData> {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public int compare(ReceiptData r1, ReceiptData r2) {
            try {
                Date date1 = dateFormat.parse(r1.getDate());
                Date date2 = dateFormat.parse(r2.getDate());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                return 0;
            }
        }
    }
}