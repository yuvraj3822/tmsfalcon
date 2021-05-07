package com.tmsfalcon.device.tmsfalcon.entities;

public class AccidentDamageDetailsModel {

    String first_name;
    String last_name;
    String address;
    String phone;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIs_injured() {
        return is_injured;
    }

    public void setIs_injured(boolean is_injured) {
        this.is_injured = is_injured;
    }

    public boolean isIs_deceased() {
        return is_deceased;
    }

    public void setIs_deceased(boolean is_deceased) {
        this.is_deceased = is_deceased;
    }

    public boolean isIs_driver() {
        return is_driver;
    }

    public void setIs_driver(boolean is_driver) {
        this.is_driver = is_driver;
    }

    public boolean isIs_passenger() {
        return is_passenger;
    }

    public void setIs_passenger(boolean is_passenger) {
        this.is_passenger = is_passenger;
    }

    public boolean isIs_bicyclist() {
        return is_bicyclist;
    }

    public void setIs_bicyclist(boolean is_bicyclist) {
        this.is_bicyclist = is_bicyclist;
    }

    public boolean isIs_pedestrian() {
        return is_pedestrian;
    }

    public void setIs_pedestrian(boolean is_pedestrian) {
        this.is_pedestrian = is_pedestrian;
    }

    String email;
    String type;
    boolean is_injured,is_deceased,is_driver,is_passenger,is_bicyclist,is_pedestrian;


}
