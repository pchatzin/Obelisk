package com.obelisk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Scenarios {

    // Public Static για να τη βλέπει το GUI
    public static class BudgetEntry {
        public String type;      // "Έσοδα" ή "Έξοδα"
        public long amount;
        public String ministry;
        public String source;    // Περιγραφή
        public String code;

        // Getters για το JavaFX TableView
        public String getType() { return type; }
        public long getAmount() { return amount; }
        public String getMinistry() { return ministry; }
        public String getSource() { return source; }
        public String getCode() { return code; }
        
        // Setter για αλλαγή ποσού
        public void setAmount(long amount) { this.amount = amount; }
    }

    // Φόρτωση δεδομένων από το αρχείο του 2025 (ως βάση για τα σενάρια)
    public static List<BudgetEntry> loadBaseScenario() {
        List<BudgetEntry> entries = new ArrayList<>();
        
        // Χρησιμοποιούμε το αρχείο του 2025 που βάλαμε στο προηγούμενο βήμα
        String resourcePath = "/budget/budget-2025.csv"; 

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Scenarios.class.getResourceAsStream(resourcePath), StandardCharsets.UTF_8))) {

            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (first) { first = false; continue; }

                String[] f = parseCsvLine(line);
                if (f.length >= 3) {
                    BudgetEntry entry = new BudgetEntry();
                    entry.type = f[1].trim();
                    // Καθαρισμός ποσού από τελείες/κόμματα αν χρειάζεται
                    String amountStr = f[2].trim().replace(".", ""); 
                    try {
                        entry.amount = Long.parseLong(amountStr);
                    } catch (NumberFormatException e) { continue; }

                    // Διαχείριση προαιρετικών πεδίων (αν υπάρχουν στο CSV)
                    entry.ministry = (f.length > 3) ? f[3].trim() : "-";
                    entry.source = (f.length > 4) ? f[4].trim() : "Άγνωστο";
                    
                    // Εξαγωγή κωδικού από την περιγραφή αν δεν υπάρχει ξεχωριστά
                    entry.code = extractLeadingDigits(entry.source);
                    
                    entries.add(entry);
                }
            }
        } catch (Exception e) {
            System.err.println("Σφάλμα φόρτωσης σεναρίου: " + e.getMessage());
            e.printStackTrace();
        }
        return entries;
    }

    // Ο δικός σου parser (είναι σωστός, τον κρατάμε)
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if ((c == ',' || c == ';') && !inQuotes) { // Υποστήριξη και ;
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private static String extractLeadingDigits(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) sb.append(c);
            else break;
        }
        return sb.toString();
    }
}
