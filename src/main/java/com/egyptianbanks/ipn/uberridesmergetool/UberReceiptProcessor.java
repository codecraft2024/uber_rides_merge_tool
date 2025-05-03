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
            List<ReceiptData> allReceipts = new PdfReader().readAll(inputDir);

            new CSVHandler().writeToCSV(allReceipts, "/Users/minaanwer/Downloads/expenses_report.csv");

            new PDFMerge("/Users/minaanwer/Downloads").merge(allReceipts, "/Users/minaanwer/Downloads/all.pdf");
        } catch (Exception e) {
            System.err.println("Error processing receipts: " + e.getMessage());
            e.printStackTrace();
        }
    }
}