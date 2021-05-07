package com.tmsfalcon.device.tmsfalcon.entities;

public class CashHistoryModel {

    public CashHistoryModel(String history_id, String trip_id, String cash_for, String issued_date, String amount, String code,String status) {
        this.history_id = history_id;
        this.trip_id = trip_id;
        this.cash_for = cash_for;
        this.issued_date = issued_date;
        this.amount = amount;
        this.code = code;
        this.status = status;
    }

    String history_id;
    String trip_id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public String getHistory_id() {
        return history_id;
    }

    public void setHistory_id(String history_id) {
        this.history_id = history_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getCash_for() {
        return cash_for;
    }

    public void setCash_for(String cash_for) {
        this.cash_for = cash_for;
    }

    public String getIssued_date() {
        return issued_date;
    }

    public void setIssued_date(String issued_date) {
        this.issued_date = issued_date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String cash_for;
    String issued_date;
    String amount;
    String code;

}
