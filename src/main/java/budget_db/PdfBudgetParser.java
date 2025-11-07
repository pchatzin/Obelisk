import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.util.*;
import java.math.BigDecimal;

public class PdfBudgetParser {

    public static List<BudgetEntry> parse(File file) throws Exception {
        List<BudgetEntry> entries = new ArrayList<>();
        PDDocument doc = PDDocument.load(file);
        String text = new PDFTextStripper().getText(doc);
        doc.close();

        for (String line : text.split("\\r?\\n")) {
            if (line.matches("^\\d{4}\\s+.*\\s+\\d+[.,]\\d+\\s+\\d+[.,]\\d+.*")) {
                String[] parts = line.trim().split("\\s{2,}");
                BudgetEntry entry = new BudgetEntry();
                entry.setCode(parts[0]);
                entry.setDescription(parts[1]);
                entry.setPlannedAmount(new BigDecimal(parts[2].replace(",", ".")));
                entry.setActualAmount(new BigDecimal(parts[3].replace(",", ".")));
                entry.setType(parts[0].startsWith("1") ? "REVENUE" : "EXPENDITURE");
                entry.setPeriod("2025");
                entries.add(entry);
            }
        }
        return entries;
    }
}
