package com.example.budgetingapp;

public class Data {

    String item, date, id, notes;
    int amount, month;

    public Data() {
    }

    public Data(String item, String date, String id, String notes, int amount, int month) {
        this.item = item;
        this.date = date;
        this.id = id;
        this.notes = notes;
        this.amount = amount;
        this.month = month;
    }

    public String getItem() {
        return item;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getNotes() {
        return notes;
    }

    public int getAmount() {
        return amount;
    }

    public int getMonth() {
        return month;
    }
}
