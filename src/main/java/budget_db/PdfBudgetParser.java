package budget_db;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfBudgetParser {

    public static List<BudgetEntry> parse(File file) throws Exception {
        List<BudgetEntry> entries = new ArrayList<>();

        if (!file.exists()) {
            System.out.println("PDF file not found: " + file.getAbsolutePath());
            return entries;
        }

        PDDocument doc = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(doc);
        doc.close();

        System.out.println("Extracted text from PDF.");

        for (String line : text.split("\\r?\\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (!line.matches(".*\\d+[.,]\\d+.*")) continue;
            if (line.toLowerCase().contains("σύνολο")) continue;

            String[] tokens = line.split("\\s+");
            if (tokens.length < 3) continue;

            try {
                String code = tokens[0];
                String amountStr = tokens[tokens.length - 1].replace(".", "").replace(",", ".");
                BigDecimal amount = new BigDecimal(amountStr);

                StringBuilder descriptionBuilder = new StringBuilder();
                for (int i = 1; i < tokens.length - 1; i++) {
                    descriptionBuilder.append(tokens[i]).append(" ");
                }
                String description = descriptionBuilder.toString().trim();

                BudgetEntry entry = new BudgetEntry();
                entry.setCode(code);
                entry.setDescription(description);
                entry.setPlannedAmount(amount);
                entry.setActualAmount(null);
                entry.setType(code.startsWith("1") ? "REVENUE" : "EXPENDITURE");
                entry.setPeriod("2025");
                entries.add(entry);
            } catch (Exception e) {
                System.out.println("Failed to parse line: " + line);
                e.printStackTrace();
            }
        }

        System.out.println("Parsed entries: " + entries.size());
        return entries;
    }
}
