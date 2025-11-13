package com.obelisk.budget_db;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PdfBudgetParser {

    private static final String MINISTRY_NAME =
            "Υπουργείο Εθνικής Οικονομίας και Οικονομικών";

    // Regex: [optional code] [description] [amount]
    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^(\\d{4,}\\s+)?(.+?)\\s+(\\d[\\d\\.]+)$"
    );

    public List<BudgetEntry> parse(File pdfFile) throws IOException {
        List<BudgetEntry> entries = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(4); // ✅ Start from page 4
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

                Matcher matcher = LINE_PATTERN.matcher(line);
                if (!matcher.matches()) continue;

                String code = matcher.group(1) != null ? matcher.group(1).trim() : "";
                String description = matcher.group(2).trim();
                String amountStr = matcher.group(3).trim();
                BigDecimal amount = parseAmount(amountStr);

                lineNumber++;

                // ✅ Combine code + description into source
                String source = (code + " " + description).trim();

                BudgetEntry entry = new BudgetEntry(
                        lineNumber,
                        currentType,
                        amount,
                        MINISTRY_NAME,
                        source
                );

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
