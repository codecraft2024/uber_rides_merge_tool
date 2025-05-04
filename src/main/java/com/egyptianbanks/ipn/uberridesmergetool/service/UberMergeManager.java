package com.egyptianbanks.ipn.uberridesmergetool.service;


import com.egyptianbanks.ipn.uberridesmergetool.*;
import com.egyptianbanks.ipn.uberridesmergetool.util.StatusLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UberMergeManager {
    private final StatusLogger statusLogger;

    String processLog = "";

    @Autowired
    public UberMergeManager(StatusLogger statusLogger) {
        this.statusLogger = statusLogger;
    }

    public String Process(String ridesDir, String outputDir) {

        statusLogger.logStatus("App Start Process Rides, please wait ....");
        statusLogger.logStatus("Start Scanning Folder: " + ridesDir);
        try {
            List<ReceiptData> allReceipts = new PdfReader().readAll(ridesDir);

            new CSVHandler().writeToCSV(allReceipts, "/Users/minaanwer/Downloads/expenses_report.csv");

            new PDFMerge("/Users/minaanwer/Downloads").merge(allReceipts, "/Users/minaanwer/Downloads/all.pdf");
        } catch (Exception e) {
            System.err.println("Error processing receipts: " + e.getMessage());
            e.printStackTrace();
        }


        statusLogger.logStatus("App finish....");

        return processLog;

    }

}
