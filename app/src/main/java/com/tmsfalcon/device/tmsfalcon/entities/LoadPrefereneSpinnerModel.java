package com.tmsfalcon.device.tmsfalcon.entities;

public class LoadPrefereneSpinnerModel {

    public String getTrailer_type() {
        return trailer_type;
    }

    public void setTrailer_type(String trailer_type) {
        this.trailer_type = trailer_type;
    }

    public String getTrailer_type_short_name() {
        return trailer_type_short_name;
    }

    public void setTrailer_type_short_name(String trailer_type_short_name) {
        this.trailer_type_short_name = trailer_type_short_name;
    }

    String trailer_type;

    public LoadPrefereneSpinnerModel(String trailer_type, String trailer_type_short_name) {
        this.trailer_type = trailer_type;
        this.trailer_type_short_name = trailer_type_short_name;
    }

    String trailer_type_short_name;
}
