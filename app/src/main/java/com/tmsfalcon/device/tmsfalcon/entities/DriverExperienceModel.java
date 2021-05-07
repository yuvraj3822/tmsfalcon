package com.tmsfalcon.device.tmsfalcon.entities;

import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;

/**
 * Created by Android on 7/13/2017.
 */

public class DriverExperienceModel {
    String equipment_class,equipment_type,from_date,to_date,total_miles;

    public DriverExperienceModel(String equipment_class, String equipment_type, String from_date, String to_date, String total_miles) {
        this.equipment_class = equipment_class;
        this.equipment_type = equipment_type;
        this.from_date = from_date;
        this.to_date = to_date;
        this.total_miles = total_miles;
    }

    public String getEquipment_class() {
        return CustomValidator.setModelStringData(equipment_class);
        //return equipment_class;
    }

    public void setEquipment_class(String equipment_class) {
        this.equipment_class = equipment_class;
    }

    public String getEquipment_type() {
        return CustomValidator.setModelStringData(equipment_type);
        //return equipment_type;
    }

    public void setEquipment_type(String equipment_type) {
        this.equipment_type = equipment_type;
    }

    public String getFrom_date() {
        return CustomValidator.setModelStringData(from_date);
        //return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return CustomValidator.setModelStringData(to_date);
        //return to_date;
    }

    @SuppressWarnings("unused")
    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getTotal_miles() {
        return CustomValidator.setModelStringData(total_miles);
        //return total_miles;
    }

    public void setTotal_miles(String total_miles) {
        this.total_miles = total_miles;
    }
}
