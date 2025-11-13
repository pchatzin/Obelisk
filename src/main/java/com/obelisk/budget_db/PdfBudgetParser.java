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

    // Regex: [προαιρετικός κωδικός] [περιγραφή] [ποσό]
    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^(\\d{4,}\\s+)?(.+?)\\s+(\\d[\\d\\.]+)$"
    );

    public List<BudgetEntry> parse(File pdfFile) throws IOException {
        List<BudgetEntry> entries = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            String[] lines = text.split("\\R");

            String currentType = null; // "Εσοδα" ή "Εξοδα"
            int lineNumber = 0;

            for (String rawLine : lines) {
                String line = rawLine.trim();

                if (line.isEmpty()) {
                    continue;
                }

                // Ανάλογα με τις επικεφαλίδες στο PDF
                if (line.contains("ΕΣΟΔΑ")) {
                    currentType = "Έσοδα";
                    continue;
                } else if (line.contains("ΕΞΟΔΑ")) {
                    currentType = "Έξοδα";
                    continue;
                }

                // Αν δεν έχει ακόμα οριστεί τύπος, προχώρα
                if (currentType == null) {
                    continue;
                }

                Matcher matcher = LINE_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    // Γραμμή που δεν μοιάζει με [κωδικός] [περιγραφή] [ποσό], την αγνοούμε
                    continue;
                }

                String description = matcher.group(2).trim();
                String amountStr = matcher.group(3).trim();

                BigDecimal amount = parseAmount(amountStr);

                lineNumber++;

                BudgetEntry entry = new BudgetEntry(
                        lineNumber,
                        currentType,
                        amount,
                        MINISTRY_NAME,
                        description
                );

                entries.add(entry);
            }
        }

        return entries;
    }

    /**
     * Μετατρέπει 3.000.000 σε BigDecimal("3000000")
     */
    private BigDecimal parseAmount(String amountStr) {
        String normalized = amountStr.replace(".", "").replace(",", ".");
        return new BigDecimal(normalized);
    }
}
