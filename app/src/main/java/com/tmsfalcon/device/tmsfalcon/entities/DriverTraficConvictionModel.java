package com.tmsfalcon.device.tmsfalcon.entities;

import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;

/**
 * Created by Android on 7/14/2017.
 */

public class DriverTraficConvictionModel {
    String conviction_date,location,charge,penalty;

    public DriverTraficConvictionModel(String conviction_date, String location, String charge, String penalty) {
        this.conviction_date = conviction_date;
        this.location = location;
        this.charge = charge;
        this.penalty = penalty;
    }

    public String getConviction_date() {
        return CustomValidator.setModelStringData(conviction_date);
    }

    @SuppressWarnings("unused")
    public void setConviction_date(String conviction_date) {
        this.conviction_date = conviction_date;
    }

    public String getLocation() {
        return CustomValidator.setModelStringData(location);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCharge() {
        return CustomValidator.setModelStringData(charge);
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getPenalty() {
        return CustomValidator.setModelStringData(penalty);
    }

    @SuppressWarnings("unused")
    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }
}
