package com.tmsfalcon.device.tmsfalcon.entities;

public class DestinationLocationModel {

    public DestinationLocationModel(String destination_group, String destination_state, String destination_city) {
        this.destination_group = destination_group;
        this.destination_state = destination_state;
        this.destination_city = destination_city;
    }

    String destination_group;

    public String getDestination_group() {
        return destination_group;
    }

    public void setDestination_group(String destination_group) {
        this.destination_group = destination_group;
    }

    public String getDestination_state() {
        return destination_state;
    }

    public void setDestination_state(String destination_state) {
        this.destination_state = destination_state;
    }

    public String getDestination_city() {
        return destination_city;
    }

    public void setDestination_city(String destination_city) {
        this.destination_city = destination_city;
    }

    String destination_state;
    String destination_city;

}
