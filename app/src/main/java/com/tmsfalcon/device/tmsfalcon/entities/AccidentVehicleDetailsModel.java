package com.tmsfalcon.device.tmsfalcon.entities;

public class AccidentVehicleDetailsModel {

    int id,accident_report_id,driver_id;
    String vehicle_year;
    String vehicle_make;
    String vehicle_unit_no;

    public String getVehicle_year() {
        return vehicle_year;
    }

    public void setVehicle_year(String vehicle_year) {
        this.vehicle_year = vehicle_year;
    }

    public String getVehicle_make() {
        return vehicle_make;
    }

    public void setVehicle_make(String vehicle_make) {
        this.vehicle_make = vehicle_make;
    }

    public String getVehicle_unit_no() {
        return vehicle_unit_no;
    }

    public void setVehicle_unit_no(String vehicle_unit_no) {
        this.vehicle_unit_no = vehicle_unit_no;
    }

    public String getVehicle_license_no() {
        return vehicle_license_no;
    }

    public void setVehicle_license_no(String vehicle_license_no) {
        this.vehicle_license_no = vehicle_license_no;
    }

    public String getVehicle_license_state() {
        return vehicle_license_state;
    }

    public void setVehicle_license_state(String vehicle_license_state) {
        this.vehicle_license_state = vehicle_license_state;
    }

    public String getVehicle_insurance_company() {
        return vehicle_insurance_company;
    }

    public void setVehicle_insurance_company(String vehicle_insurance_company) {
        this.vehicle_insurance_company = vehicle_insurance_company;
    }

    public String getVehicle_insurance_policy_no() {
        return vehicle_insurance_policy_no;
    }

    public void setVehicle_insurance_policy_no(String vehicle_insurance_policy_no) {
        this.vehicle_insurance_policy_no = vehicle_insurance_policy_no;
    }

    public String getVehicle_insurance_policy_holder() {
        return vehicle_insurance_policy_holder;
    }

    public void setVehicle_insurance_policy_holder(String vehicle_insurance_policy_holder) {
        this.vehicle_insurance_policy_holder = vehicle_insurance_policy_holder;
    }

    public String getOwner_first_name() {
        return owner_first_name;
    }

    public void setOwner_first_name(String owner_first_name) {
        this.owner_first_name = owner_first_name;
    }

    public String getOwner_last_name() {
        return owner_last_name;
    }

    public void setOwner_last_name(String owner_last_name) {
        this.owner_last_name = owner_last_name;
    }

    public String getOwner_dob() {
        return owner_dob;
    }

    public void setOwner_dob(String owner_dob) {
        this.owner_dob = owner_dob;
    }

    public String getOwner_city() {
        return owner_city;
    }

    public void setOwner_city(String owner_city) {
        this.owner_city = owner_city;
    }

    public String getOwner_state() {
        return owner_state;
    }

    public void setOwner_state(String owner_state) {
        this.owner_state = owner_state;
    }

    public String getOwner_zipcode() {
        return owner_zipcode;
    }

    public void setOwner_zipcode(String owner_zipcode) {
        this.owner_zipcode = owner_zipcode;
    }

    String vehicle_license_no;
    String vehicle_license_state;
    String vehicle_insurance_company,vehicle_insurance_policy_no,vehicle_insurance_policy_holder;
    String owner_first_name,owner_last_name,owner_dob,owner_city,owner_state,owner_zipcode;

    public String getVehicle_insurance_provider() {
        return vehicle_insurance_provider;
    }

    @Override
    public String toString() {
        return "AccidentVehicleDetailsModel{" +
                "id=" + id +
                ", accident_report_id=" + accident_report_id +
                ", driver_id=" + driver_id +
                ", vehicle_insurance_provider='" + vehicle_insurance_provider + '\'' +
                ", vehicle_insurance_policy_number='" + vehicle_insurance_policy_number + '\'' +
                ", vehicle_dot_number='" + vehicle_dot_number + '\'' +
                ", vehicle_license_number='" + vehicle_license_number + '\'' +
                ", vehicle_registration_number='" + vehicle_registration_number + '\'' +
                ", vehicle_owner_name='" + vehicle_owner_name + '\'' +
                ", vehicle_owner_phone_number='" + vehicle_owner_phone_number + '\'' +
                ", vehicle_owner_insurance_provider='" + vehicle_owner_insurance_provider + '\'' +
                ", vehicle_owner_insurance_policy_number='" + vehicle_owner_insurance_policy_number + '\'' +
                '}';
    }

    public void setVehicle_insurance_provider(String vehicle_insurance_provider) {
        this.vehicle_insurance_provider = vehicle_insurance_provider;
    }

    String vehicle_insurance_provider;

    public AccidentVehicleDetailsModel(){

    }

    public AccidentVehicleDetailsModel(int accident_report_id, int driver_id, String vehicle_insuranc_provider, String vehicle_insurance_policy_number, String vehicle_dot_number, String vehicle_license_number, String vehicle_registration_number, String vehicle_owner_name, String vehicle_owner_phone_number, String vehicle_owner_insurance_provider, String vehicle_owner_insurance_policy_number) {
        this.accident_report_id = accident_report_id;
        this.driver_id = driver_id;
        this.vehicle_insurance_provider = vehicle_insuranc_provider;
        this.vehicle_insurance_policy_number = vehicle_insurance_policy_number;
        this.vehicle_dot_number = vehicle_dot_number;
        this.vehicle_license_number = vehicle_license_number;
        this.vehicle_registration_number = vehicle_registration_number;
        this.vehicle_owner_name = vehicle_owner_name;
        this.vehicle_owner_phone_number = vehicle_owner_phone_number;
        this.vehicle_owner_insurance_provider = vehicle_owner_insurance_provider;
        this.vehicle_owner_insurance_policy_number = vehicle_owner_insurance_policy_number;
    }

    String vehicle_insurance_policy_number;
    String vehicle_dot_number;
    String vehicle_license_number;
    String vehicle_registration_number;
    String vehicle_owner_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccident_report_id() {
        return accident_report_id;
    }

    public void setAccident_report_id(int accident_report_id) {
        this.accident_report_id = accident_report_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }


    public String getVehicle_insurance_policy_number() {
        return vehicle_insurance_policy_number;
    }

    public void setVehicle_insurance_policy_number(String vehicle_insurance_policy_number) {
        this.vehicle_insurance_policy_number = vehicle_insurance_policy_number;
    }

    public String getVehicle_dot_number() {
        return vehicle_dot_number;
    }

    public void setVehicle_dot_number(String vehicle_dot_number) {
        this.vehicle_dot_number = vehicle_dot_number;
    }

    public String getVehicle_license_number() {
        return vehicle_license_number;
    }

    public void setVehicle_license_number(String vehicle_license_number) {
        this.vehicle_license_number = vehicle_license_number;
    }

    public String getVehicle_registration_number() {
        return vehicle_registration_number;
    }

    public void setVehicle_registration_number(String vehicle_registration_number) {
        this.vehicle_registration_number = vehicle_registration_number;
    }

    public String getVehicle_owner_name() {
        return vehicle_owner_name;
    }

    public void setVehicle_owner_name(String vehicle_owner_name) {
        this.vehicle_owner_name = vehicle_owner_name;
    }

    public String getVehicle_owner_phone_number() {
        return vehicle_owner_phone_number;
    }

    public void setVehicle_owner_phone_number(String vehicle_owner_phone_number) {
        this.vehicle_owner_phone_number = vehicle_owner_phone_number;
    }

    public String getVehicle_owner_insurance_provider() {
        return vehicle_owner_insurance_provider;
    }

    public void setVehicle_owner_insurance_provider(String vehicle_owner_insurance_provider) {
        this.vehicle_owner_insurance_provider = vehicle_owner_insurance_provider;
    }

    public String getVehicle_owner_insurance_policy_number() {
        return vehicle_owner_insurance_policy_number;
    }

    public void setVehicle_owner_insurance_policy_number(String vehicle_owner_insurance_policy_number) {
        this.vehicle_owner_insurance_policy_number = vehicle_owner_insurance_policy_number;
    }

    String vehicle_owner_phone_number;
    String vehicle_owner_insurance_provider;
    String vehicle_owner_insurance_policy_number;


}
