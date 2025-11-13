package com.obelisk.budget_db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
        File pdfFile = new File(pdfPath);

        if (!pdfFile.exists()) {
            System.err.println("Δεν βρέθηκε το PDF του προϋπολογισμού στο: " + pdfFile.getAbsolutePath());
            return;
        }

        System.out.println("Διαβάζω PDF: " + pdfFile.getAbsolutePath());

        List<BudgetEntry> entries = pdfParser.parse(pdfFile);

        System.out.println("Βρέθηκαν " + entries.size() + " εγγραφές προϋπολογισμού.");

        // Αποθήκευση στη βάση
        repository.saveAll(entries);

        // Εξαγωγή σε CSV
        writeToCsv(entries, csvPath);

        System.out.println("Τα δεδομένα αποθηκεύτηκαν στο CSV: " + csvPath);
    }

    private void writeToCsv(List<BudgetEntry> entries, String csvPath) throws IOException {
        Path path = Path.of(csvPath);

        // Δημιουργεί τον φάκελο budget αν δεν υπάρχει
        Files.createDirectories(path.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            // Header
            writer.write("line,type,amount,ministry,source");
            writer.newLine();

            for (BudgetEntry entry : entries) {
                String line = String.join(",",
                        safe(entry.getLineNumber()),
                        safe(entry.getType()),
                        safe(entry.getAmount()),
                        safe(entry.getMinistry()),
                        safeCsv(entry.getSource())
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    /**
     * Αν η περιγραφή έχει κόμματα, τη βάζουμε σε εισαγωγικά
     */
    private String safeCsv(String value) {
        if (value == null) {
            return "";
        }
        String v = value.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v + "\"";
        }
        return v;
    }
}
