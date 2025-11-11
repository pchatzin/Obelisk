package budget_db;

import java.io.File;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BudgetImporter implements CommandLineRunner {

    private final BudgetEntryRepository repository;

    public BudgetImporter(BudgetEntryRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(BudgetImporter.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        File file = new File("src/main/resources/Κρατικός-Προϋπολογισμός-2025_ΟΕ.pdf");
        List<BudgetEntry> entries = PdfBudgetParser.parse(file);
        System.out.println("Parsed entries: " + entries.size());
        repository.saveAll(entries);
        System.out.println("Saved to database.");
        System.exit(0); // Ensures the app exits after import
    }
}
