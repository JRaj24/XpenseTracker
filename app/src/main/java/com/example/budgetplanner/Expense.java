package com.example.budgetplanner;

import java.time.LocalDate;

public class Expense {

    String eName,eAmount;
    String eDate,eMonthyear;

    public String geteMonthyear() {
        return eMonthyear;
    }

    public void seteMonthyear(String eMonthyear) {
        this.eMonthyear = eMonthyear;
    }

    public Expense() {
    }

    public Expense(String eName, String eAmount, String eDate, String eMonthyear) {
        this.eName = eName;
        this.eAmount = eAmount;
        this.eDate = eDate;
        this.eMonthyear = eMonthyear;
    }

    public Expense(String eName, String eAmount, String eDate) {
        this.eName = eName;
        this.eAmount = eAmount;
        this.eDate = eDate;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String geteAmount() {
        return eAmount;
    }

    public void seteAmount(String eAmount) {
        this.eAmount = eAmount;
    }

    public String geteDate() {
        return eDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }
}
