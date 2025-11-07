package budget_db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetEntryRepository extends JpaRepository<BudgetEntry, Long> {}
