package com.obelisk;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BudgetAnalyzer {

    // Μοντέλο μιας γραμμής του CSV
    private static class Entry {
        String type;      // "Έσοδα" ή "Έξοδα"
        long amount;      // ποσό
        String ministry;  // Υπουργείο ή "-" για έσοδα
        String source;    // πλήρης περιγραφή π.χ. "11 Φόροι"
        String code;      // κωδικός στην αρχή της source (μόνο τα ψηφία)
    }

    // Για τις κατηγορίες 2-ψηφίων κωδικών (Άρθρο 1)
    private static class Category {
        String code;
        String label;
        long amount;
    }

    public static void main(String[] args) {
        // Default διαδρομή αρχείου, όπως είναι στο tree σου
        String csvPath = "budget/budget2025.csv";
        if (args.length > 0) {
            csvPath = args[0];
        }

        try {
            List<Entry> entries = loadEntries(csvPath);

            // ΠΙΝΑΚΑΣ ΑΡΘΡΟ 1 – μόνο έσοδα
            System.out.println("==================================================");
            System.out.println("ΠΙΝΑΚΑΣ 1 - ΕΣΟΔΑ");
            System.out.println("==================================================");
            long totalRevenue = generateArticle1(entries);

            // ΠΙΝΑΚΑΣ ΑΡΘΡΟ 2 – έξοδα ανά Υπουργείο
            System.out.println();
            System.out.println("==================================================");
            System.out.println("ΠΙΝΑΚΑΣ 2 - ΕΞΟΔΑ ΑΝΑ ΥΠΟΥΡΓΕΙΟ");
            System.out.println("==================================================");
            long totalExpenses = generateArticle2(entries);

            // Τελικό αποτέλεσμα
            long result = totalRevenue - totalExpenses;
            String status;
            if (result > 0) {
                status = "ΠΛΕΟΝΑΣΜΑΤΙΚΟΣ";
            } else if (result < 0) {
                status = "ΕΛΛΕΙΜΜΑΤΙΚΟΣ";
            } else {
                status = "ΙΣΟΖΥΓΙΣΜΕΝΟΣ";
            }

            System.out.println();
            System.out.printf("Αποτέλεσμα (έσοδα - έξοδα): %d%n", result);
            System.out.println("Ο κρατικός προϋπολογισμός είναι: " + status);

        } catch (IOException e) {
            System.err.println("Σφάλμα κατά την ανάγνωση του αρχείου: " + e.getMessage());
        }
    }

    /**
     * Φορτώνει όλες τις γραμμές του CSV σε μια λίστα Entry.
     */
    private static List<Entry> loadEntries(String csvFile) throws IOException {
        List<Entry> entries = new ArrayList<>();

        Path path = Paths.get(csvFile);
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                // Παράκαμψη κενών γραμμών
                if (line.trim().isEmpty()) continue;

                // Παράκαμψη header
                if (first) {
                    first = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);
                if (fields.length != 5) {
                    // Αν για κάποιο λόγο δεν έχουμε 5 στήλες, αγνόησέ τη γραμμή
                    continue;
                }

                Entry e = new Entry();
                // fields[0] = "# line" (δεν μας ενδιαφέρει εδώ)
                e.type = fields[1].trim();
                e.amount = Long.parseLong(fields[2].trim());
                e.ministry = fields[3].trim();
                e.source = fields[4].trim();
                e.code = extractLeadingDigits(e.source);

                entries.add(e);
            }
        }

        return entries;
    }

    /**
     * Parser για CSV γραμμή με υποστήριξη για πεδία σε quotes (που μπορεί να έχουν κόμματα).
     * Π.χ. 120,Έσοδα,658000000,-,"1150602 Φόρος ..., συμφωνιών"
     */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Απλά γυρνάμε το flag, δεν κρατάμε τα quotes στο αποτέλεσμα
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());

        return fields.toArray(new String[0]);
    }

    /**
     * Επιστρέφει τα συνεχόμενα ψηφία από την αρχή ενός string.
     * Π.χ. "11 Φόροι" -> "11"
     *      "1003-501-0000000 ..." -> "1003"
     */
    private static String extractLeadingDigits(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Δημιουργεί και εμφανίζει τον πίνακα τύπου Άρθρο 1.
     * - Κωδικοί 2 ψηφίων από τις γραμμές "Έσοδα"
     * - Κρατάμε μόνο την πρώτη εμφάνιση κάθε 2-ψηφίου κωδικού
     * - Επιστρέφει το σύνολο εσόδων.
     */
    private static long generateArticle1(List<Entry> entries) {
        // LinkedHashMap για να κρατήσουμε τη σειρά εμφάνισης
        Map<String, Category> categories = new LinkedHashMap<>();
        Set<String> seenRevenueCodes = new HashSet<>();

        long totalRevenue = 0L;

        for (Entry e : entries) {
            if (!"Έσοδα".equals(e.type)) {
                continue;
            }

            // Μόνο 2-ψηφιοι κωδικοί και μόνο την πρώτη φορά που τους βλέπουμε
            if (e.code != null && e.code.length() == 2 && !seenRevenueCodes.contains(e.code)) {
                seenRevenueCodes.add(e.code);

                Category c = new Category();
                c.code = e.code;
                String label = e.source.substring(e.code.length()).trim();
                c.label = label;
                c.amount = e.amount;
                categories.put(c.code, c);

                totalRevenue += e.amount;
            }
        }

        System.out.printf("%-5s %-70s %20s%n", "ΚΩΔ", "ΠΕΡΙΓΡΑΦΗ", "ΠΟΣΟ (€)");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (Category c : categories.values()) {
            System.out.printf("%-5s %-70s %20d%n", c.code, c.label, c.amount);
        }
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-76s %20d%n", "Σύνολο εσόδων", totalRevenue);

        return totalRevenue;
    }

    /**
     * Δημιουργεί και εμφανίζει τον πίνακα τύπου Άρθρο 2:
     * - Σύνολο εξόδων ανά Υπουργείο (στήλη ministry)
     * - Επιστρέφει το γενικό σύνολο εξόδων.
     */
    private static long generateArticle2(List<Entry> entries) {
        Map<String, Long> expensesByMinistry = new HashMap<>();
        long totalExpenses = 0L;

        for (Entry e : entries) {
            if (!"Έξοδα".equals(e.type)) continue;

            String ministry = (e.ministry == null || e.ministry.isEmpty()) ? "-" : e.ministry;
            expensesByMinistry.merge(ministry, e.amount, Long::sum);
            totalExpenses += e.amount;
        }

        // Ταξινόμηση αλφαβητικά ανά Υπουργείο
        List<Map.Entry<String, Long>> list =
                new ArrayList<>(expensesByMinistry.entrySet());
        list.sort(Map.Entry.comparingByKey());

        System.out.printf("%-60s %20s%n", "ΥΠΟΥΡΓΕΙΟ / ΦΟΡΕΑΣ", "ΠΟΣΟ ΕΞΟΔΩΝ (€)");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (Map.Entry<String, Long> e : list) {
            System.out.printf("%-60s %20d%n", e.getKey(), e.getValue());
        }
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-60s %20d%n",
                "Γενικό σύνολο εξόδων κρατικού προϋπολογισμού", totalExpenses);

        return totalExpenses;
    }
}