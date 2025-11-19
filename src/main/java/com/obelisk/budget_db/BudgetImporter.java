package com.obelisk.budget_db;

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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BudgetImporter implements CommandLineRunner {

    private final PdfBudgetParser pdfParser;
    private final BudgetEntryRepository repository;

    @Value("${budget.pdf.dir}")
    private String pdfDir;

    public BudgetImporter(PdfBudgetParser pdfParser, BudgetEntryRepository repository) {
        this.pdfParser = pdfParser;
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> BudgetImporter starting...");

        File dir = new File(pdfDir);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println(">>> ΔΕΝ βρέθηκε φάκελος PDF: " + dir.getAbsolutePath());
            return;
        }

        File[] pdfFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfFiles == null || pdfFiles.length == 0) {
            System.out.println(">>> Δεν βρέθηκαν PDF αρχεία στον φάκελο.");
            return;
        }

        for (File pdfFile : pdfFiles) {
            System.out.println(">>> Επεξεργασία: " + pdfFile.getName());

            List<BudgetEntry> entries = pdfParser.parse(pdfFile);
            System.out.println(">>> Αρχικές εγγραφές: " + entries.size());

            if (entries.isEmpty()) continue;

            entries = entries.stream()
                    .sorted(Comparator.comparing(e -> e.getLineNumber() == null ? 0 : e.getLineNumber()))
                    .filter(e -> {
                        String src = safe(e.getSource()).toLowerCase(Locale.ROOT);
                        String noSpaces = src.replaceAll("\\s+", "");
                        return !noSpaces.contains("ονομασία")
                                && !noSpaces.contains("σύνολο")
                                && !noSpaces.contains("οικονομικόέτος");
                    })
                    .collect(Collectors.toList());

            System.out.println(">>> Μετά το φίλτρο Ονομασία/Σύνολο/Οικ.έτος: " + entries.size());

            int i = 1;
            for (BudgetEntry e : entries) {
                e.setLineNumber(i++);
            }

            repository.deleteAll();
            repository.saveAll(entries);
            Path budgetFolder = Path.of("budget");
            Files.createDirectories(budgetFolder);

            String baseName = pdfFile.getName().replace(".pdf", ".csv");
            Path csvPath = budgetFolder.resolve(baseName);

            writeToCsv(entries, csvPath);

            System.out.println(">>> Τελικό CSV δημιουργήθηκε: " + csvPath);
        }
    }

    private void writeToCsv(List<BudgetEntry> entries, Path csvPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
            writer.write("# line,type,amount,ministry,source");
            writer.newLine();

            for (BudgetEntry e : entries) {
                String ministry = e.getMinistry();
                if (ministry == null || ministry.isBlank()) {
                    ministry = "-";
                }

                String line = String.join(",",
                        safe(e.getLineNumber()),
                        safe(e.getType()),
                        safeAmount(e.getAmount()),
                        escape(ministry),
                        escape(e.getSource())
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private String safe(Object o) { return o == null ? "" : o.toString(); }
    private String safeAmount(BigDecimal a) { return a == null ? "" : a.toPlainString(); }
    private String escape(String s) {
        if (s == null) return "";
        s = s.replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\n") || s.contains("\r"))
            return "\"" + s + "\"";
        return s;
    }
}
