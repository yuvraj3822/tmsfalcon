package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Android on 7/18/2017.
 */

public class TruckModel {

    public TruckModel(String id,String thumb, String make, String unit, String model, String vin) {
        this.thumb = thumb;
        this.id = id;
        this.make = make;
        this.unit = unit;
        this.model = model;
        this.vin = vin;
    }

    String thumb;
    String make;
    String unit;
    String model;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    String vin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getMake() {
        return make;
    }

    @SuppressWarnings("unused")
    public void setMake(String make) {
        this.make = make;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getModel() {
        return model;
    }

    @SuppressWarnings("unused")
    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    String year;
}
