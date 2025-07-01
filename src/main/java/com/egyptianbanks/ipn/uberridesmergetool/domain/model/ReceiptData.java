package com.egyptianbanks.ipn.uberridesmergetool.domain.model;

public class ReceiptData {
    private final String date;
    private final String amount;
    private final String description;
    private final String department;
    private final int serialNumber;
    private final String fileName;
    private final String fromLocation;
    private final String toLocation;

    public ReceiptData(String date, String amount, String description,
                       String department, int serialNumber, String fileName,
                       String fromLocation, String toLocation) {
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.department = department;
        this.serialNumber = serialNumber;
        this.fileName = fileName;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
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

    public String getFromLocation() {
        return fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public static String csvHeader() {
        return "Date,Amount,Description,Department,SerialNumber,FromLocation,ToLocation";
    }

    public String toCsvRow() {
        return String.format("%s,%s,%s,%s,%d,%s,%s",
                date,
                amount,
                description,
                department,
                serialNumber,
                fromLocation,
                toLocation);
    }

}