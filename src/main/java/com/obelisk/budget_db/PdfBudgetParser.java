package com.obelisk.budget_db;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PdfBudgetParser {

    private static final String MINISTRY_NAME =
            "Υπουργείο Εθνικής Οικονομίας και Οικονομικών";

    // Σελίδες του PDF που θέλουμε να αγνοούμε εντελώς
    private static final Set<Integer> EXCLUDED_PAGES = Set.of(
            1, 2, 3, 4, 5, 6, 62, 63, 64, 65, 66, 
            71, 72, 73, 74, 75, 76, 77, 78
    );

    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^(\\d{4,}\\s+)?(.+?)\\s+(\\d[\\d\\.]+)$"
    );

    public List<BudgetEntry> parse(File pdfFile) throws IOException {
    List<BudgetEntry> entries = new ArrayList<>();

    try (PDDocument document = PDDocument.load(pdfFile)) {

        int pageCount = document.getNumberOfPages();
        int lineNumber = 0;
        String currentType = null;

        for (int page = 1; page <= pageCount; page++) {

            //  Αγνόησε τελείως αυτές τις σελίδες
            if (EXCLUDED_PAGES.contains(page)) {
                continue;
            }

            //  Πάρε ΜΟΝΟ το bold κείμενο της συγκεκριμένης σελίδας
            BoldOnlyTextStripper stripper = new BoldOnlyTextStripper();
            stripper.setStartPage(page);
            stripper.setEndPage(page);

           String pageText = stripper.getText(document);
String[] lines = pageText.split("\\R");

// Επικεφαλίδα σελίδας = τίτλος Υπουργείου / Φορέα
// Παίρνουμε την πρώτη μη κενή γραμμή και τυχόν συνέχεια του τίτλου.
// Σταματάμε ΜΟΛΙΣ αρχίσουν τίτλοι τύπου "ΤΑΚΤΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ",
// "ΠΙΣΤΩΣΕΙΣ ΚΑΤΑ ΕΙΔΙΚΟ ΦΟΡΕΑ", "ΣΥΓΧΡΗΜΑΤΟΔΟΤΟΥΜΕΝΟ ΣΚΕΛΟΣ" κτλ.
StringBuilder headerBuilder = new StringBuilder();
boolean startedHeader = false;

for (String rawLine : lines) {
    String t = rawLine.trim();
    if (t.isEmpty()) {
        continue;
    }

    String upper = t.toUpperCase();

    if (!startedHeader) {
        // Πρώτη μη κενή γραμμή = αρχή τίτλου υπουργείου
        headerBuilder.append(t);
        startedHeader = true;
        continue;
    }

    // Από εδώ και κάτω: αν η γραμμή είναι τίτλος πίνακα / έτους / σκέλους, σταματάμε.
    if (t.matches(".*\\d.*")
            || upper.contains("ΠΙΣΤΩΣΕΙΣ")
            || upper.contains("ΟΙΚΟΝΟΜΙΚΟ ΕΤΟΣ")
            || upper.contains("ΚΩΔΙΚΟΣ ΦΟΡΕΑ")
            || upper.contains("ΤΑΚΤΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ")
            || upper.contains("ΣΥΓΧΡΗΜΑΤΟΔΟΤΟΥΜΕΝΟ ΣΚΕΛΟΣ")
            || upper.contains("ΕΘΝΙΚΟ ΣΚΕΛΟΣ")) {
        break;
    }

    // Διαφορετικά, είναι συνέχεια του ίδιου τίτλου (ίδιο "μέγεθος" στο PDF)
    headerBuilder.append(" ").append(t);
}

String pageHeader = headerBuilder.length() == 0 ? "-" : headerBuilder.toString();

            //  Αν η επικεφαλίδα είναι "ΕΣΟΔΑ" ή "ΕΞΟΔΑ", για τη 4η στήλη θέλουμε "-"
            String headerForColumn4;
            String headerUpper = pageHeader.toUpperCase();
            if (headerUpper.contains("ΕΣΟΔΑ") || headerUpper.contains("ΕΞΟΔΑ")) {
                headerForColumn4 = "-";
            } else {
                headerForColumn4 = pageHeader;
            }

            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.contains("ΕΣΟΔΑ")) {
                    currentType = "Έσοδα";
                    continue;
                } else if (line.contains("ΕΞΟΔΑ")) {
                    currentType = "Έξοδα";
                    continue;
                }

                if (currentType == null) {
                    // αν δεν ξέρουμε ακόμα αν είμαστε σε Έσοδα ή Έξοδα, προχωράμε
                    continue;
                }

                Matcher matcher = LINE_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    continue;
                }

                String code = matcher.group(1) != null ? matcher.group(1).trim() : "";
                String description = matcher.group(2).trim();
                String amountStr = matcher.group(3).trim();
                BigDecimal amount = parseAmount(amountStr);

                lineNumber++;

                // Πηγή = κωδικός + περιγραφή
                String source = (code + " " + description).trim();

                
                BudgetEntry entry = new BudgetEntry(
                    lineNumber,
                    currentType,
                    amount,
                    headerForColumn4,  // 4η στήλη = "-" αν Έσοδα/Έξοδα, αλλιώς η επικεφαλίδα σελίδας
                    source
                );

                entries.add(entry);
            }
        }
    }

    return entries;
}
    private BigDecimal parseAmount(String amountStr) {
        String normalized = amountStr.replace(".", "").replace(",", ".");
        return new BigDecimal(normalized);
    }

    /**
     * PDFTextStripper που κρατάει μόνο τα κομμάτια κειμένου που είναι πραγματικά "bold"
     * με βάση το όνομα της γραμματοσειράς ή/και το font weight.
     */
    private static class BoldOnlyTextStripper extends PDFTextStripper {

        BoldOnlyTextStripper() throws IOException {
            super();
            setSortByPosition(true);
        }

        private boolean isBold(List<TextPosition> textPositions) {
            for (TextPosition tp : textPositions) {
                var font = tp.getFont();
                String fontName = font.getName().toLowerCase();

                if (fontName.contains("bold")) {
                    return true;
                }

                PDFontDescriptor fd = font.getFontDescriptor();
                if (fd != null && fd.getFontWeight() > 500) { 
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
            // Αν αυτό το "κομμάτι" κειμένου δεν είναι bold, μην το γράψεις καθόλου
            if (!isBold(textPositions)) {
                return;
            }
            // Αν είναι bold
            super.writeString(text, textPositions);
        }
    }
}