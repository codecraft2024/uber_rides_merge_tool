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
        ClassPathResource resource = new ClassPathResource("ExpensesForm.xlsx");

        try (InputStream inp = resource.getInputStream(); Workbook workbook = new XSSFWorkbook(inp)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Column indices (0-based)
            final int ACCREDIT_COL = 0;      // الاعتماد (column A)
            final int STATEMENT_COL = 1;     // بـيـان مـصـروفات
            final int AMOUNT_COL = 2;        // المبلغ
            final int DEPARTMENT_COL = 3;    // خاص بادراة
            final int DATE_COL = 4;          // التاريخ
            final int SERIAL_COL = 5;        // مسلسل
            final int HEADER_ROW_INDEX = 3;  // Excel Row 4 (0-based index)

            Row headerRow = sheet.getRow(HEADER_ROW_INDEX);
            if (headerRow == null) {
                throw new IllegalStateException("Expected header row at row 4 not found in template.");
            }

            // Get cell styles from template
            CellStyle accreditStyle = headerRow.getCell(ACCREDIT_COL) != null ?
                    headerRow.getCell(ACCREDIT_COL).getCellStyle() : workbook.createCellStyle();
            CellStyle statementStyle = headerRow.getCell(STATEMENT_COL).getCellStyle();
            CellStyle amountStyle = headerRow.getCell(AMOUNT_COL).getCellStyle();
            CellStyle deptStyle = headerRow.getCell(DEPARTMENT_COL).getCellStyle();
            CellStyle dateStyle = headerRow.getCell(DATE_COL).getCellStyle();
            CellStyle serialStyle = headerRow.getCell(SERIAL_COL).getCellStyle();

            // Ensure proper date format
            CreationHelper creationHelper = workbook.getCreationHelper();
            short dateFormat = creationHelper.createDataFormat().getFormat("dd-MMM-yyyy");
            dateStyle.setDataFormat(dateFormat);

            int rowIndex = HEADER_ROW_INDEX + 1;
            for (ReceiptData receipt : receipts) {
                Row row = sheet.createRow(rowIndex);

                // الاعتماد (cell with "     " value)
                Cell accreditCell = row.createCell(ACCREDIT_COL);
                accreditCell.setCellValue("     ");  // five spaces
                accreditCell.setCellStyle(accreditStyle);

                // بـيـان مـصـروفات
                Cell statementCell = row.createCell(STATEMENT_COL);
                statementCell.setCellValue("انتقالات اوبر");
                statementCell.setCellStyle(statementStyle);

                // المبلغ
                Cell amountCell = row.createCell(AMOUNT_COL);
                amountCell.setCellValue(parseAmount(receipt.getAmount()));
                amountCell.setCellStyle(amountStyle);

                // خاص بادراة
                Cell deptCell = row.createCell(DEPARTMENT_COL);
                deptCell.setCellValue("التطوير");
                deptCell.setCellStyle(deptStyle);

                // التاريخ
                Cell dateCell = row.createCell(DATE_COL);
                dateCell.setCellValue(parseDate(receipt.getDate()));
                dateCell.setCellStyle(dateStyle);

                // مسلسل (serial number)
                Cell serialCell = row.createCell(SERIAL_COL);
                serialCell.setCellValue(rowIndex - HEADER_ROW_INDEX);
                serialCell.setCellStyle(serialStyle);

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
