package com.obelisk;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Compares Greece (row 5) with another selected country (rows 1–4)
 * using both absolute amounts (in million €) and %GDP/share metrics
 * from countries/Country_comp.csv.
 *
 * Data sources: Eurostat, European Commission finance site,
 * United Nations World Population Prospects.
 * NOTE: All values are speculative for 2025 as the year has not ended.
 */
public class CountryComp {

    private static class CountryData {
        String country;
        double population; // in millions
        double perCapitaSpending;
        String currency;

        // Absolute amounts (million €)
        double totalRevenue;
        double totalExpenditure;
        double balance;
        double gdp;
        double publicDebt;
        double directTaxes;
        double indirectTaxes;
        double nonTax;
        double healthExp;
        double educationExp;
        double defenseExp;
        double infrastructureExp;
        double investments;

        // Percentages
        double revenuePctGDP;
        double expenditurePctGDP;
        double balancePctGDP;
        double debtPctRevenue;
        double directTaxesShare;
        double indirectTaxesShare;
        double nonTaxShare;
        double healthShare;
        double educationShare;
        double defenseShare;
        double infrastructureShare;
        double investmentsShare;
    }

    public static void main(String[] args) {
        String csvPath = "/mnt/c/Users/pinec/Desktop/Obelisk/countries/Country_comp.csv";

        try {
            List<CountryData> countries = loadCountries(csvPath);

            // Greece is always row 5
            CountryData greece = countries.get(4);

            System.out.println("============================================================");
            System.out.println("ΣΥΓΚΡΙΣΗ ΕΛΛΑΔΑΣ ΜΕ ΑΛΛΗ ΧΩΡΑ");
            System.out.println("============================================================");
            System.out.println("Όλα τα δεδομένα βασίζονται σε Eurostat, European Commission Finance site και UN WNP.");
            System.out.println("Είναι εκτιμήσεις για το 2025 καθώς το έτος δεν έχει ολοκληρωθεί.");
            System.out.println("Πρώτος πίνακας: Απόλυτα ποσά σε εκατομμύρια ευρώ (€).");
            System.out.println("Δεύτερος πίνακας: Ποσοστά (% ΑΕΠ ή % εσόδων).");
            System.out.println();

            Scanner scanner = new Scanner(System.in);
            boolean continueComparison = true;

            while (continueComparison) {
                System.out.println("Διαθέσιμες χώρες για σύγκριση:");
                for (int i = 0; i < 4; i++) {
                    System.out.printf("%d. %s%n", i + 1, countries.get(i).country);
                }

                System.out.println();
                System.out.print("Επιλέξτε χώρα (1-4): ");
                int choice = scanner.nextInt();

                if (choice < 1 || choice > 4) {
                    System.out.println("Μη έγκυρη επιλογή.");
                    continue;
                }

                CountryData other = countries.get(choice - 1);

                // USA
                if ("ΗΠΑ".equals(other.country)) {
                    System.out.println("ΣΗΜΕΙΩΣΗ: Τα δεδομένα των ΗΠΑ είναι σε δολάρια ($).");
                    System.out.println("Οι συγκρίσεις γίνονται με βάση ποσοστά επί του ΑΕΠ και όχι μετατροπές.");
                }

                printAbsoluteTable(greece, other);
                compareCountries(greece, other);

                System.out.println();
                System.out.println("Αιτιολόγηση σύγκρισης:");
                System.out.println(explanationForCountry(other.country));

                // === Ερώτηση ΝΑΙ/ΟΧΙ ===
                System.out.println();
                System.out.print("Θέλετε να κάνετε άλλη σύγκριση; (ΝΑΙ/ΟΧΙ): ");
                String answer = scanner.next().trim().toUpperCase();

                if (answer.equals("ΝΑΙ")) {
                    continueComparison = true;
                } else if (answer.equals("ΟΧΙ")) {
                    continueComparison = false;
                    System.out.println("Το πρόγραμμα τερματίζει. Ευχαριστούμε!");
                } else {
                    System.out.println("Παρακαλώ απαντήστε μόνο με ΝΑΙ ή ΟΧΙ.");
                }
            }

        } catch (IOException e) {
            System.err.println("Σφάλμα κατά την ανάγνωση του αρχείου: " + e.getMessage());
        }
    }

    private static List<CountryData> loadCountries(String csvFile) throws IOException {
        List<CountryData> list = new ArrayList<>();
        Path path = Paths.get(csvFile);

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (first) { first = false; continue; }

                String[] f = line.split(",", -1);
                CountryData c = new CountryData();
                c.country = f[1].trim();
                c.population = Double.parseDouble(f[2].trim());
                c.perCapitaSpending = Double.parseDouble(f[3].trim());
                c.currency = f[4].trim();

                // Absolute amounts (CSV columns: 5,7,10,11,12,15,16,18,20,22,24,26,28,30)
                c.totalRevenue = Double.parseDouble(f[5].trim());
                c.totalExpenditure = Double.parseDouble(f[7].trim());
                c.balance = Double.parseDouble(f[10].trim());
                c.gdp = Double.parseDouble(f[11].trim());
                c.publicDebt = Double.parseDouble(f[12].trim());
                c.directTaxes = Double.parseDouble(f[15].trim());
                c.indirectTaxes = Double.parseDouble(f[17].trim());
                c.nonTax = Double.parseDouble(f[19].trim());
                c.healthExp = Double.parseDouble(f[21].trim());
                c.educationExp = Double.parseDouble(f[23].trim());
                c.defenseExp = Double.parseDouble(f[25].trim());
                c.infrastructureExp = Double.parseDouble(f[27].trim());
                c.investments = Double.parseDouble(f[29].trim());

                // Percentages (CSV columns: 6,8,9,13,16,18,20,22,24,26,28,30)
                c.revenuePctGDP = Double.parseDouble(f[6].trim());
                c.expenditurePctGDP = Double.parseDouble(f[8].trim());
                c.balancePctGDP = Double.parseDouble(f[9].trim());
                c.debtPctRevenue = Double.parseDouble(f[13].trim());
                c.directTaxesShare = Double.parseDouble(f[16].trim());
                c.indirectTaxesShare = Double.parseDouble(f[18].trim());
                c.nonTaxShare = Double.parseDouble(f[20].trim());
                c.healthShare = Double.parseDouble(f[22].trim());
                c.educationShare = Double.parseDouble(f[24].trim());
                c.defenseShare = Double.parseDouble(f[26].trim());
                c.infrastructureShare = Double.parseDouble(f[28].trim());
                c.investmentsShare = Double.parseDouble(f[30].trim());

                list.add(c);
            }
        }
        return list;
    }

    private static void printAbsoluteTable(CountryData greece, CountryData other) {
        System.out.println();
        System.out.printf("ΑΠΟΛΥΤΑ ΜΕΓΕΘΗ (σε εκατομμύρια ευρώ) - Ελλάδα vs %s%n", other.country);
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-30s %-15s %-15s%n", "Δείκτης", "Ελλάδα", other.country);
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Συνολικά Έσοδα", greece.totalRevenue, other.totalRevenue);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Συνολικές Δαπάνες", greece.totalExpenditure, other.totalExpenditure);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Ισοζύγιο", greece.balance, other.balance);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "ΑΕΠ", greece.gdp, other.gdp);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Δημόσιο Χρέος", greece.publicDebt, other.publicDebt);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Άμεσοι φόροι", greece.directTaxes, other.directTaxes);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Έμμεσοι φόροι", greece.indirectTaxes, other.indirectTaxes);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Μη φορολογικά έσοδα", greece.nonTax, other.nonTax);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Δαπάνες Υγείας", greece.healthExp, other.healthExp);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Δαπάνες Παιδείας", greece.educationExp, other.educationExp);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Δαπάνες Άμυνας", greece.defenseExp, other.defenseExp);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Υποδομές", greece.infrastructureExp, other.infrastructureExp);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Επενδύσεις", greece.investments, other.investments);
        System.out.println("------------------------------------------------------------");
    }

    private static void compareCountries(CountryData greece, CountryData other) {
        System.out.println();
        System.out.printf("ΠΟΣΟΣΤΑ (%% ΑΕΠ ή %% εσόδων) - Ελλάδα vs %s%n", other.country);
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-30s %-15s %-15s%n", "Δείκτης", "Ελλάδα", other.country);
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Πληθυσμός (εκ.)", greece.population, other.population);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Κατά κεφαλήν Δαπάνη (€)", greece.perCapitaSpending, other.perCapitaSpending);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Έσοδα %% ΑΕΠ", greece.revenuePctGDP, other.revenuePctGDP);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Δαπάνες %% ΑΕΠ", greece.expenditurePctGDP, other.expenditurePctGDP);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Ισοζύγιο %% ΑΕΠ", greece.balancePctGDP, other.balancePctGDP);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Χρέος %% έσοδα", greece.debtPctRevenue, other.debtPctRevenue);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Άμεσοι φόροι %% έσοδα", greece.directTaxesShare, other.directTaxesShare);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Έμμεσοι φόροι %% έσοδα", greece.indirectTaxesShare, other.indirectTaxesShare);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Μη φορολογικά %% έσοδα", greece.nonTaxShare, other.nonTaxShare);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Υγεία %% δαπανών", greece.healthShare, other.healthShare);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Παιδεία %% δαπανών", greece.educationShare, other.educationShare);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Άμυνα %% δαπανών", greece.defenseShare, other.defenseShare);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Υποδομές %% δαπανών", greece.infrastructureShare, other.infrastructureShare);
        System.out.printf("%-30s %-15.1f %-15.1f%n", "Επενδύσεις %% δαπανών", greece.investmentsShare, other.investmentsShare);
        System.out.println("------------------------------------------------------------");
    }

    private static String explanationForCountry(String country) {
    switch (country) {
        case "Ιταλία":
            return "Η Ιταλία συγκρίνεται με την Ελλάδα γιατί και οι δύο χώρες ανήκουν στη Νότια Ευρώπη, "
                 + "έχουν παρόμοια κοινωνικοοικονομικά χαρακτηριστικά και βρέθηκαν στο επίκεντρο της κρίσης χρέους. "
                 + "Οι μεγαλύτερες δαπάνες και έσοδα της Ιταλίας αντανακλούν το μέγεθος της οικονομίας της, "
                 + "ενώ η Ελλάδα εμφανίζει υψηλότερα ποσοστά χρέους σε σχέση με τα έσοδά της, δείχνοντας μεγαλύτερη εξάρτηση από δανεισμό. "
                 + "Οι διαφορές στο ισοζύγιο υποδηλώνουν διαφορετική ικανότητα διαχείρισης ελλειμμάτων: "
                 + "στην Ιταλία ακόμη μεγάλα ποσά είναι διαχειρίσιμα λόγω μεγέθους, ενώ στην Ελλάδα μικρές αποκλίσεις έχουν σημαντικό αντίκτυπο.";
        case "Πορτογαλία":
            return "Η Πορτογαλία συγκρίνεται με την Ελλάδα γιατί οι δύο χώρες είχαν παρόμοια εμπειρία κατά την κρίση χρέους "
                 + "και μπήκαν σε προγράμματα προσαρμογής. Οι μικρότερες δαπάνες και έσοδα της Πορτογαλίας δείχνουν μια πιο περιορισμένη οικονομία, "
                 + "αλλά και πιο συνετή δημοσιονομική πολιτική μετά την κρίση. Η Ελλάδα εμφανίζει υψηλότερα ποσοστά χρέους, "
                 + "κάτι που σημαίνει ότι η πορεία εξυγίανσης ήταν πιο δύσκολη και παρατεταμένη. "
                 + "Οι διαφορές στις δαπάνες για παιδεία και υγεία δείχνουν διαφορετικές προτεραιότητες: "
                 + "η Πορτογαλία επένδυσε περισσότερο σε κοινωνική συνοχή, ενώ η Ελλάδα συχνά αναγκάστηκε να περικόψει λόγω πιέσεων.";
        case "Γερμανία":
            return "Η Γερμανία συγκρίνεται με την Ελλάδα ως η μεγαλύτερη οικονομία της Ευρωζώνης και βασικός χρηματοδότης των μηχανισμών στήριξης. "
                 + "Τα πολύ υψηλότερα έσοδα και δαπάνες δείχνουν την κλίμακα της γερμανικής οικονομίας, αλλά και την ισχυρή φορολογική βάση. "
                 + "Οι χαμηλότερες αναλογίες χρέους σε σχέση με τα έσοδα υποδηλώνουν πιο σταθερή δημοσιονομική θέση και μεγαλύτερη αξιοπιστία στις αγορές. "
                 + "Οι διαφορές στις δαπάνες για άμυνα και υποδομές δείχνουν διαφορετικές στρατηγικές: "
                 + "η Γερμανία επενδύει περισσότερο σε βιομηχανική ανάπτυξη και τεχνολογία, ενώ η Ελλάδα διαθέτει μεγαλύτερο ποσοστό για άμυνα λόγω γεωπολιτικής θέσης.";
        case "ΗΠΑ":
            return "Οι ΗΠΑ συγκρίνονται με την Ελλάδα για να φανεί η αντίθεση ανάμεσα σε μια παγκόσμια υπερδύναμη και μια μικρότερη ευρωπαϊκή οικονομία. "
                 + "Οι αριθμοί είναι σε δολάρια, αλλά οι ποσοστιαίες διαφορές δείχνουν διαφορετικά μοντέλα φορολογίας και δημόσιων δαπανών. "
                 + "Οι ΗΠΑ έχουν πολύ μεγαλύτερη δυνατότητα να χρηματοδοτούν ελλείμματα λόγω του ρόλου του δολαρίου ως παγκόσμιου αποθεματικού νομίσματος, "
                 + "ενώ η Ελλάδα είναι περιορισμένη από τους κανόνες της Ευρωζώνης. "
                 + "Οι διαφορές στις δαπάνες για υγεία και παιδεία δείχνουν διαφορετικά συστήματα: "
                 + "στις ΗΠΑ μεγάλο μέρος είναι ιδιωτικό, ενώ στην Ελλάδα οι δημόσιες δαπάνες έχουν μεγαλύτερη βαρύτητα.";
        default:
            return "Δεν υπάρχει διαθέσιμη αιτιολόγηση για αυτή τη χώρα.";
    }
    }
}
