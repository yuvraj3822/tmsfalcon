package com.tmsfalcon.device.tmsfalcon.entities;

import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;

/**
 * Created by Android on 7/14/2017.
 */

public class DriverAccidentRecordModel {

    String accident_nature,accident_date,fatalities,injuries;

    public DriverAccidentRecordModel(String accident_nature, String accident_date, String fatalities, String injuries) {
        this.accident_nature = accident_nature;
        this.accident_date = accident_date;
        this.fatalities = fatalities;
        this.injuries = injuries;
    }

    public String getAccident_nature() {
        return CustomValidator.setModelStringData(accident_nature);
        //return accident_nature;
    }

    public void setAccident_nature(String accident_nature) {
        this.accident_nature = accident_nature;
    }

    public String getAccident_date() {
        return CustomValidator.setModelStringData(accident_date);
        //return accident_date;
    }

    public void setAccident_date(String accident_date) {
        this.accident_date = accident_date;
    }

    public String getFatalities() {
        return CustomValidator.setModelStringData(fatalities);
        //return fatalities;
    }

    public void setFatalities(String fatalities) {
        this.fatalities = fatalities;
    }

    public String getInjuries() {
        return CustomValidator.setModelStringData(injuries);
        //return injuries;
    }

    public void setInjuries(String injuries) {
        this.injuries = injuries;
    }
}
