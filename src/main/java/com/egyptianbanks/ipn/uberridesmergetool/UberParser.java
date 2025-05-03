package com.egyptianbanks.ipn.uberridesmergetool;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UberParser {

    public static ReceiptData parse(String pdfPath) throws IOException {
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

}
