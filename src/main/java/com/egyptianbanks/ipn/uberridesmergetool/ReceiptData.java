package com.egyptianbanks.ipn.uberridesmergetool;

public  class ReceiptData {
        private final String amount;
        private final String date;
        private final String fileName;

        public ReceiptData(String amount, String date, String fileName) {
            this.amount = amount;
            this.date = date;
            this.fileName = fileName;
        }

        public String getAmount() {
            return amount;
        }

        public String getDate() {
            return date;
        }

        public String getFileName() {
            return fileName;
        }
    }