package com.tmsfalcon.device.tmsfalcon.entities;

public class EscrowModel {

    String id;
    String title;

    public EscrowModel(String id, String title, String periodic_amount, String payment_schedule, String start_date, String end_date, String balance) {
        this.id = id;
        this.title = title;
        this.periodic_amount = periodic_amount;
        this.payment_schedule = payment_schedule;
        this.start_date = start_date;
        this.end_date = end_date;
        this.balance = balance;
    }

    String periodic_amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPeriodic_amount() {
        return periodic_amount;
    }

    public void setPeriodic_amount(String periodic_amount) {
        this.periodic_amount = periodic_amount;
    }

    public String getPayment_schedule() {
        return payment_schedule;
    }

    public void setPayment_schedule(String payment_schedule) {
        this.payment_schedule = payment_schedule;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getOpening_balance() {
        return opening_balance;
    }

    public void setOpening_balance(String opening_balance) {
        this.opening_balance = opening_balance;
    }

    String payment_schedule;
    String start_date;
    String end_date;
    String balance;
    String opening_balance;
}
