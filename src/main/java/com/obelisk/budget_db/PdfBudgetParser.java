package com.obelisk.budget_db;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class PdfBudgetParser {

    private static final String MINISTRY_NAME = "Υπουργείο Εθνικής Οικονομίας και Οικονομικών";

    private static final Pattern LINE_WITH_CODE = Pattern.compile("^(\\d+)\\s+(.+?)\\s+(\\d[\\d\\.]+)$");
    private static final Pattern LINE_NO_CODE = Pattern.compile("^(.+?)\\s+(\\d[\\d\\.]+)$");

    public List<BudgetEntry> parse(File pdfFile) throws IOException {
        List<BudgetEntry> entries = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(4);

            String text = stripper.getText(document);
            String[] lines = text.split("\\R");

            String currentType = null;
            int lineNumber = 0;

            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.isEmpty()) continue;

                if (line.contains("ΕΣΟΔΑ")) {
                    currentType = "Έσοδα";
                    continue;
                } else if (line.contains("ΕΞΟΔΑ")) {
                    currentType = "Έξοδα";
                    continue;
                }

                if (currentType == null) continue;

                String code = null;
                String description;
                String amountStr;

                Matcher mWithCode = LINE_WITH_CODE.matcher(line);
                if (mWithCode.matches()) {
                    code = mWithCode.group(1).trim();
                    description = mWithCode.group(2).trim();
                    amountStr = mWithCode.group(3).trim();
                } else {
                    Matcher mNoCode = LINE_NO_CODE.matcher(line);
                    if (!mNoCode.matches()) continue;
                    description = mNoCode.group(1).trim();
                    amountStr = mNoCode.group(2).trim();
                }

                BigDecimal amount = parseAmount(amountStr);
                lineNumber++;

                String source = (code != null ? code + " " : "") + description;

                BudgetEntry entry = new BudgetEntry(lineNumber, currentType, amount, MINISTRY_NAME, source);
                entries.add(entry);
            }
        }

        return entries;
    }

    private BigDecimal parseAmount(String amountStr) {
        String normalized = amountStr.replace(".", "").replace(",", ".");
        return new BigDecimal(normalized);
    }
}
