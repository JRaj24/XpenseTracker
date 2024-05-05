package com.example.budgetplanner;

public class Goal {



    String gName;
    String gAmount;
    String gDate,gSaving;

    public String getgName() {
        return gName;
    }

    public Goal() {
    }

    public void setgName(String gName) {
        this.gName = gName;
    }



    public Goal(String gName, String gAmount, String gDate, String gSaving) {
        this.gName = gName;
        this.gAmount = gAmount;
        this.gDate = gDate;
        this.gSaving = gSaving;
    }

    public String getgAmount() {
        return gAmount;
    }

    public void setgAmount(String gAmount) {
        this.gAmount = gAmount;
    }

    public String getgDate() {
        return gDate;
    }

    public void setgDate(String gDate) {
        this.gDate = gDate;
    }

    public String getgSaving() {
        return gSaving;
    }

    public void setgSaving(String gSaving) {
        this.gSaving = gSaving;
    }


}
