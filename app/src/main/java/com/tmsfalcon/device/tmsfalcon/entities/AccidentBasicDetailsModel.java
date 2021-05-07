package com.tmsfalcon.device.tmsfalcon.entities;

public class AccidentBasicDetailsModel {

    int id;

    @Override
    public String toString() {
        return "AccidentBasicDetailsModel{" +
                "id=" + id +
                ", driver_id=" + driver_id +
                ", accident_lat='" + accident_lat + '\'' +
                ", accident_long='" + accident_long + '\'' +
                ", accident_location='" + accident_location + '\'' +
                ", accident_date='" + accident_date + '\'' +
                ", accident_time='" + accident_time + '\'' +
                ", employer_name='" + employer_name + '\'' +
                ", employer_phone_number='" + employer_phone_number + '\'' +
                ", employer_insurance_provider='" + employer_insurance_provider + '\'' +
                ", employer_insurance_policy_number='" + employer_insurance_policy_number + '\'' +
                '}';
    }

    public AccidentBasicDetailsModel(){

    }

    public AccidentBasicDetailsModel(int driver_id, String accident_lat, String accident_long, String accident_location, String accident_date, String accident_time, String employer_name, String employer_phone_number, String employer_insurance_provider, String employer_insurance_policy_number) {
        this.driver_id = driver_id;
        this.accident_lat = accident_lat;
        this.accident_long = accident_long;
        this.accident_location = accident_location;
        this.accident_date = accident_date;
        this.accident_time = accident_time;
        this.employer_name = employer_name;
        this.employer_phone_number = employer_phone_number;
        this.employer_insurance_provider = employer_insurance_provider;
        this.employer_insurance_policy_number = employer_insurance_policy_number;
    }

    int driver_id;
    String accident_lat;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getAccident_lat() {
        return accident_lat;
    }

    public void setAccident_lat(String accident_lat) {
        this.accident_lat = accident_lat;
    }

    public String getAccident_long() {
        return accident_long;
    }

    public void setAccident_long(String accident_long) {
        this.accident_long = accident_long;
    }

    public String getAccident_location() {
        return accident_location;
    }

    public void setAccident_location(String accident_location) {
        this.accident_location = accident_location;
    }

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

    public String getEmployer_name() {
        return employer_name;
    }

    public void setEmployer_name(String employer_name) {
        this.employer_name = employer_name;
    }

    public String getEmployer_phone_number() {
        return employer_phone_number;
    }

    public void setEmployer_phone_number(String employer_phone_number) {
        this.employer_phone_number = employer_phone_number;
    }

    public String getEmployer_insurance_provider() {
        return employer_insurance_provider;
    }

    public void setEmployer_insurance_provider(String employer_insurance_provider) {
        this.employer_insurance_provider = employer_insurance_provider;
    }

    public String getEmployer_insurance_policy_number() {
        return employer_insurance_policy_number;
    }

    public void setEmployer_insurance_policy_number(String employer_insurance_policy_number) {
        this.employer_insurance_policy_number = employer_insurance_policy_number;
    }

    String accident_long;
    String accident_location;
    String accident_date;
    String accident_time;
    String employer_name,employer_phone_number,employer_insurance_provider,employer_insurance_policy_number;

}
