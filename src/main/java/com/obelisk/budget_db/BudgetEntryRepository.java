package com.obelisk.budget_db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetEntryRepository extends JpaRepository<BudgetEntry, Long> {
    // προς το παρόν δεν χρειάζονται custom μέθοδοι
}
