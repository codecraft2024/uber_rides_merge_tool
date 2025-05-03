package com.egyptianbanks.ipn.uberridesmergetool;

class ReceiptData {
    private final String date;
    private final String amount;
    private final String description;
    private final String department;
    private final int serialNumber;

    public ReceiptData(String date, String amount, String description,
                       String department, int serialNumber) {
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.department = department;
        this.serialNumber = serialNumber;
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
}