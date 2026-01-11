package com.obelisk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Scenarios {

    public static class BudgetEntry {
        public String type;
        public long amount;
        public String ministry;
        public String source;
        public String code;

        public BudgetEntry() {}

        public String getType() { return type; }
        public long getAmount() { return amount; }
        public String getMinistry() { return ministry; }
        public String getSource() { return source; }
        public String getCode() { return code; }
    }

    private List<BudgetEntry> originalEntries;
    public List<BudgetEntry> modifiedEntries; 

    public Scenarios() {
        this.originalEntries = loadBaseData();
        this.modifiedEntries = new ArrayList<>();
        for (BudgetEntry entry : originalEntries) {
            BudgetEntry newEntry = new BudgetEntry();
            newEntry.type = entry.type;
            newEntry.amount = entry.amount;
            newEntry.ministry = entry.ministry;
            newEntry.source = entry.source;
            newEntry.code = entry.code;
            this.modifiedEntries.add(newEntry);
        }
    }

    public boolean updateEntryAmount(String code, long newAmount) {
        List<BudgetEntry> entries = modifiedEntries.stream()
                .filter(e -> code.equals(e.code))
                .collect(Collectors.toList());

        if (entries.isEmpty()) return false;

        BudgetEntry first = entries.get(0);
        String desc = first.source.replace(code, "").trim();
        String ministry = first.ministry;
        String type = first.type;

        modifiedEntries.removeAll(entries);

        BudgetEntry updated = new BudgetEntry();
        updated.type = type;
        updated.amount = newAmount;
        updated.ministry = ministry;
        updated.source = code + " " + desc;
        updated.code = code;

        modifiedEntries.add(updated);
        return true;
    }

    public void addNewEntry(String type, String ministry, String code, String description, long amount) {
        BudgetEntry newEntry = new BudgetEntry();
        newEntry.type = type;
        newEntry.amount = amount;
        newEntry.ministry = ministry; 
        newEntry.source = code + " " + description;
        newEntry.code = code;
        modifiedEntries.add(newEntry);
    }

    public void transferExpenses(String fromMinistry, String toMinistry, String description, long amount) {
        BudgetEntry reduction = new BudgetEntry();
        reduction.type = "Έξοδα"; 
        reduction.amount = -amount;
        reduction.ministry = fromMinistry;
        reduction.source = "Μεταφορά προς " + toMinistry + ": " + description;
        reduction.code = "TRF-OUT";

        BudgetEntry addition = new BudgetEntry();
        addition.type = "Έξοδα";
        addition.amount = amount;
        addition.ministry = toMinistry;
        addition.source = "Μεταφορά από " + fromMinistry + ": " + description;
        addition.code = "TRF-IN";

        modifiedEntries.add(reduction);
        modifiedEntries.add(addition);
    }


    public long getTotalRevenue() {
        return modifiedEntries.stream()
                .filter(e -> "Έσοδα".equals(e.type))
                .mapToLong(e -> e.amount).sum();
    }

    public long getTotalExpenses() {
        return modifiedEntries.stream()
                .filter(e -> "Έξοδα".equals(e.type)) 
                .mapToLong(e -> e.amount).sum();
    }

    public long getBalance() {
        return getTotalRevenue() - getTotalExpenses();
    }

    
    private List<BudgetEntry> loadBaseData() {
        List<BudgetEntry> entries = new ArrayList<>();
        String path = "/budget/budget-2025.csv"; 

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(path), StandardCharsets.UTF_8))) {
            
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (first) { first = false; continue; }

                String[] f = parseCsvLine(line);
                if (f.length >= 3) {
                    BudgetEntry entry = new BudgetEntry();
                    entry.type = f[1].trim();
                    try {
                        entry.amount = Long.parseLong(f[2].trim().replace(".", ""));
                    } catch (Exception e) { continue; }
                    
                    entry.ministry = (f.length > 3) ? f[3].trim() : "-";
                    entry.source = (f.length > 4) ? f[4].trim() : "";
                    entry.code = extractLeadingDigits(entry.source);
                    entries.add(entry);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    private String[] parseCsvLine(String line) {

        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') inQuotes = !inQuotes;
            else if ((c == ',' || c == ';') && !inQuotes) { 
                fields.add(current.toString());
                current.setLength(0);
            } else current.append(c);
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private String extractLeadingDigits(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) sb.append(c);
            else break;
        }
        return sb.toString();
    }
}
