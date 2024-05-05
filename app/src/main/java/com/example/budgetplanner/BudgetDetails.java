package com.example.budgetplanner;

public class BudgetDetails {


        private String bName;

        public String getbName() {
            return bName;
        }

        public void setbName(String bName) {
            this.bName = bName;
        }

    @Override
    public String toString() {
        return "BudgetDetails{" +
                "bName='" + bName + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", amount=" + amount +
                '}';
    }

    private String month;
        private String year;
        private int amount;

        public BudgetDetails() {
            // Required empty constructor
        }

        public BudgetDetails(String month, String year, int amount) {
            this.month = month;
            this.year = year;
            this.amount = amount;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }


