package com.egyptianbanks.ipn.uberridesmergetool;

import java.util.List;

public class UberReceiptProcessor {

    public static void main(String[] args) {

        String inputDir = "/Users/minaanwer/Downloads";
        String outputFile = "/Users/minaanwer/Downloads/output.csv";

        try {
            List<ReceiptData> allReceipts = new PdfReader(null).readAll(inputDir);

            new CSVHandler(null).writeToCSV(allReceipts, "/Users/minaanwer/Downloads/expenses_report.csv");

            new PDFMerge("/Users/minaanwer/Downloads",null).merge(allReceipts, "/Users/minaanwer/Downloads/all.pdf");
        } catch (Exception e) {
            System.err.println("Error processing receipts: " + e.getMessage());
            e.printStackTrace();
        }
    }
}