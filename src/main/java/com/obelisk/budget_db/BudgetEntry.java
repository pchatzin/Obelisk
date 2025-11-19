package com.obelisk.budget_db;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "budget_entries")
public class BudgetEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer lineNumber;
    private String type;
    private BigDecimal amount;
    private String ministry;

    @Column(length = 2000)
    private String source;

    public BudgetEntry() {}

    public BudgetEntry(Integer lineNumber, String type, BigDecimal amount, String ministry, String source) {
        this.lineNumber = lineNumber;
        this.type = type;
        this.amount = amount;
        this.ministry = ministry;
        this.source = source;
    }

    public Long getId() { return id; }
    public Integer getLineNumber() { return lineNumber; }
    public void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMinistry() { return ministry; }
    public void setMinistry(String ministry) { this.ministry = ministry; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
