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

    public static class CountryData {

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
           String csvPath = "src/main/resources/countries";

        try {
            List<CountryData> countries = loadCountries();

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

    public static List<CountryData> loadCountries() {

        List<CountryData> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                CountryComp.class.getResourceAsStream("resources/countries/Country_comp.csv"), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;
                if (first) { first = false; continue; }

                String[] f = line.split(",", -1);
                if (f.length < 32) continue;
                CountryData c = new CountryData();
                
                try {
                    c.country = f[1].trim();
                    c.population = Double.parseDouble(f[2].trim());
                    c.perCapitaSpending = Double.parseDouble(f[3].trim());
                    c.currency = f[4].trim();

                // Absolute amounts 
                c.totalRevenue = Double.parseDouble(f[5].trim());
                c.totalExpenditure = Double.parseDouble(f[7].trim());
                c.balance = Double.parseDouble(f[9].trim());
                c.gdp = Double.parseDouble(f[11].trim());
                c.publicDebt = Double.parseDouble(f[12].trim());
                c.directTaxes = Double.parseDouble(f[16].trim());
                c.indirectTaxes = Double.parseDouble(f[18].trim());
                c.nonTax = Double.parseDouble(f[20].trim());
                c.healthExp = Double.parseDouble(f[22].trim());
                c.educationExp = Double.parseDouble(f[24].trim());
                c.defenseExp = Double.parseDouble(f[26].trim());
                c.infrastructureExp = Double.parseDouble(f[28].trim());
                c.investments = Double.parseDouble(f[30].trim());

                // Percentages
                c.revenuePctGDP = Double.parseDouble(f[6].trim());
                c.expenditurePctGDP = Double.parseDouble(f[8].trim());
                c.balancePctGDP = Double.parseDouble(f[10].trim());
                c.debtPctRevenue = Double.parseDouble(f[13].trim());
                c.debtPctRevenue = Double.parseDouble(f[15].trim()); // αν θες να το εμφανίζεις
                c.directTaxesShare = Double.parseDouble(f[17].trim());
                c.indirectTaxesShare = Double.parseDouble(f[19].trim());
                c.nonTaxShare = Double.parseDouble(f[21].trim());
                c.healthShare = Double.parseDouble(f[23].trim());
                c.educationShare = Double.parseDouble(f[25].trim());
                c.defenseShare = Double.parseDouble(f[27].trim());
                c.infrastructureShare = Double.parseDouble(f[29].trim());
                c.investmentsShare = Double.parseDouble(f[31].trim());

                list.add(c);
                } catch (NumberFormatException e) {
                    System.err.println("Σφάλμα στη γραμμή δεδομένων: " + line);
                }
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

    public static String explanationForCountry(String country) {
    switch (country) {
        case "Ιταλία":
            return "Αιτιολόγηση σύγκρισης Ελλάδας – Ιταλίας:\n"
                 + "- Κατά κεφαλήν δαπάνη: Ιταλία ~55.800€, Ελλάδα ~22.000€ (πολύ μεγαλύτερη κλίμακα στην Ιταλία).\n"
                 + "- Έσοδα/Δαπάνες ως % ΑΕΠ: Παρόμοια επίπεδα (47–50%), αλλά η Ιταλία έχει μεγαλύτερο έλλειμμα (-3.3% έναντι -2.3%).\n"
                 + "- Δομή εσόδων: Η Ελλάδα βασίζεται περισσότερο στη φορολογία (44% άμεσοι, 39% έμμεσοι), ενώ η Ιταλία έχει χαμηλότερα ποσοστά.\n"
                 + "- Δαπάνες ανά τομέα: Παρόμοια ποσοστά σε υγεία/παιδεία, αλλά η Ελλάδα διαθέτει μεγαλύτερο ποσοστό για άμυνα.\n"
                 + "- Συνολικά: Η Ιταλία ξοδεύει πολύ περισσότερα ανά πολίτη, ενώ η Ελλάδα έχει πιο βαριά φορολογική επιβάρυνση και μεγαλύτερη έμφαση στην άμυνα.";
        case "Πορτογαλία":
            return "Αιτιολόγηση σύγκρισης Ελλάδας – Πορτογαλίας:\n"
                 + "- Κατά κεφαλήν δαπάνη: Πορτογαλία ~28.300€, Ελλάδα ~22.000€ (περισσότερα ανά πολίτη στην Πορτογαλία).\n"
                 + "- Έσοδα/Δαπάνες ως % ΑΕΠ: Ελλάδα υψηλότερα (47–49%) έναντι Πορτογαλίας (45–45.5%).\n"
                 + "- Ισοζύγιο: Πορτογαλία μικρό πλεόνασμα (0.3%), Ελλάδα έλλειμμα (-2.3%).\n"
                 + "- Χρέος ως % εσόδων: Ελλάδα 7.4, Πορτογαλία 5.4 (μεγαλύτερη πίεση στην Ελλάδα).\n"
                 + "- Δομή εσόδων: Ελλάδα πολύ μεγαλύτερη εξάρτηση από φόρους, Πορτογαλία πιο ισορροπημένη.\n"
                 + "- Δαπάνες ανά τομέα: Ελλάδα πολύ υψηλότερα ποσοστά σε υγεία, παιδεία, άμυνα.\n"
                 + "- Συνολικά: Πορτογαλία πιο ισορροπημένα δημόσια οικονομικά, Ελλάδα μεγαλύτερη έμφαση σε κοινωνικές και αμυντικές δαπάνες.";
        case "Γερμανία":
            return "Αιτιολόγηση σύγκρισης Ελλάδας – Γερμανίας:\n"
                 + "- Κατά κεφαλήν δαπάνη: Γερμανία ~52.800€, Ελλάδα ~22.000€ (υπερδιπλάσια ανά πολίτη στη Γερμανία).\n"
                 + "- Έσοδα/Δαπάνες ως % ΑΕΠ: Σχεδόν ίδια, αλλά Γερμανία μικρότερο έλλειμμα (-1.8% έναντι -2.3%).\n"
                 + "- Χρέος ως % εσόδων: Ελλάδα 7.4, Γερμανία 2.1 (πολύ χαμηλότερο στη Γερμανία).\n"
                 + "- Δομή εσόδων: Ελλάδα μεγαλύτερη εξάρτηση από φόρους, Γερμανία πιο διαφοροποιημένη.\n"
                 + "- Δαπάνες ανά τομέα: Γερμανία επενδύει περισσότερο σε υγεία/παιδεία, Ελλάδα σε άμυνα.\n"
                 + "- Συνολικά: Γερμανία με χαμηλό χρέος και μεγαλύτερη κοινωνική έμφαση, Ελλάδα με περιορισμένους πόρους και γεωπολιτική πίεση.";
        case "ΗΠΑ":
            return "Αιτιολόγηση σύγκρισης Ελλάδας – ΗΠΑ:\n"
                 + "- Κατά κεφαλήν δαπάνη: ΗΠΑ ~83.800€, Ελλάδα ~22.000€ (υπερτριπλάσια ανά πολίτη στις ΗΠΑ).\n"
                 + "- Έσοδα/Δαπάνες ως % ΑΕΠ: Ελλάδα πολύ υψηλότερα (47–49%) έναντι ΗΠΑ (18.7–24.8%).\n"
                 + "- Ισοζύγιο: ΗΠΑ μεγαλύτερο έλλειμμα (-6.1%), Ελλάδα μικρότερο (-2.3%).\n"
                 + "- Χρέος ως % εσόδων: Παρόμοια επίπεδα (7.4 vs 6.4), αλλά οι ΗΠΑ έχουν μεγαλύτερη ευελιξία λόγω δολαρίου.\n"
                 + "- Δομή εσόδων: ΗΠΑ κυριαρχούν οι άμεσοι φόροι (56.6%), Ελλάδα πιο ισορροπημένη μεταξύ άμεσων/έμμεσων.\n"
                 + "- Δαπάνες ανά τομέα: ΗΠΑ πολύ υψηλότερη άμυνα (15% vs 4.7%), Ελλάδα μεγαλύτερη έμφαση στην παιδεία.\n"
                 + "- Συνολικά: ΗΠΑ με μικρότερο κράτος ως % ΑΕΠ αλλά τεράστια στρατιωτική/υγειονομική δαπάνη, Ελλάδα με μεγαλύτερη κρατική παρουσία και έμφαση στην παιδεία.";
        default:
            return "Δεν υπάρχει διαθέσιμη αιτιολόγηση για αυτή τη χώρα.";
    }
}
}
