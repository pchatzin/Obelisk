package com.obelisk.budget_db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class BudgetImporter implements CommandLineRunner {

    private final PdfBudgetParser pdfParser;
    private final BudgetEntryRepository repository;

    @Value("${budget.pdf.path}")
    private String pdfPath;

    @Value("${budget.csv.path}")
    private String csvPath;

    public BudgetImporter(PdfBudgetParser pdfParser,
                          BudgetEntryRepository repository) {
        this.pdfParser = pdfParser;
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println(">>> BudgetImporter starting...");

        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            System.out.println(">>> ΔΕΝ βρέθηκε PDF: " + pdfFile.getAbsolutePath());
            return;
        }

        // 1. Parse PDF
        List<BudgetEntry> entries = pdfParser.parse(pdfFile);
        System.out.println(">>> Αρχικές εγγραφές: " + entries.size());

        if (entries.isEmpty()) return;

        // 2. Sort by original lineNumber (if parser gave them)
        entries = entries.stream()
                .sorted(Comparator.comparing(e ->
                        e.getLineNumber() == null ? 0 : e.getLineNumber()))
                .collect(Collectors.toList());

        // 3. DELETE FIRST 63 ROWS
        entries = entries.stream()
                .skip(63)
                .collect(Collectors.toList());

        System.out.println(">>> Μετά το skip 63: " + entries.size());

        // 4. DELETE ROWS WHERE 5TH COLUMN CONTAINS:
        //    - Ονομασία / Ο ν ο μ α σ ί α
        //    - Σύνολο
        //    - Οικονομικό Έτος
        entries = entries.stream()
                .filter(e -> {
                    String src = safe(e.getSource()).toLowerCase(Locale.ROOT);
                    String noSpaces = src.replaceAll("\\s+", "");

                    return !noSpaces.contains("ονομασία") &&
                           !noSpaces.contains("σύνολο") &&
                           !noSpaces.contains("οικονομικόέτος");
                })
                .collect(Collectors.toList());

        System.out.println(">>> Μετά το φίλτρο Ονομασία/Σύνολο/Οικ.έτος: " + entries.size());

        // 5. RE-NUMBER FROM 1
        int i = 1;
        for (BudgetEntry e : entries) {
            e.setLineNumber(i++);
        }

        // 6. Save to DB (optional)
        repository.deleteAll();
        repository.saveAll(entries);

        // 7. Write CSV
        writeToCsv(entries, csvPath);

        System.out.println(">>> Τελικό CSV δημιουργήθηκε: " + csvPath);
    }

    private void writeToCsv(List<BudgetEntry> entries, String csvPath) throws IOException {

        Path path = Path.of(csvPath);

        Files.createDirectories(path.getParent());

        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

            // First line as comment
            writer.write("# line,type,amount,ministry,source");
            writer.newLine();

            for (BudgetEntry e : entries) {
                String line = String.join(",",
                        safe(e.getLineNumber()),
                        safe(e.getType()),
                        safeAmount(e.getAmount()),
                        escape(e.getMinistry()),
                        escape(e.getSource())
                );

                writer.write(line);
                writer.newLine();
            }
        }
    }

    private String safe(Object o) {
        return o == null ? "" : o.toString();
    }

    private String safeAmount(BigDecimal a) {
        return a == null ? "" : a.toPlainString();
    }

    // escapes " and puts field in quotes if needed
    private String escape(String s) {
        if (s == null) return "";
        s = s.replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\n") || s.contains("\r"))
            return "\"" + s + "\"";
        return s;
    }
}
