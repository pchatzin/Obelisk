package com.obelisk;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class exceltocsv {

    public static void main(String[] args) {
        // Input Excel file path (matches your tree)
        String excelFile = "src/main/resources/Country comp.xlsx";
        // Output CSV
        String csvFile = "countries/Country_comp.csv";

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis);
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"))) {

            // Evaluator and formatter to mimic Excel display
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter formatter = new DataFormatter(); // respects cell formatting

            Sheet sheet = workbook.getSheetAt(0);

            // 1) Determine the header row and total columns
            // If the first real data row is the one starting with "1" (Line=1), we can derive width
            // by scanning the widest row:
            int maxCols = 0;
            for (Row r : sheet) {
                if (r != null) {
                    maxCols = Math.max(maxCols, r.getLastCellNum()); // lastCellNum is 1-based
                }
            }
            if (maxCols <= 0) {
                System.out.println("No columns detected. Exiting.");
                return;
            }

            // 2) Write the CSV header (commented)
            String header = "Line,Country,Population (mill),Per Capita Spending,Currency,Total Revenue,Revenue % GDP,Total Expenditure,Expenditure % GDP,Balance,Balance % GDP,GDP,Public Dept,Dept % GDP,Interest Payments,Debt Service % Revenue,Direct Taxes,Direct Taxes Share to Revenue,Indirect Taxes,Indirect Taxes Share to Revenue,Non-Tax Share,Non-tax Share to Revenue,Health Exp.,Health Share,EducationExp.,Education Share,Defense Exp.,Defense Share,Infrastructure Exp.,Infrastructure Share,Investments,Investments Share";
            writer.write("# " + header);
            writer.newLine();

            // 3) Iterate each row and keep column positions stable
            int rowIndex = 0;
            for (Row row : sheet) {
                if (row == null) continue;

                List<String> cells = new ArrayList<>(maxCols);

                for (int c = 0; c < maxCols; c++) {
                    Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    // Evaluate formulas to their resulting type/value
                    CellType type = cell.getCellType();
                    if (type == CellType.FORMULA) {
                        type = evaluator.evaluateFormulaCell(cell);
                    }

                    // Use formatter for user-facing value, but strip commas to keep CSV safe
                    String text = formatter.formatCellValue(cell, evaluator);

                    // Normalize commas inside to avoid breaking CSV
                    if (text != null) {
                        text = text.replace(",", "");
                    } else {
                        text = "";
                    }

                    // Optionally trim scientific notation to a consistent plain form if needed
                    // (kept as formatted by Excel for now)

                    cells.add(text);
                }

                // Build the CSV line
                String line = String.join(",", cells);

                // Skip obvious “vertical header” lines that aren’t data rows:
                boolean looksLikeData =
                        row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() == CellType.NUMERIC;

                if (looksLikeData) {
                    writer.write(line);
                    writer.newLine();
                }

                rowIndex++;
            }

            System.out.println("Conversion complete: " + csvFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
