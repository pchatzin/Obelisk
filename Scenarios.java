package com.obelisk;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Διαχειρίζεται τις αλλαγές στον προυπολογισμό
 * Ο χρήστης μπορεί να δει προσομοιώσεις των αλλαγών του στα έσοδα,έξοδα και Υπουργεία
 * αλλα και να δει τα αποτελέσματα στον τελικό προυπολογισμό
 */

public class Scenarios {

    private List<BudgetAnalyzer.Entry> originalEntries;
    private List<BudgetAnalyzer.Entry>modifiedEntries;
    private Scanner scanner;

    public Scenarios(List<BudgetAnalyzer.Entry>entries) {
        this.originalEntries = new ArrayList<>(entries);
        this.modifiedEntries = new ArrayList<>(entries);
        this.scanner = new Scanner(System.in);
    }

    public void runScenarios() {
        System.out.println();
        System.out.println("=============================================================");
        System.out.println("ΑΛΛΑΓΕΣ ΣΤΟΝ ΠΡΟΥΠΟΛΟΓΙΣΜΟ");
        System.out.println("=============================================================");

        if(askForChanges()) {
            System.out.println("Τερματισμός προγράμματος");
            return;
        }

        boolean continueChanges = true;
        while (continueChanges) {
            showChanges();
            continueChanges = askForChanges();
        }

        showResults();
    }

    private boolean askForChanges() {

        while (true) {
            System.out.println("Θέλετε να κένετε αλλαγές στον προυπολογισμό; (Απαντήστε με ΝΑΙ ή ΟΧΙ): ");
            String answer = scanner.nextLine().trim().toUpperCase(Locale.ROOT);

            if (answer.equals("ΝΑΙ")){
                return true;
            } else if(answer.equals("ΟΧΙ")) {
                return false ;
            } else {
                System.out.println("Παρακαλώ απανήστε με ΝΑΙ ή ΟΧΙ");
            }
        }
    }

    private void showChanges() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("ΕΙΔΟΣ ΠΟΥ ΘΑ ΑΛΛΑΞΕΙ");
        System.out.println("============================================================");
        System.out.println("1.ΕΣΟΔΑ");
        System.out.println("2.ΕΞΟΔΑ");
        System.out.println("3.ΥΠΟΥΡΓΕΙΟ");
        System.out.println("============================================================");

        int choice = getChoice(1, 3);
        handleChoice(choice);
    }


    private boolean askToContinueChanges() {

        while (true) {
            System.out.println.("Θέλετε να συνεχίσετε τις αλλαγές; (ΝΑΙ ή ΟΧΙ):");
            String answer = scanner.nextLine().trim().toUpperCase(Locale.ROOT);

            if (answer.equals("ΝΑΙ")) {
                return true;
            } else if (answer.equals("ΟΧΙ")) {
                return false;
            } else {
                System.out.println("Παρακαλώ απανήστε με ΝΑΙ ή ΟΧΙ");
            }
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1:
                handleRevenueChanges();
                break;
            case 2:
                handleExpensesChanges();
                break;
            case 3:
                handleMinistryChanges();
                break;
        }
    }

    private void handleRevenueChanges() {
        System.out.println();
        System.out.println("ΑΛΛΑΓΕΣ ΣΤΑ ΕΣΟΔΑ");
        System.out.println("====================");

        showRevenueWithCodes();

        System.out.println();
        System.out.println("Επιλογες:");
        System.out.println("1.Αλλαγή εσόδου με βάση τον κωδικό");
        System.out.println("2.Προσθήκη νέου εσόδου");

        int choice = getChoice(1, 2);

        switch (choice) {
            case 1:
                changeRevenueByCode();
                break;
            case 2:
                addNewRevenue();
                break;
        }
    }

    private void handleExpensesChanges() {
      System.out.println();
        System.out.println("ΑΛΛΑΓΕΣ ΣΤΑ ΕΞΟΔΑ");
        System.out.println("====================");

        showExpenseWithCodes();

        System.out.println();
        System.out.println("Επιλογες:");
        System.out.println("1.Αλλαγή εξόδου με βάση τον κωδικό");
        System.out.println("2.Προσθήκη νέου εξόδου");

        int choice = getChoice(1, 2);

        switch (choice) {
            case 1:
                changeExpenseByCode();
                break;
            case 2:
                addNewExpense();
                break;  
        }
    }

    private void handleMinistryChanges() {
        System.out.println();
        System.out.println("ΑΛΛΑΓΕΣ ΣΕ ΥΠΟΥΡΓΕΙΟ");
        System.out.println("====================");

        Set<String> ministries = getMinistries();
        List<String> ministryList = new ArrayList<>(ministries);
        Collections.sort(ministryList);

        System.out.println("Διαθέσιμα Υπουργεία:");

        for (int i = 0; i < ministryList.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, ministryList.get(i));
        }

        System.oyt.println("Επιλέξετε Υπουργείο δίνοντας αριθμό:");
        int ministryChoice = getChoice(1, ministryList.size());
        String selectedMinistry = ministryList.get(ministryChoice - 1);

        System.out.println();
        System.out.println("Επιλογές για " + selectedMinistry + ":");
        System.out.println("1.Αλλαγή εσόδου με βάση τον κωδικό");
        System.out.println("2.Αλλαγή εξόδου με βάση τον κωδικό");
        System.out.println("3.Μεταφορά εξόδων από ή προς άλλο υπουργείο");

        int choice = getChoice(1, 3);

        switch (choice) {
            case 1:
                changeMinistryRevenueByCode(selectedMinistry);
                break;
            case 2:
                changeMinistryExpenseByCode(selectedMinistry);
                break;
            case 3:
                transferExpenses(selectedMinistry);
                break;
        }
    }

    private void showRevenueWithCodes() {
        List<BudgetAnalyzer.Entry> revenueEntries = modifiedEntries.stream()
        .filter(e ->"Έσοδα".equals(e.type))
        .collect(Collectors.tolist());

        long totalRevenue = revenueEntries.stream()
        .mapToLong(e -> e.amount)
        .sum();

        System.out.printf("Τρέχον σύνολο εσόδων: %,d €%n", totalRevenue);
        System.out.println();
        System.out.println("Κωδικοί Εσόδων:");
        System.out.printf("%-8s %-60s %15s%n", "ΚΩΔΙΚΟΣ", "ΠΕΡΙΓΡΑΦΗ", "ΠΟΣΟ (€)");
        System.out.println("--------------------------------------------------------------------------------");

        Map<String, List<BudgetAnalyzer.Entry>> revenuesByCode = revenueEntries.stream()
        .filter(e -> e.code != null && !e.code.isEmpty())
        .collect(Collectors.groupingBy(e -> e.code));

        for (Map.Entry<String, List<BudgetAnalyzer.Entry>> entry : revenuesByCode.entrySet()) {
            String code = entry.getKey();
            long amount = entry.getValue().stream().mapToLong(e -> e.amount).sum();
            String description = entry.getValue().get(0).source.replace(code, "").trim();
            System.out.printf("%-8s %-60s %,15%n" , code, description, amount);
        }
    }

    private void showExpenseWithCodes() {
        List<BudgetAnalyzer.Entry> expenseEntries = modifiedEntries.stream()
                .filter(e -> "Έξοδα".equals(e.type))
                .collect(Collectors.toList());
                
        long totalExpenses = expenseEntries.stream()
                .mapToLong(e -> e.amount)
                .sum();
                
        System.out.printf("Τρέχον σύνολο εξόδων: %,d €%n", totalExpenses);
        System.out.println();
        System.out.println("Κωδικοί Εξόδων:");
        System.out.printf("%-10s %-50s %-30s %15s%n", "ΚΩΔΙΚΟΣ", "ΠΕΡΙΓΡΑΦΗ", "ΥΠΟΥΡΓΕΙΟ", "ΠΟΣΟ (€)");
        System.out.println("------------------------------------------------------------------------------------------");

        Map<String, List<BudgetAnalyzer.Entry>> expenseByCode = expenseEntries.stream()
        .filter(e -> e.code != null && !e.code.isEmpty())
        .collect(Collectors.groupingBy(e -> e.code));

        for (Map.Entry<String, List<BudgetAnalyzer.Entry>> entry : expenseByCode.entrySet()) {
            String code = entry.getKey();
            long amount = entry.getValue().stream().mapToLong(e -> e.amount).sum();
            String description = entry.getValue().get(0).source.replace(code, "").trim();
            String ministry = entry.getValue().get(0).ministry;
            System.out.printf("%-10s %-50s %-30s %,15d%n", code, description, ministry, amount);
        }
    }

     private void showMinistryRevenuesWithCodes(String ministry) {
        List<BudgetAnalyzer.Entry> ministryEntries = modifiedEntries.stream()
                .filter(e -> ministry.equals(e.ministry) && "Έσοδα".equals(e.type))
                .collect(Collectors.toList());
                
        if (ministryEntries.isEmpty()) {
            System.out.println("Δεν βρέθηκαν έσοδα για το υπουργείο: " + ministry);
            return;
        }

        
        long totalRevenue = ministryEntries.stream()
                .mapToLong(e -> e.amount)
                .sum();
                
        System.out.println();
        System.out.println("ΕΣΟΔΑ ΓΙΑ ΥΠΟΥΡΓΕΙΟ: " + ministry);
        System.out.printf("Σύνολο: %,d €%n", totalRevenue);
        System.out.println();
        System.out.printf("%-10s %-60s %15s%n", "ΚΩΔΙΚΟΣ", "ΠΕΡΙΓΡΑΦΗ", "ΠΟΣΟ (€)");
        System.out.println("-------------------------------------------------------------------------");

        Map<String. List<BudgetAnalyzer.Entry>> revenueByCode = ministryEntries.stream()
        .filter(e -> e.code != null && !e.code.isEmpty())
        .collect(Collectors.groupingBy(e -> e.code));

        for (Map.Entry<String. List<BudgetAnalyzer.Entry>> entry : revenueByCode.entrySet()) {
            String code = entry.getKey();
            long amount = entry.getValue().stream().mapToLong(e -> e.amount).sum();
            String description = entry.getValue().get(0).source.replace(code, "").trim();
            System.out.printf("%-10s %-60s %,15d%n", code, description, amount);
        }
     }

     private void showMinistryExpensesWithCodes(String ministry) {
        List<BudgetAnalyzer.Entry> ministryEntries = modifiedEntries.stream()
        .filter(e -> ministry.equals(e.ministry) && "Έξοδα".equals(e.type))
        .collect(Collectors.toList());

        long totalExpenses = ministryEntries.stream()
        .mapToLong(e -> e.amount)
        .sum();

        System.out.println();
        System.out.println("ΕΞΟΔΑ ΓΙΑ ΥΠΟΥΡΓΕΙΟ" = ministry);
        System.out.printf("ΣΥΝΟΛΟ: %,d €%n", totalExpenses);
        SAystem.out.println();
        System.out.printf("%-10s %-60s %15s%n" , "ΚΩΔΙΚΟΣ" , "ΠΕΡΙΓΡΑΦΗ" , "ΠΟΣΟ (€)");
        System.out.println("---------------------------------------------------------------------------");
     }
} 

