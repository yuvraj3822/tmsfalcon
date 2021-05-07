package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Dell on 4/4/2019.
 */

public class RecentCallsModel {
    String id;
    String dialer_name;

    public RecentCallsModel(){

    }

    public RecentCallsModel(String dialer_name, String dialer_phone, String dialer_ext, String dialer_call_type, String dialer_time) {
        this.dialer_name = dialer_name;
        this.dialer_phone = dialer_phone;
        this.dialer_ext = dialer_ext;
        this.dialer_call_type = dialer_call_type;
        this.dialer_time = dialer_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDialer_name() {
        return dialer_name;
    }

    public void setDialer_name(String dialer_name) {
        this.dialer_name = dialer_name;
    }

    public String getDialer_phone() {
        return dialer_phone;
    }

    public void setDialer_phone(String dialer_phone) {
        this.dialer_phone = dialer_phone;
    }

    public String getDialer_ext() {
        return dialer_ext;
    }

    public void setDialer_ext(String dialer_ext) {
        this.dialer_ext = dialer_ext;
    }

    public String getDialer_call_type() {
        return dialer_call_type;
    }

    public void setDialer_call_type(String dialer_call_type) {
        this.dialer_call_type = dialer_call_type;
    }

    public String getDialer_time() {
        return dialer_time;
    }

    public void setDialer_time(String dialer_time) {
        this.dialer_time = dialer_time;
    }

    String dialer_phone;
    String dialer_ext;
    String dialer_call_type;
    String dialer_time;

}
