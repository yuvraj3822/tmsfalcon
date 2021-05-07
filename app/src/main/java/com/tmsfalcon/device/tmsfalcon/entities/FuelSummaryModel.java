package com.tmsfalcon.device.tmsfalcon.entities;

public class FuelSummaryModel {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getFuel_station_name() {
        return fuel_station_name;
    }

    public void setFuel_station_name(String fuel_station_name) {
        this.fuel_station_name = fuel_station_name;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getRebate_amount() {
        return rebate_amount;
    }

    public void setRebate_amount(String rebate_amount) {
        this.rebate_amount = rebate_amount;
    }

    public String getRebate_method() {
        return rebate_method;
    }

    public void setRebate_method(String rebate_method) {
        this.rebate_method = rebate_method;
    }

    public String getRebate_method_value() {
        return rebate_method_value;
    }

    public void setRebate_method_value(String rebate_method_value) {
        this.rebate_method_value = rebate_method_value;
    }

    public String getTotal_galloons() {
        return total_galloons;
    }

    public void setTotal_galloons(String total_galloons) {
        this.total_galloons = total_galloons;
    }

    String id;
    String transaction_id;
    String fuel_station_name;

    public FuelSummaryModel(String id, String transaction_id, String fuel_station_name, String transaction_date, String rebate_amount, String rebate_method, String rebate_method_value, String total_galloons) {
        this.id = id;
        this.transaction_id = transaction_id;
        this.fuel_station_name = fuel_station_name;
        this.transaction_date = transaction_date;
        this.rebate_amount = rebate_amount;
        this.rebate_method = rebate_method;
        this.rebate_method_value = rebate_method_value;
        this.total_galloons = total_galloons;
    }

    String transaction_date;
    String rebate_amount;
    String rebate_method;
    String rebate_method_value;
    String total_galloons;
}
