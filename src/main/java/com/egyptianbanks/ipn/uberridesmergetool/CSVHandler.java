package com.egyptianbanks.ipn.uberridesmergetool;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CSVHandler {

    public  void writeToCSV(List<ReceiptData> receipts, String outputPath) throws IOException {
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

    private  Date parseDate(String dateString, SimpleDateFormat format1, SimpleDateFormat format2)
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
}
