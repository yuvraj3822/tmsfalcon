package com.tmsfalcon.device.tmsfalcon.entities;

public class ReportedAccidentListModel {

    String accident_date;

    public String getAccident_id() {
        return accident_id;
    }

    public void setAccident_id(String accident_id) {
        this.accident_id = accident_id;
    }

    String accident_id;

    public ReportedAccidentListModel(String accident_id,String accident_date, String accident_time, String accident_location, String accident_status, String accident_detail_url) {
        this.accident_id = accident_id;
        this.accident_date = accident_date;
        this.accident_time = accident_time;
        this.accident_location = accident_location;
        this.accident_status = accident_status;
        this.accident_detail_url = accident_detail_url;
    }

    public ReportedAccidentListModel(String accident_id,String accident_date, String accident_time, String accident_location) {
        this.accident_id = accident_id;
        this.accident_date = accident_date;
        this.accident_time = accident_time;
        this.accident_location = accident_location;
    }

    String accident_time;
    String accident_location;
    String accident_status;

    public String getAccident_date() {
        return accident_date;
    }

    public void setAccident_date(String accident_date) {
        this.accident_date = accident_date;
    }

    public String getAccident_time() {
        return accident_time;
    }

    public void setAccident_time(String accident_time) {
        this.accident_time = accident_time;
    }

    public String getAccident_location() {
        return accident_location;
    }

    public void setAccident_location(String accident_location) {
        this.accident_location = accident_location;
    }

    public String getAccident_status() {
        return accident_status;
    }

    public void setAccident_status(String accident_status) {
        this.accident_status = accident_status;
    }

    public String getAccident_detail_url() {
        return accident_detail_url;
    }

    public void setAccident_detail_url(String accident_detail_url) {
        this.accident_detail_url = accident_detail_url;
    }

    String accident_detail_url;
}
