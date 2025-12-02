package com.obelisk;

import java.sql.BatchUpdateException;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Διαχειρίζεται τις αλλαγές στον προυπολογισμό
 * Ο χρήστης μπορεί να δει προσομοιώσεις των αλλαγών του στα έσοδα,έξοδα και Υπουργεία
 * αλλα και να δει τα αποτελέσματα στον τελικό προυπολογισμό
 */

class BudgetEntry {
    public String type;
    public long amount;
    public String ministry;
    public String source;
    public String code;
    
    public BudgetEntry() {}
}
class Scenarios {

    private List<BudgetEntry> originalEntries;
    private List<BudgetEntry>modifiedEntries;
    private Scanner scanner;

    public Scenarios(List<BudgetEntry>entries) {
        this.originalEntries = new ArrayList<>(entries);
        this.modifiedEntries = new ArrayList<>(entries);
        this.scanner = new Scanner(System.in);

        for (BudgetEntry entry : entries) {
            BudgetEntry newEntry = new BudgetEntry();
            newEntry.type = entry.type;
            newEntry.amount = entry.amount;
            newEntry.ministry = entry.ministry;
            newEntry.source = entry.source;
            newEntry.code = entry.code;
            this.originalEntries.add(newEntry);
            this.modifiedEntries.add(newEntry);
        }
    }

    public void runScenarios() {
       System.out.println();
       System.out.println("=============================================================");
       System.out.println("ΑΛΛΑΓΕΣ ΣΤΟΝ ΠΡΟΥΠΟΛΟΓΙΣΜΟ");
       System.out.println("=============================================================");
      
       if (!askForChanges()) {
          System.out.println("Τερματισμός προγράμματος");
          return;
        }

        boolean moreChanges = true;
        while (moreChanges) {
          showChangeMenu();

          moreChanges = askToContinueChanges();
        }

        showFinalResultsWithTables();
    }

    private boolean askForChanges() {

        while (true) {
            System.out.println("Θέλετε να κάνετε αλλαγές στον προυπολογισμό; (Απαντήστε με ΝΑΙ ή ΟΧΙ): ");
            String answer = scanner.nextLine().trim().toUpperCase(Locale.ROOT);

            if (answer.equals("ΝΑΙ")){
                return true;
            } else if(answer.equals("ΟΧΙ")) {
                return false ;
            } else {
                System.out.println("Παρακαλώ απαντήστε με ΝΑΙ ή ΟΧΙ");
            }
        }
    }

    private void showChangeMenu() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("ΕΙΔΟΣ ΠΟΥ ΘΑ ΑΛΛΑΞΕΙ");
        System.out.println("============================================================");
        System.out.println("1.ΕΣΟΔΑ");
        System.out.println("2.ΕΞΟΔΑ");
        System.out.println("3.ΥΠΟΥΡΓΕΙΟ");
        System.out.println("============================================================");

        int choice = getMenuChoice(1, 3);
        handleChoice(choice);
    }


    private boolean askToContinueChanges() {
        while (true) {
           System.out.println("Θέλετε να συνεχίσετε τις αλλαγές; (ΝΑΙ ή ΟΧΙ):");
           String answer = scanner.nextLine().trim().toUpperCase(Locale.ROOT);

           if (answer.equals("ΝΑΙ")) {
               return true;
            } else if (answer.equals("ΟΧΙ")) {
               return false;
            } else {
               System.out.println("Παρακαλώ απαντήστε με ΝΑΙ ή ΟΧΙ");
            }
        }
    }

    private int getMenuChoice(int min, int max) {
        while (true) {
            try {
                System.out.print("Επιλογή (" + min + "-" + max + "): ");
                String input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }

                System.out.println("Επιλογή εκτός ορίων.");
            } catch (NumberFormatException e) {
                System.out.println("Μη έγκυρη είσοδος.");
            }
        }
    }

    private long getAmountFromUser() {

        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);
            }catch (NumberFormatException e) {
                System.out.println("Μη έγκυρο ποσό.Δοκιμάστε ξανά:");
            }
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1:
                handleRevenueChanges();
                break;
            case 2:
                handleExpenseChanges();
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
        System.out.println("Επιλογές:");
        System.out.println("1.Αλλαγή εσόδου με βάση τον κωδικό");
        System.out.println("2.Προσθήκη νέου εσόδου");

        int choice = getMenuChoice(1, 2);

        switch (choice) {
            case 1:
                changeRevenueByCode();
                break;
            case 2:
                addNewRevenue();
                break;
        }
    }

    private void handleExpenseChanges() {
      System.out.println();
        System.out.println("ΑΛΛΑΓΕΣ ΣΤΑ ΕΞΟΔΑ");
        System.out.println("====================");

        showExpenseWithCodes();

        System.out.println();
        System.out.println("Επιλογές:");
        System.out.println("1.Αλλαγή εξόδου με βάση τον κωδικό");
        System.out.println("2.Προσθήκη νέου εξόδου");

        int choice = getMenuChoice(1, 2);

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

        System.out.println("Επιλέξτε Υπουργείο δίνοντας αριθμό:");
        int ministryChoice = getMenuChoice(1, ministryList.size());
        String selectedMinistry = ministryList.get(ministryChoice - 1);

        System.out.println();
        System.out.println("Επιλογές για " + selectedMinistry + ":");
        System.out.println("1.Αλλαγή εσόδου με βάση τον κωδικό");
        System.out.println("2.Αλλαγή εξόδου με βάση τον κωδικό");
        System.out.println("3.Μεταφορά εξόδων από ή προς άλλο υπουργείο");

        int choice = getMenuChoice(1, 3);

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

    private Set<String> getMinistries() {
        return modifiedEntries.stream()
                .map(e -> e.ministry)
                .filter(m -> m != null && !m.isEmpty() && !"-".equals(m.trim()))
                .collect(Collectors.toSet());
    }

    private void showRevenueWithCodes() {
        List<BudgetEntry> revenueEntries = modifiedEntries.stream()
        .filter(e ->"Έσοδα".equals(e.type))
        .collect(Collectors.toList());

        long totalRevenue = revenueEntries.stream()
        .mapToLong(e -> e.amount)
        .sum();

        System.out.printf("Τρέχον σύνολο εσόδων: %,d €%n", totalRevenue);
        System.out.println();
        System.out.println("Κωδικοί Εσόδων:");
        System.out.printf("%-8s %-60s %15s%n", "ΚΩΔΙΚΟΣ", "ΠΕΡΙΓΡΑΦΗ", "ΠΟΣΟ (€)");
        System.out.println("--------------------------------------------------------------------------------");

        Map<String, List<BudgetEntry>> revenuesByCode = revenueEntries.stream()
        .filter(e -> e.code != null && !e.code.isEmpty())
        .collect(Collectors.groupingBy(e -> e.code));

        for (Map.Entry<String, List<BudgetEntry>> entry : revenuesByCode.entrySet()) {
            String code = entry.getKey();
            long amount = entry.getValue().stream().mapToLong(e -> e.amount).sum();
            String description = entry.getValue().get(0).source.replace(code, "").trim();
            System.out.printf("%-8s %-60s %,15d%n" , code, description, amount);
        }
    }

    private void showExpenseWithCodes() {
        List<BudgetEntry> expenseEntries = modifiedEntries.stream()
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

        Map<String, List<BudgetEntry>> expenseByCode = expenseEntries.stream()
                .filter(e -> e.code != null && !e.code.isEmpty())
                .collect(Collectors.groupingBy(e -> e.code));

        for (Map.Entry<String, List<BudgetEntry>> entry : expenseByCode.entrySet()) {
            String code = entry.getKey();
            long amount = entry.getValue().stream().mapToLong(e -> e.amount).sum();
            String description = entry.getValue().get(0).source.replace(code, "").trim();
            String ministry = entry.getValue().get(0).ministry;
            System.out.printf("%-10s %-50s %-30s %,15d%n", code, description, ministry, amount);
        }
    }

     private void showMinistryRevenuesWithCodes(String ministry) {
        List<BudgetEntry> ministryEntries = modifiedEntries.stream()
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

        Map<String, List<BudgetEntry>> revenueByCode = ministryEntries.stream()
                .filter((BudgetEntry e) -> e.code != null && !e.code.isEmpty())
                .collect(Collectors.groupingBy((BudgetEntry e) -> e.code));

        for (Map.Entry<String, List<BudgetEntry>> entry : revenueByCode.entrySet()) {
            String code = entry.getKey();
            long amount = entry.getValue().stream().mapToLong(e -> e.amount).sum();
            String description = entry.getValue().get(0).source.replace(code, "").trim();
            System.out.printf("%-10s %-60s %,15d%n", code, description, amount);
        }
     }

     private void showMinistryExpensesWithCodes(String ministry) {
        List<BudgetEntry> ministryEntries = modifiedEntries.stream()
            .filter(e -> ministry.equals(e.ministry) && "Έξοδα".equals(e.type))
            .collect(Collectors.toList());

        long totalExpenses = ministryEntries.stream()
            .mapToLong(e -> e.amount)
            .sum();

        System.out.println();
        System.out.println("ΕΞΟΔΑ ΓΙΑ ΥΠΟΥΡΓΕΙΟ: " + ministry);
        System.out.printf("ΣΥΝΟΛΟ: %,d €%n", totalExpenses);
        System.out.println();
        System.out.printf("%-10s %-60s %15s%n" , "ΚΩΔΙΚΟΣ" , "ΠΕΡΙΓΡΑΦΗ" , "ΠΟΣΟ (€)");
        System.out.println("---------------------------------------------------------------------------");

        Map<String, List<BudgetEntry>> expensesByCode = ministryEntries.stream()
                 .filter(e -> e.code != null && !e.code.isEmpty())
                 .collect(Collectors.groupingBy(e -> e.code));

             for (Map.Entry<String, List<BudgetEntry>> entry :  expensesByCode.entrySet()) {
             String code = entry.getKey();
             long amount = entry.getValue().stream().mapToLong(e -> e.amount).sum();
             String description = entry.getValue().get(0).source.replace(code, "").trim();
             System.out.printf("%-10s %-60s %,15d%n", code, description, amount);
             }
     }

     private void changeRevenueByCode() {
        System.out.println();
        System.out.print("Εισάγετε τον κωδικό του εσόδου που θέλετε να αλλάξετε: ");
        String code = scanner.nextLine().trim();

        List<BudgetEntry> revenueEntries = modifiedEntries.stream()
                .filter(e -> "Έσοδα".equals(e.type) && code.equals(e.code))
                .collect(Collectors.toList());
        
        if (revenueEntries.isEmpty()) {
            System.out.println("Δεν βρέθηκε έσοδο με κωδικό: " + code);
            return;
        }
        
        long currentAmount = revenueEntries.stream().mapToLong(e -> e.amount).sum();
        String description = revenueEntries.get(0).source.replace(code, "").trim();
        
        System.out.printf("Τρέχον έσοδο: %s - %s - %,d €%n", code, description, currentAmount);
        System.out.print("Εισάγετε το νέο ποσό (€): ");
        long newAmount = getAmountFromUser();


        modifiedEntries.removeAll(revenueEntries);

        BudgetEntry updatedRevenue = new BudgetEntry();
        updatedRevenue.type = "Έσοδα";
        updatedRevenue.amount = newAmount;
        updatedRevenue.ministry = "-";
        updatedRevenue.source = code + " " + description;
        updatedRevenue.code = code;

        modifiedEntries.add(updatedRevenue);

        System.out.printf("Επιτυχής αλλαγή ! Το έσοδο %s άλλαξε από %,d € σε %,d €%n",
            code, currentAmount, newAmount);
     }

     private void changeExpenseByCode() {
        System.out.println();
        System.out.print("Εισάγετε τον κωδικό του εξόδου που θέλετε να αλλάξετε: ");
        String code = scanner.nextLine().trim();

        List<BudgetEntry> expenseEntries = modifiedEntries.stream()
                .filter(e -> "Έξοδα".equals(e.type) && code.equals(e.code))
                .collect(Collectors.toList());
        
        if (expenseEntries.isEmpty()) {
            System.out.println("Δεν βρέθηκε έξοδο με κωδικό: " + code);
            return;
        }
        
        long currentAmount = expenseEntries.stream().mapToLong(e -> e.amount).sum();
        String description = expenseEntries.get(0).source.replace(code, "").trim();
        String ministry = expenseEntries.get(0).ministry;
        
        System.out.printf("Τρέχον έξοδο: %s - %s - %s - %,d €%n", code, description, ministry, currentAmount);
        System.out.print("Εισάγετε το νέο ποσό (€): ");
        long newAmount = getAmountFromUser();


        modifiedEntries.removeAll(expenseEntries);
        
        BudgetEntry updatedExpense = new BudgetEntry();
        updatedExpense.type = "Έξοδα";
        updatedExpense.amount = newAmount;
        updatedExpense.ministry = ministry;
        updatedExpense.source = code + " " + description;
        updatedExpense.code = code;
        
        modifiedEntries.add(updatedExpense);
        
        System.out.printf("Επιτυχής αλλαγή! Το έξοδο %s άλλαξε από %,d € σε %,d €%n", 
         code, currentAmount, newAmount);
    }

    private void changeMinistryRevenueByCode(String ministry) {
        System.out.println();

        showMinistryRevenuesWithCodes(ministry);

        List<BudgetEntry> ministryEntries = modifiedEntries.stream()
            .filter(e -> ministry.equals(e.ministry) && "Έσοδα".equals(e.type))
            .collect(Collectors.toList());
            
            if (ministryEntries.isEmpty()) {
                return;
            }
        
        System.out.print("Εισάγετε τον κωδικό του εσόδου που θέλετε να αλλάξετε: ");
        String code = scanner.nextLine().trim();

        List<BudgetEntry> revenueEntries = modifiedEntries.stream()
                .filter(e -> "Έσοδα".equals(e.type) && code.equals(e.code) && ministry.equals(e.ministry))
                .collect(Collectors.toList());
        
        if (revenueEntries.isEmpty()) {
            System.out.println("Δεν βρέθηκε έσοδο με κωδικό: " + code + " για το υπουργείο: " + ministry);
            return;
        }


        long currentAmount = revenueEntries.stream().mapToLong(e -> e.amount).sum();
        String description = revenueEntries.get(0).source.replace(code, "").trim();
        
        System.out.printf("Τρέχον έσοδο: %s - %s - %,d €%n", code, description, currentAmount);
        System.out.print("Εισάγετε το νέο ποσό (€): ");
        long newAmount = getAmountFromUser();


        modifiedEntries.removeAll(revenueEntries);
        
        BudgetEntry updatedRevenue = new BudgetEntry();
        updatedRevenue.type = "Έσοδα";
        updatedRevenue.amount = newAmount;
        updatedRevenue.ministry = ministry;
        updatedRevenue.source = code + " " + description;
        updatedRevenue.code = code;
        
        modifiedEntries.add(updatedRevenue);

         System.out.printf("Επιτυχής αλλαγή! Το έσοδο %s του %s άλλαξε από %,d € σε %,d €%n", 
          code, ministry, currentAmount, newAmount);
    }


    private void changeMinistryExpenseByCode(String ministry) {
        System.out.println();

        showMinistryExpensesWithCodes(ministry);
        
        System.out.print("Εισάγετε τον κωδικό του εξόδου που θέλετε να αλλάξετε: ");
        String code = scanner.nextLine().trim();

        List<BudgetEntry> expenseEntries = modifiedEntries.stream()
                .filter(e -> "Έξοδα".equals(e.type) && code.equals(e.code) && ministry.equals(e.ministry))
                .collect(Collectors.toList());
        
        if (expenseEntries.isEmpty()) {
            System.out.println("Δεν βρέθηκε έξοδο με κωδικό: " + code + " για το υπουργείο: " + ministry);
            return;
        }

        long currentAmount = expenseEntries.stream().mapToLong(e -> e.amount).sum();
        String description = expenseEntries.get(0).source.replace(code, "").trim();
        
        System.out.printf("Τρέχον έξοδο: %s - %s - %,d €%n", code, description, currentAmount);
        System.out.print("Εισάγετε το νέο ποσό (€): ");
        long newAmount = getAmountFromUser();

        modifiedEntries.removeAll(expenseEntries);
        
        BudgetEntry updatedExpense = new BudgetEntry();
        updatedExpense.type = "Έξοδα";
        updatedExpense.amount = newAmount;
        updatedExpense.ministry = ministry;
        updatedExpense.source = code + " " + description;
        updatedExpense.code = code;
        
        modifiedEntries.add(updatedExpense);

        System.out.printf("Επιτυχής αλλαγή! Το έξοδο %s του %s άλλαξε από %,d € σε %,d €%n", 
         code, ministry, currentAmount, newAmount);
    }

    private void addNewRevenue() {
        System.out.println();
        System.out.print("Κωδικός νέου εσόδου:");
        String code = scanner.nextLine().trim();
        System.out.println("Περιγραφή νέου εσόδου:");
        String description = scanner.nextLine();
        System.out.print("Ποσό νέου εσόδου:");
        long amount = getAmountFromUser();

        BudgetEntry newRevenue = new BudgetEntry();
        newRevenue.type = "Έσοδα";
        newRevenue.amount = amount;
        newRevenue.ministry = "-";
        newRevenue.source = code + " " + description;
        newRevenue.code = code ;


        modifiedEntries.add(newRevenue);

        System.out.printf("Προστέθηκε νέο έσοδο: %s %s - %,d €%n", code, description, amount);
    }


    private void addNewExpense() {

        System.out.println();
        Set<String> ministries = getMinistries();
        List<String> ministryList = new ArrayList<>(ministries);
        Collections.sort(ministryList);
        
        System.out.println("Υπουργεία:");
        for (int i = 0; i < ministryList.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, ministryList.get(i));
        }
        
        System.out.println("Επιλέξτε υπουργείο:");
        int ministryChoice = getMenuChoice(1, ministryList.size());
        String ministry = ministryList.get(ministryChoice - 1);
        System.out.print("Κωδικός νέου εξόδου:");
        String code = scanner.nextLine().trim();
        System.out.print("Περιγραφή νέου εξόδου:");
        String description = scanner.nextLine();
        System.out.print("Ποσό νέου εξόδου (€):");
        long amount = getAmountFromUser();


        BudgetEntry newExpense = new BudgetEntry();
        newExpense.type = "Έξοδα";
        newExpense.amount = amount;
        newExpense.ministry = ministry;
        newExpense.source = code + " " + description;
        newExpense.code = code;

        modifiedEntries.add(newExpense);
        
        System.out.printf("Προστέθηκε νέο έξοδο: %s %s - %s - %,d €%n", code, description, ministry, amount);
    }


    private void transferExpenses(String fromMinistry) {

        System.out.println();
        Set<String> ministries = getMinistries();
        List<String> ministryList = new ArrayList<>(ministries);
        Collections.sort(ministryList);


        ministryList.remove(fromMinistry);

        System.out.println("Μεταφορά από:" + fromMinistry);
        System.out.println("Προς Υπουργείο:");

        for ( int i = 0; i < ministryList.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, ministryList.get(i));
        }

        System.out.println("Επιλέξτε προορισμό:");
        int toChoice = getMenuChoice(1, ministryList.size());
        String toMinistry = ministryList.get(toChoice - 1);
        System.out.print("Ποσό μεταφοράς:");
        long amount = getAmountFromUser();
        System.out.print("Περιγραφή μεταφοράς:");
        String description = scanner.nextLine();


        BudgetEntry reduction = new BudgetEntry();
        reduction.type = "Έξοδα";
        reduction.amount = -amount;
        reduction.ministry = fromMinistry;
        reduction.source = "Μεταφορά προς " + toMinistry + ": " + description;
        reduction.code = "TRF1";



        BudgetEntry addition = new BudgetEntry();
        addition.type = "Έξοδα";
        addition.amount = amount;
        addition.ministry = toMinistry;
        addition.source = "Μεταφορά από " + fromMinistry + ": " + description; 
        addition.code = "TRF1";


        modifiedEntries.add(reduction);
        modifiedEntries.add(addition);

        System.out.printf("Μεταφέρθηκαν %,d € από %s προς %s.%n", amount, fromMinistry, toMinistry);
    }


    private void showFinalResultsWithTables() {
      System.out.println();
      System.out.println("==================================================");
      System.out.println("ΤΕΛΙΚΑ ΑΠΟΤΕΛΕΣΜΑΤΑ ΜΕ ΑΛΛΑΓΕΣ");
      System.out.println("==================================================");
      System.out.println();
      System.out.println("ΠΙΝΑΚΑΣ 1 - ΕΣΟΔΑ ΜΕ ΑΛΛΑΓΕΣ");
      System.out.println("==================================================");
    
      Map<String, BudgetEntry> revenueCategories = new LinkedHashMap<>();
      Set<String> seenRevenueCodes = new HashSet<>();

      long totalRevenue = 0L;

      for (BudgetEntry e : modifiedEntries) {
          if (!"Έσοδα".equals(e.type)) {
              continue;
            }

          if (e.code != null && e.code.length() >= 2 && !seenRevenueCodes.contains(e.code)) {
              seenRevenueCodes.add(e.code);

              BudgetEntry category = new BudgetEntry();
              category.code = e.code;
              String label = e.source.substring(e.code.length()).trim();
              category.source = label;
              category.amount = e.amount;
              revenueCategories.put(category.code, category);
              totalRevenue += e.amount;
            }
        }

       System.out.printf("%-5s %-70s %20s%n", "ΚΩΔ", "ΠΕΡΙΓΡΑΦΗ", "ΠΟΣΟ (€)");
       System.out.println("-------------------------------------------------------------------------------------------------");
       for (BudgetEntry c : revenueCategories.values()) {
          System.out.printf("%-5s %-70s %20d%n", c.code, c.source, c.amount);
        }
       System.out.println("-------------------------------------------------------------------------------------------------");
       System.out.printf("%-76s %20d%n", "Σύνολο εσόδων", totalRevenue);
       System.out.println();
       System.out.println("ΠΙΝΑΚΑΣ 2 - ΕΞΟΔΑ ΑΝΑ ΥΠΟΥΡΓΕΙΟ ΜΕ ΑΛΛΑΓΕΣ");
       System.out.println("==================================================");
    
       Map<String, Long> expensesByMinistry = new HashMap<>();
       long totalExpenses = 0L;

       for (BudgetEntry e : modifiedEntries) {
           if (!"Έξοδα".equals(e.type)) continue;

          String ministry = (e.ministry == null || e.ministry.isEmpty()) ? "-" : e.ministry;
          expensesByMinistry.merge(ministry, e.amount, Long::sum);
          totalExpenses += e.amount;
        }

       List<Map.Entry<String, Long>> list = new ArrayList<>(expensesByMinistry.entrySet());
       list.sort(Map.Entry.comparingByKey());

       System.out.printf("%-60s %20s%n", "ΥΠΟΥΡΓΕΙΟ / ΦΟΡΕΑΣ", "ΠΟΣΟ ΕΞΟΔΩΝ (€)");
       System.out.println("-------------------------------------------------------------------------------------------------");
       for (Map.Entry<String, Long> e : list) {
           System.out.printf("%-76s %20d%n", e.getKey(), e.getValue());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-76s %20d%n", "Γενικό σύνολο εξόδων", totalExpenses);
        System.out.println();
        System.out.println("ΤΕΛΙΚΟ ΑΠΟΤΕΛΕΣΜΑ");
        System.out.println("==================================================");
        long result = totalRevenue - totalExpenses;
        System.out.printf("ΕΣΟΔΑ: %,d €%n", totalRevenue);
        System.out.printf("ΕΞΟΔΑ: %,d €%n", totalExpenses);
        System.out.printf("ΑΠΟΤΕΛΕΣΜΑ: %,d €%n", result);
    
        if (result > 0) {
           System.out.println("ΚΑΤΑΣΤΑΣΗ: ΠΛΕΟΝΑΣΜΑΤΙΚΟΣ");
        } else if (result < 0) {
           System.out.println("ΚΑΤΑΣΤΑΣΗ: ΕΛΛΕΙΜΜΑΤΙΚΟΣ");
        } else {
           System.out.println("ΚΑΤΑΣΤΑΣΗ: ΙΣΟΖΥΓΙΣΜΕΝΟΣ");
        }
    
        System.out.println();
        System.out.println("Ολοκλήρωση scenarios.");
 }
    public static void main(String[] args) {
    try {
        List<BudgetEntry> entries = loadEntriesFromCSV("budget/budget2025.csv");
        
        System.out.println("Φορτώθηκαν " + entries.size() + " εγγραφές από το CSV");
        
        Scenarios scenarios = new Scenarios(entries);
        scenarios.runScenarios();

    } catch (Exception e) {
        System.err.println("Σφάλμα: " + e.getMessage());
        e.printStackTrace();
    }
 }

    private static List<BudgetEntry> loadEntriesFromCSV(String csvFile) throws IOException {
        List<BudgetEntry> entries = new ArrayList<>();
        Path path = Paths.get(csvFile);
    
     try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
        String line;
        boolean first = true;
        
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            if (first) {
                first = false;
                continue;
            }
            
            String[] fields = parseCsvLine(line);
            if (fields.length >= 5) {
                BudgetEntry entry = new BudgetEntry();
                entry.type = fields[1].trim();
                entry.amount = Long.parseLong(fields[2].trim());
                entry.ministry = fields[3].trim();
                entry.source = fields[4].trim();
                entry.code = extractLeadingDigits(entry.source);
                entries.add(entry);
            }
        }
    }
    return entries;
 }
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

     for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
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

    private static String extractLeadingDigits(String text) {
       if (text == null) return "";
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
} 

