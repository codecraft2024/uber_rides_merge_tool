package com.egyptianbanks.ipn.uberridesmergetool;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UberParser {

    public ReceiptData parse(String pdfPath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Extract total amount (looking for "Total EGP X.XX")
            Pattern amountPattern = Pattern.compile("Total\\s+EGP\\s+(\\d+\\.\\d{2})");
            Matcher amountMatcher = amountPattern.matcher(text);
            String amount = amountMatcher.find() ? amountMatcher.group(1) : null;

            // Extract and format date
            String formattedDate = extractAndFormatDate(text);

            if (amount == null || formattedDate == null) {
                throw new IOException("Could not find required data in the PDF");
            }

            return new ReceiptData(
                    formattedDate,
                    amount,
                    "Uber rides",
                    "development",
                    1 // Default serial number
            );
        }
    }

    private String extractAndFormatDate(String text) throws IOException {
        try {
            // First try full format (April 3, 2025)
            Pattern datePattern = Pattern.compile(
                    "(January|February|March|April|May|June|July|August|September|October|November|December)\\s+\\d{1,2},\\s+\\d{4}"
            );
            Matcher dateMatcher = datePattern.matcher(text);

            if (dateMatcher.find()) {
                String dateStr = dateMatcher.group();
                SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            }

            // Then try short format (4/3/25 11:00 PM)
            Pattern altDatePattern = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{2,4})\\s+\\d{1,2}:\\d{2}\\s+[AP]M");
            Matcher altDateMatcher = altDatePattern.matcher(text);

            if (altDateMatcher.find()) {
                String dateStr = altDateMatcher.group();
                SimpleDateFormat inputFormat = new SimpleDateFormat("M/d/yy h:mm a", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            }

            return null;
        } catch (ParseException e) {
            throw new IOException("Failed to parse date from PDF", e);
        }
    }
}
