package com.egyptianbanks.ipn.uberridesmergetool.domain.model;

public class ReceiptData {
    private final String date;
    private final String amount;
    private final String description;
    private final String department;
    private final int serialNumber;



    private final String fileName;

    public ReceiptData(String date, String amount, String description,
                       String department, int serialNumber, String fileName) {
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.department = department;
        this.serialNumber = serialNumber;
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDepartment() {
        return department;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getFileName() {
        return fileName;
    }

}