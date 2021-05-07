package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Dell on 9/16/2019.
 */

public class SuggestedLoadsModel {
    String call_log_id;
    String origin;

    public SuggestedLoadsModel(String call_log_id, String origin, String destination, String price, String pm, String miles, String status, String my_response, String public_link, boolean is_action_visible, boolean is_awarded) {
        this.call_log_id = call_log_id;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
        this.pm = pm;
        this.miles = miles;
        this.status = status;
        this.my_response = my_response;
        this.public_link = public_link;
        this.is_action_visible = is_action_visible;
        this.is_awarded = is_awarded;
    }

    public String getCall_log_id() {
        return call_log_id;
    }

    public void setCall_log_id(String call_log_id) {
        this.call_log_id = call_log_id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMy_response() {
        return my_response;
    }

    public void setMy_response(String my_response) {
        this.my_response = my_response;
    }

    public String getPublic_link() {
        return public_link;
    }

    public void setPublic_link(String public_link) {
        this.public_link = public_link;
    }

    public boolean isIs_action_visible() {
        return is_action_visible;
    }

    public void setIs_action_visible(boolean is_action_visible) {
        this.is_action_visible = is_action_visible;
    }

    public boolean isIs_awarded() {
        return is_awarded;
    }

    public void setIs_awarded(boolean is_awarded) {
        this.is_awarded = is_awarded;
    }

    String destination;
    String price;
    String pm;
    String miles;
    String status;
    String my_response;
    String public_link;
    boolean is_action_visible,is_awarded;
}
