package com.egyptianbanks.ipn.uberridesmergetool.data;

import com.egyptianbanks.ipn.uberridesmergetool.domain.model.ReceiptData;
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
                fromLocCell.setCellValue(receipt.getFromLocation());
                fromLocCell.setCellStyle(fromLocStyle);

                // مكان الوصول
                Cell toLocCell = row.createCell(START_COL_INDEX + TO_LOC_COL);
                toLocCell.setCellValue(receipt.getToLocation());
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

    private java.util.Date parseDate(String dateStr) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
    }

    private double parseAmount(String amountStr) {
        return Double.parseDouble(amountStr.replace(",", "."));
    }
}
