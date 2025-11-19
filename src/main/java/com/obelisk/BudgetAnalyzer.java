package com.obelisk;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *  Διαβάζει το budget2025.csv και:
 *
 *  1) Φτιάχνει πίνακα τύπου Άρθρο 1 (έσοδα):
 *     - Βρίσκει όλους τους κωδικούς 2 ψηφίων (π.χ. 11, 12, 13…)
 *     - Κρατάει μόνο την πρώτη εμφάνιση κάθε 2-ψηφίου κωδικού
 *     - Υπολογίζει το σύνολο εσόδων από αυτούς τους κωδικούς
 *
 *  2) Φτιάχνει πίνακα τύπου Άρθρο 2 (έξοδα ανά Υπουργείο):
 *     - Ομαδοποιεί τις γραμμές "Έξοδα" ανά Υπουργείο (στήλη ministry)
 *     - Υπολογίζει το γενικό σύνολο εξόδων κρατικού προϋπολογισμού
 *
 *  3) Διαδραστικό μενού για επιπλέον αναλύσεις:
 *     - Επιλογή 1: Ανάλυση εσόδων / εξόδων
 *       (πίνακας με στήλες amount, ministry, source)
 *     - Επιλογή 2: Ανάλυση ανά Υπουργείο
 *       (πίνακας με στήλες type, amount, source)
 */
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
            System.out.println();

            // === 2ο ΜΕΡΟΣ: ΔΙΑΔΡΑΣΤΙΚΟ ΜΕΝΟΥ ===
            runInteractiveMenu(entries);

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
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Category c : categories.values()) {
            System.out.printf("%-5s %-70s %20d%n", c.code, c.label, c.amount);
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
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
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Map.Entry<String, Long> e : list) {
            System.out.printf("%-76s %20d%n", e.getKey(), e.getValue());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-76s %20d%n",
                "Γενικό σύνολο εξόδων κρατικού προϋπολογισμού", totalExpenses);

        return totalExpenses;
    }

    // ============================================================
    // 2ο ΜΕΡΟΣ: ΔΙΑΔΡΑΣΤΙΚΟ ΜΕΝΟΥ
    // ============================================================

    private static void runInteractiveMenu(List<Entry> entries) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================");
        System.out.println("Για περισσότερες πληροφορίες επιλέξτε μία από τις παρακάτω επιλογές:");
        System.out.println("  1 : Ανάλυση εσόδων / εξόδων");
        System.out.println("  2 : Ανάλυση ανά Υπουργείο");
        System.out.println("==================================================");

        int choice = 0;
        while (true) {
            System.out.print("Πληκτρολογήστε 1 ή 2 και πατήστε Enter: ");
            String line = scanner.nextLine().trim();
            if (line.equals("1") || line.equals("2")) {
                choice = Integer.parseInt(line);
                break;
            } else {
                System.out.println("Μη έγκυρη επιλογή. Παρακαλώ δοκιμάστε ξανά.");
            }
        }

        if (choice == 1) {
            analyzeByType(entries, scanner);
        } else {
            analyzeByMinistry(entries, scanner);
        }

        System.out.println("Τέλος αναφοράς. Ευχαριστούμε που χρησιμοποιήσατε το BudgetAnalyzer.");
    }

    /**
     * Επιλογή 1: Ανάλυση εσόδων / εξόδων.
     * Ζητάει από τον χρήστη "ΕΣΟΔΑ" ή "ΕΞΟΔΑ" και εμφανίζει πίνακα:
     * amount, ministry, source.
     */
    private static void analyzeByType(List<Entry> entries, Scanner scanner) {
        System.out.println();
        System.out.println("ΕΠΙΛΟΓΗ 1: Ανάλυση εσόδων / εξόδων");
        System.out.println("Μπορείτε να δείτε αναλυτικά όλες τις εγγραφές εσόδων ή εξόδων.");
        System.out.println();

        String userChoice;
        String expectedType;

        while (true) {
            System.out.print("Πληκτρολογήστε \"ΕΣΟΔΑ\" ή \"ΕΞΟΔΑ\" και πατήστε Enter: ");
            userChoice = scanner.nextLine().trim().toUpperCase(Locale.ROOT);

            if (userChoice.equals("ΕΣΟΔΑ")) {
                expectedType = "Έσοδα";
                break;
            } else if (userChoice.equals("ΕΞΟΔΑ")) {
                expectedType = "Έξοδα";
                break;
            } else {
                System.out.println("Μη έγκυρη τιμή. Γράψτε ακριβώς ΕΣΟΔΑ ή ΕΞΟΔΑ.");
            }
        }

        List<Entry> result = new ArrayList<>();
        for (Entry e : entries) {
            if (expectedType.equals(e.type)) {
                result.add(e);
            }
        }

        if (result.isEmpty()) {
            System.out.println("Δεν βρέθηκαν εγγραφές για τον τύπο: " + expectedType);
            return;
        }

        System.out.println();
        System.out.println("Ανάλυση για: " + expectedType);
        System.out.printf("%-20s %-76s %-60s%n", "ΠΟΣΟ (€)", "ΥΠΟΥΡΓΕΙΟ / ΦΟΡΕΑΣ", "ΠΗΓΗ");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        long sum = 0L;
        for (Entry e : result) {
            String ministry = (e.ministry == null || e.ministry.isEmpty()) ? "-" : e.ministry;
            System.out.printf("%-20d %-76s %-60s%n", e.amount, ministry, e.source);
            sum += e.amount;
        }

        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-76s %-60d%n", "ΣΥΝΟΛΟ", "", sum);
    }

    /**
     * Επιλογή 2: Ανάλυση ανά Υπουργείο.
     * Ζητάει από τον χρήστη το Υπουργείο (σε κεφαλαία) και εμφανίζει πίνακα:
     * type, amount, source.
     */
    private static void analyzeByMinistry(List<Entry> entries, Scanner scanner) {
        System.out.println();
        System.out.println("ΕΠΙΛΟΓΗ 2: Ανάλυση ανά Υπουργείο");
        System.out.println("Θα εμφανιστούν όλες οι εγγραφές για το Υπουργείο που θα δώσετε.");
        System.out.println("Γράψτε το Υπουργείο όπως εμφανίζεται στον πίνακα (με κεφαλαία).");
        System.out.println();

        // Σύνολο μοναδικών υπουργείων (για έλεγχο)
        Set<String> ministries = new HashSet<>();
        for (Entry e : entries) {
            if (e.ministry != null && !e.ministry.isEmpty() && !" -".equals(e.ministry.trim())) {
                ministries.add(e.ministry);
            }
        }

        String selectedMinistry;
        List<Entry> result;

        while (true) {
            System.out.print("Πληκτρολογήστε το Υπουργείο σε ΚΕΦΑΛΑΙΑ και πατήστε Enter: ");
            String input = scanner.nextLine().trim();

            // Βρίσκουμε υπουργείο με case-insensitive σύγκριση
            selectedMinistry = null;
            for (String m : ministries) {
                if (m.equalsIgnoreCase(input)) {
                    selectedMinistry = m;
                    break;
                }
            }

            if (selectedMinistry == null) {
                System.out.println("Δεν βρέθηκε Υπουργείο με αυτή την ονομασία.");
                System.out.println("Παρακαλώ ελέγξτε την ορθογραφία και δοκιμάστε ξανά.");
                continue;
            }

            // Φιλτράρουμε τις εγγραφές για το συγκεκριμένο υπουργείο
            result = new ArrayList<>();
            for (Entry e : entries) {
                if (selectedMinistry.equals(e.ministry)) {
                    result.add(e);
                }
            }

            if (result.isEmpty()) {
                System.out.println("Δεν βρέθηκαν εγγραφές για αυτό το Υπουργείο. Δοκιμάστε ξανά.");
            } else {
                break;
            }
        }

        System.out.println();
        System.out.println("Ανάλυση για ΥΠΟΥΡΓΕΙΟ: " + selectedMinistry);
        System.out.printf("%-10s %-20s %-60s%n", "ΤΥΠΟΣ", "ΠΟΣΟ (€)", "ΠΗΓΗ");
        System.out.println("---------------------------------------------------------------------------------------------------");

        long sum = 0L;
        for (Entry e : result) {
            System.out.printf("%-10s %-20d %-60s%n", e.type, e.amount, e.source);
            sum += e.amount;
        }

        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-20d %-60s%n", "ΣΥΝΟΛΟ", sum, "");
    }
}