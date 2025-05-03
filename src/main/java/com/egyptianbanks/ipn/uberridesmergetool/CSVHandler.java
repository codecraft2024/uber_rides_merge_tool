package com.egyptianbanks.ipn.uberridesmergetool;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

public class CSVHandler {

    public void writeToCSV(List<ReceiptData> receipts, String outputPath) throws IOException, ParseException {
        // Sort receipts by date
        receipts.sort((r1, r2) -> {
            try {
                SimpleDateFormat excelDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date1 = excelDateFormat.parse(r1.getDate());
                Date date2 = excelDateFormat.parse(r2.getDate());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                return 0; // If parsing fails, maintain original order
            }
        });

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            // Write CSV header
            writer.println("Date,Amount (EGP),Description,Department,Serial Number");

            // Write data rows
            for (ReceiptData receipt : receipts) {
                writer.printf("\"%s\",%.2f,\"%s\",\"%s\",%d%n",
                        formatDateForOutput(receipt.getDate()),
                        parseAmount(receipt.getAmount()),
                        receipt.getDescription(),
                        receipt.getDepartment(),
                        receipt.getSerialNumber());
            }
        }
    }

    private String formatDateForOutput(String excelDate) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date date = inputFormat.parse(excelDate);
        return outputFormat.format(date);
    }

    private double parseAmount(String amountStr) {
        // Handle comma as decimal separator if needed
        amountStr = amountStr.replace(",", ".");
        return Double.parseDouble(amountStr);
    }
}

