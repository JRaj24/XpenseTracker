package com.example.budgetplanner;

import java.util.Map;

public class Budget {

    private String monthYear;
    private Map<String, BudgetDetails> budgetDetailsMap;

    public Budget() {
        // Required empty constructor
    }

    public Budget(String monthYear, Map<String, BudgetDetails> budgetDetailsMap) {
        this.monthYear = monthYear;
        this.budgetDetailsMap = budgetDetailsMap;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public Map<String, BudgetDetails> getBudgetDetailsMap() {
        return budgetDetailsMap;
    }

    public void setBudgetDetailsMap(Map<String, BudgetDetails> budgetDetailsMap) {
        this.budgetDetailsMap = budgetDetailsMap;
    }
}


