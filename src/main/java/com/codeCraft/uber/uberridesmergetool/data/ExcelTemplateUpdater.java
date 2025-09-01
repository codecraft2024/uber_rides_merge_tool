package com.codeCraft.uber.uberridesmergetool.data;

import com.codeCraft.uber.uberridesmergetool.domain.model.ReceiptData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExcelTemplateUpdater {

    public void fillTemplate(List<ReceiptData> receipts, String outputPath) throws IOException, ParseException {
        // Load Excel template from resources
        ClassPathResource resource = new ClassPathResource("ExpensesForm2.xlsx");

        try (InputStream inp = resource.getInputStream(); Workbook workbook = new XSSFWorkbook(inp)) {
            Sheet sheet = workbook.getSheetAt(0);

            // --- Fill static fields ---
            // Place value in the cell immediately to the right of the label cell

            // الإسم (find label in B3, value in C3)
            Row rowName = sheet.getRow(2);
            if (rowName == null) rowName = sheet.createRow(2);
            Cell cellName = rowName.getCell(2); // C3 (index 2)
            if (cellName == null) cellName = rowName.createCell(2);
            cellName.setCellValue("مينا انور لويز");

            // الرقم الوظيفى (label in B4, value in C4)
            Row rowJobNum = sheet.getRow(3);
            if (rowJobNum == null) rowJobNum = sheet.createRow(3);
            Cell cellJobNum = rowJobNum.getCell(2); // C4 (index 2)
            if (cellJobNum == null) cellJobNum = rowJobNum.createCell(2);
            cellJobNum.setCellValue("281");

            // الشهر (label in B5, value in C5)
            Row rowMonth = sheet.getRow(4);
            if (rowMonth == null) rowMonth = sheet.createRow(4);
            Cell cellMonth = rowMonth.getCell(2); // C5 (index 2)
            if (cellMonth == null) cellMonth = rowMonth.createCell(2);
            if (!receipts.isEmpty()) {
                String dateStr = receipts.get(0).getDate();
                java.util.Date date = parseDate(dateStr);
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", new java.util.Locale("ar"));
                cellMonth.setCellValue(monthFormat.format(date));
            }

            // الإدارة (label in F3, value in G3)
            Cell cellDept = rowName.getCell(6); // G3 (index 6)
            if (cellDept == null) cellDept = rowName.createCell(6);
            cellDept.setCellValue("development");

            // القطاع (label in F4, value in G4)
            Cell cellSector = rowJobNum.getCell(6); // G4 (index 6)
            if (cellSector == null) cellSector = rowJobNum.createCell(6);
            cellSector.setCellValue("mobile team");

            // Data starts at row 8 (index 7) and column B (index 1)
            final int START_ROW_INDEX = 7;
            final int START_COL_INDEX = 1;

            // Column offsets for new arrangement:
            final int DATE_COL = 0;           // التاريخ (B)
            final int FROM_LOC_COL = 1;       // مكان بدء الرحلة (C)
            final int TO_LOC_COL = 2;         // مكان الوصول (D)
            final int AMOUNT_COL = 3;         // القيمة (E)
            final int REASON_COL = 4;         // سبب الانتقال (F)
            final int PAYMENT_COL = 5;        // طريقة الدفع (G)

            Row headerRow = sheet.getRow(START_ROW_INDEX);
            if (headerRow == null) {
                throw new IllegalStateException("Expected header row at row 8 not found in template.");
            }

            // Get cell styles from template, use default style if cell is null
            CellStyle dateStyle = headerRow.getCell(START_COL_INDEX + DATE_COL) != null ?
                    headerRow.getCell(START_COL_INDEX + DATE_COL).getCellStyle() : workbook.createCellStyle();
            CellStyle fromLocStyle = headerRow.getCell(START_COL_INDEX + FROM_LOC_COL) != null ?
                    headerRow.getCell(START_COL_INDEX + FROM_LOC_COL).getCellStyle() : workbook.createCellStyle();
            CellStyle toLocStyle = headerRow.getCell(START_COL_INDEX + TO_LOC_COL) != null ?
                    headerRow.getCell(START_COL_INDEX + TO_LOC_COL).getCellStyle() : workbook.createCellStyle();
            CellStyle amountStyle = headerRow.getCell(START_COL_INDEX + AMOUNT_COL) != null ?
                    headerRow.getCell(START_COL_INDEX + AMOUNT_COL).getCellStyle() : workbook.createCellStyle();
            CellStyle reasonStyle = headerRow.getCell(START_COL_INDEX + REASON_COL) != null ?
                    headerRow.getCell(START_COL_INDEX + REASON_COL).getCellStyle() : workbook.createCellStyle();
            CellStyle paymentStyle = headerRow.getCell(START_COL_INDEX + PAYMENT_COL) != null ?
                    headerRow.getCell(START_COL_INDEX + PAYMENT_COL).getCellStyle() : workbook.createCellStyle();

            // Ensure proper date format
            CreationHelper creationHelper = workbook.getCreationHelper();
            short dateFormat = creationHelper.createDataFormat().getFormat("dd-MMM-yyyy");
            dateStyle.setDataFormat(dateFormat);

            int rowIndex = START_ROW_INDEX;
            for (ReceiptData receipt : receipts) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }

                // التاريخ
                Cell dateCell = row.createCell(START_COL_INDEX + DATE_COL);
                dateCell.setCellValue(parseDate(receipt.getDate()));
                dateCell.setCellStyle(dateStyle);

                // مكان بدء الرحلة
                Cell fromLocCell = row.createCell(START_COL_INDEX + FROM_LOC_COL);
                fromLocCell.setCellValue(shortenAddress(receipt.getFromLocation(), true, receipt));
                fromLocCell.setCellStyle(fromLocStyle);

                // مكان الوصول
                Cell toLocCell = row.createCell(START_COL_INDEX + TO_LOC_COL);
                toLocCell.setCellValue(shortenAddress(receipt.getToLocation(), false, receipt));
                toLocCell.setCellStyle(toLocStyle);

                // القيمة
                Cell amountCell = row.createCell(START_COL_INDEX + AMOUNT_COL);
                amountCell.setCellValue(parseAmount(receipt.getAmount()));
                amountCell.setCellStyle(amountStyle);

                // سبب الانتقال
                Cell reasonCell = row.createCell(START_COL_INDEX + REASON_COL);
                reasonCell.setCellValue("الذهاب/الرجوع من العمل");
                reasonCell.setCellStyle(reasonStyle);

                // طريقة الدفع
                Cell paymentCell = row.createCell(START_COL_INDEX + PAYMENT_COL);
                paymentCell.setCellValue("uber wallet");
                paymentCell.setCellStyle(paymentStyle);

                rowIndex++;
            }

            // Save to output path
            try (OutputStream out = new FileOutputStream(outputPath)) {
                workbook.write(out);
            }
        }
    }

    // Helper to shorten address and annotate as (عمل) or (بيت)
    private String shortenAddress(String address, boolean isFrom, ReceiptData receipt) {
        if (address == null) return "";
        String shortAddr = address;
        // Extract up to first comma or just the street/number
        int idx = address.indexOf(',');
        if (idx > 0) {
            shortAddr = address.substring(0, idx).trim();
        } else {
            String[] parts = address.split(" ");
            if (parts.length > 3) {
                shortAddr = String.join(" ", parts[0], parts[1], parts[2]);
            }
        }

        String annotation = isWorkAddress(address) ? "(عمل)" : "(بيت)";

        // If Arabic, insert annotation first, then address
        if (containsArabic(shortAddr)) {
            return annotation + " " + shortAddr;
        } else {
            // For English or mixed, append at the end
            return shortAddr + " " + annotation;
        }
    }

    private boolean isWorkAddress(String address) {
        if (address == null) return false;
        String lower = address.toLowerCase();
        return lower.contains("n teseen");
    }

    private boolean containsArabic(String s) {
        if (s == null) return false;
        // Use regex to check for any Arabic Unicode character
        return s.matches(".*[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF]+.*");
    }

    private java.util.Date parseDate(String dateStr) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
    }

    private double parseAmount(String amountStr) {
        return Double.parseDouble(amountStr.replace(",", "."));
    }
}
