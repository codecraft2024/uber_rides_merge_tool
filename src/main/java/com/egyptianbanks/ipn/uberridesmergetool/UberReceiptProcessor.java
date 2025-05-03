package com.egyptianbanks.ipn.uberridesmergetool;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UberReceiptProcessor {

    public static void main(String[] args) {
        System.out.println("app started>");


        String inputDir = "/Users/minaanwer/Downloads";
        String outputFile = "/Users/minaanwer/Downloads/output.csv";

        try {
            List<ReceiptData> allReceipts = processDirectory(inputDir);
            writeToCSV(allReceipts, outputFile);
            System.out.println("Successfully processed " + allReceipts.size() + " receipts to " + outputFile);
        } catch (Exception e) {
            System.err.println("Error processing receipts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<ReceiptData> processDirectory(String directoryPath) throws IOException {
        List<ReceiptData> receipts = new ArrayList<>();

        // Get all PDF files in directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.pdf")) {
            for (Path filePath : stream) {
                try {
                    ReceiptData data = parseUberReceipt(filePath.toString());
                    receipts.add(data);
                    System.out.println("Processed: " + filePath.getFileName());
                } catch (Exception e) {
                    System.err.println("Error processing " + filePath + ": " + e.getMessage());
                }
            }
        }
        return receipts;
    }

    public static ReceiptData parseUberReceipt(String pdfPath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Extract total amount (looking for "Total EGP X.XX")
            Pattern amountPattern = Pattern.compile("Total\\s+EGP\\s+(\\d+\\.\\d{2})");
            Matcher amountMatcher = amountPattern.matcher(text);
            String amount = amountMatcher.find() ? amountMatcher.group(1) : null;

            // Extract date - first try full format (April 3, 2025)
            Pattern datePattern = Pattern.compile(
                    "(January|February|March|April|May|June|July|August|September|October|November|December)\\s+\\d{1,2},\\s+\\d{4}"
            );
            Matcher dateMatcher = datePattern.matcher(text);
            String date = dateMatcher.find() ? dateMatcher.group() : null;

            // If full date not found, look for short format (4/3/25 11:00 PM)
            if (date == null) {
                Pattern altDatePattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2,4}\\s+\\d{1,2}:\\d{2}\\s+[AP]M");
                Matcher altDateMatcher = altDatePattern.matcher(text);
                date = altDateMatcher.find() ? altDateMatcher.group() : null;
            }

            if (amount == null || date == null) {
                throw new IOException("Could not find required data in the PDF");
            }

            return new ReceiptData(amount, date, Paths.get(pdfPath).getFileName().toString());
        }
    }

    public static void writeToCSV(List<ReceiptData> receipts, String outputPath) throws IOException {
        // Sort receipts by date
        receipts.sort((r1, r2) -> {
            try {
                // Parse dates in format "April 3, 2025"
                SimpleDateFormat format1 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                // Parse dates in format "4/3/25 11:00 PM"
                SimpleDateFormat format2 = new SimpleDateFormat("M/d/yy h:mm a", Locale.ENGLISH);

                Date date1 = parseDate(r1.getDate(), format1, format2);
                Date date2 = parseDate(r2.getDate(), format1, format2);

                return date1.compareTo(date2);
            } catch (ParseException e) {
                return 0; // If parsing fails, maintain original order
            }
        });

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("File,Amount,Date");
            for (ReceiptData receipt : receipts) {
                writer.printf("\"%s\",\"EGP %s\",\"%s\"%n",
                        receipt.getFileName(),
                        receipt.getAmount(),
                        receipt.getDate());
            }
        }
    }

    private static Date parseDate(String dateString, SimpleDateFormat format1, SimpleDateFormat format2)
            throws ParseException {
        try {
            return format1.parse(dateString);
        } catch (ParseException e1) {
            try {
                return format2.parse(dateString);
            } catch (ParseException e2) {
                throw new ParseException("Could not parse date: " + dateString, 0);
            }
        }
    }

    public static class ReceiptData {
        private final String amount;
        private final String date;
        private final String fileName;

        public ReceiptData(String amount, String date, String fileName) {
            this.amount = amount;
            this.date = date;
            this.fileName = fileName;
        }

        public String getAmount() {
            return amount;
        }

        public String getDate() {
            return date;
        }

        public String getFileName() {
            return fileName;
        }
    }
}