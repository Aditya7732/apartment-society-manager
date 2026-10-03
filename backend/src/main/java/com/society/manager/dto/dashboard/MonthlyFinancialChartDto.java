package com.society.manager.dto.dashboard;

import java.math.BigDecimal;

public class MonthlyFinancialChartDto {
    private String month;
    private BigDecimal collection;
    private BigDecimal expenses;

    public MonthlyFinancialChartDto() {}

    public MonthlyFinancialChartDto(String month, BigDecimal collection, BigDecimal expenses) {
        this.month = month;
        this.collection = collection;
        this.expenses = expenses;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getCollection() { return collection; }
    public void setCollection(BigDecimal collection) { this.collection = collection; }
    public BigDecimal getExpenses() { return expenses; }
    public void setExpenses(BigDecimal expenses) { this.expenses = expenses; }
}
