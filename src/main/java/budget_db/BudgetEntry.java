import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class BudgetEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Πρωτογενή στοιχεία
    private String code;                // Κωδικός εσόδου ή δαπάνης (π.χ. 1211, 2111)
    private String description;         // Περιγραφή κατηγορίας (π.χ. ΦΠΑ, Υγεία)
    private String type;                // "REVENUE" ή "EXPENDITURE"
    private String period;              // Χρονική περίοδος (π.χ. "Σεπτέμβριος 2025")

    // Ποσά
    private BigDecimal plannedAmount;   // Προϋπολογισθέν ποσό
    private BigDecimal actualAmount;    // Πραγματοποιηθέν ποσό
    private BigDecimal deviationAmount; // Απόκλιση = actual - planned
    private BigDecimal deviationPercent;// Ποσοστό απόκλισης

    // Φορέας
    private String agencyCode;          // Κωδικός Υπουργείου ή Φορέα (π.χ. 1001)
    private String agencyName;          // Όνομα Υπουργείου (π.χ. Υπουργείο Υγείας)

    // Πηγή χρηματοδότησης και κατηγοριοποίηση
    private String fundingSource;       // Πηγή χρηματοδότησης (π.χ. ΕΣΠΑ, Εθνικοί Πόροι)
    private String budgetCategory;      // Κατηγορία προϋπολογισμού (π.χ. "Τακτικός", "ΠΔΕ")
    private String entryGroup;          // Ομαδοποίηση (π.χ. "Φόροι", "Δαπάνες Υγείας")

    // Προαιρετικά σχόλια
    private String notes;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public BigDecimal getPlannedAmount() { return plannedAmount; }
    public void setPlannedAmount(BigDecimal plannedAmount) { this.plannedAmount = plannedAmount; }

    public BigDecimal getActualAmount() { return actualAmount; }
    public void setActualAmount(BigDecimal actualAmount) { this.actualAmount = actualAmount; }

    public BigDecimal getDeviationAmount() { return deviationAmount; }
    public void setDeviationAmount(BigDecimal deviationAmount) { this.deviationAmount = deviationAmount; }

    public BigDecimal getDeviationPercent() { return deviationPercent; }
    public void setDeviationPercent(BigDecimal deviationPercent) { this.deviationPercent = deviationPercent; }

    public String getAgencyCode() { return agencyCode; }
    public void setAgencyCode(String agencyCode) { this.agencyCode = agencyCode; }

    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

    public String getFundingSource() { return fundingSource; }
    public void setFundingSource(String fundingSource) { this.fundingSource = fundingSource; }

    public String getBudgetCategory() { return budgetCategory; }
    public void setBudgetCategory(String budgetCategory) { this.budgetCategory = budgetCategory; }

    public String getEntryGroup() { return entryGroup; }
    public void setEntryGroup(String entryGroup) { this.entryGroup = entryGroup; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
