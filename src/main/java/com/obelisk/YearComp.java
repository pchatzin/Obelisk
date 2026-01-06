package com.obelisk;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Συγκρίνει προϋπολογισμούς Ελλάδας μεταξύ δύο ετών (2020–2025).
 * Τα δεδομένα διαβάζονται από CSV αρχεία ίδιου format.
 */
public class YearComp {

    private static class YearData {
        int year;
        long totalRevenue;
        long totalExpenses;
        long balance;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueComparison = true;

        System.out.println("==================================================");
        System.out.println("ΣΥΓΚΡΙΣΗ ΠΡΟΫΠΟΛΟΓΙΣΜΩΝ ΜΕΤΑΞΥ ΕΤΩΝ");
        System.out.println("Διαθέσιμα έτη: 2020 – 2025");
        System.out.println("==================================================");

        while (continueComparison) {

            System.out.print("Δώστε πρώτο έτος (π.χ. 2020): ");
            int year1 = scanner.nextInt();

            System.out.print("Δώστε δεύτερο έτος (π.χ. 2025): ");
            int year2 = scanner.nextInt();

            try {
                YearData y1 = loadYearData(year1);
                YearData y2 = loadYearData(year2);

                printComparisonTable(y1, y2);

            } catch (IOException e) {
                System.err.println("Σφάλμα ανάγνωσης αρχείου: " + e.getMessage());
            }

            System.out.println();
            System.out.print("Θέλετε να κάνετε άλλη σύγκριση; (ΝΑΙ/ΟΧΙ): ");
            String answer = scanner.next().trim().toUpperCase();

            if (answer.equals("ΝΑΙ")) {
                continueComparison = true;
            } else if (answer.equals("ΟΧΙ")) {
                continueComparison = false;
                System.out.println("Το πρόγραμμα τερματίζει. Ευχαριστούμε!");
            } else {
                System.out.println("Παρακαλώ απαντήστε μόνο ΝΑΙ ή ΟΧΙ.");
            }
        }
    }

    // ΦΟΡΤΩΣΗ ΔΕΔΟΜΕΝΩΝ ΕΤΟΥΣ

    private static YearData loadYearData(int year) throws IOException {
        String csvPath = "budget/budget" + year + ".csv";
        Path path = Paths.get(csvPath);

        long revenue = 0L;
        long expenses = 0L;

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (first) { first = false; continue; }

                String[] f = parseCsvLine(line);

                String type = f[1].trim();
                long amount = Long.parseLong(f[2].trim());

                if ("Έσοδα".equals(type)) {
                    revenue += amount;
                } else if ("Έξοδα".equals(type)) {
                    expenses += amount;
                }
            }
        }

        YearData data = new YearData();
        data.year = year;
        data.totalRevenue = revenue;
        data.totalExpenses = expenses;
        data.balance = revenue - expenses;

        return data;
    }


    private static String[] parseCsvLine(String line) {
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        java.util.List<String> fields = new java.util.ArrayList<>();

        for (char c : line.toCharArray()) {
            if (c == '"') {
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

    // ============================================================
    // ΕΚΤΥΠΩΣΗ ΣΥΓΚΡΙΣΗΣ
    // ============================================================

    private static void printComparisonTable(YearData y1, YearData y2) {
        System.out.println();
        System.out.printf("ΣΥΓΚΡΙΣΗ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ %d vs %d%n", y1.year, y2.year);
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-25s %-15s %-15s%n", "Δείκτης", y1.year, y2.year);
        System.out.println("------------------------------------------------------------");

        System.out.printf("%-25s %-15d %-15d%n",
                "Συνολικά Έσοδα", y1.totalRevenue, y2.totalRevenue);

        System.out.printf("%-25s %-15d %-15d%n",
                "Συνολικά Έξοδα", y1.totalExpenses, y2.totalExpenses);

        System.out.printf("%-25s %-15d %-15d%n",
                "Ισοζύγιο", y1.balance, y2.balance);

        System.out.println("------------------------------------------------------------");

        printDelta("Μεταβολή Εσόδων (%)", y1.totalRevenue, y2.totalRevenue);
        printDelta("Μεταβολή Εξόδων (%)", y1.totalExpenses, y2.totalExpenses);
        printDelta("Μεταβολή Ισοζυγίου (%)", y1.balance, y2.balance);
    }

    private static void printDelta(String label, long v1, long v2) {
        double delta = ((double)(v2 - v1) / Math.abs(v1)) * 100.0;
        System.out.printf("%-25s %+.2f%%%n", label, delta);
    }
}
