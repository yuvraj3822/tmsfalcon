package com.tmsfalcon.device.tmsfalcon.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;

/**
 * Created by Android on 8/24/2017.
 */

public class FuelFragmentModel implements Parcelable {

    String id;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    String method;

    public FuelFragmentModel(String id, String company_id, String fuel_stations, String driver_id, String rebate_schedule, String fuel_station, String rebate_per_gallon) {
        this.id = id;
        this.company_id = company_id;
        this.fuel_stations = fuel_stations;
        this.driver_id = driver_id;
        this.rebate_schedule = rebate_schedule;
        this.fuel_station = fuel_station;
        this.rebate_per_gallon = rebate_per_gallon;
    }
    public FuelFragmentModel(String id, String company_id, String fuel_stations, String driver_id, String rebate_schedule, String fuel_station, String rebate_per_gallon,String method) {
        this.id = id;
        this.company_id = company_id;
        this.fuel_stations = fuel_stations;
        this.driver_id = driver_id;
        this.rebate_schedule = rebate_schedule;
        this.fuel_station = fuel_station;
        this.rebate_per_gallon = rebate_per_gallon;
        this.method = method;
    }

    String company_id;

    protected FuelFragmentModel(Parcel in) {
        id = in.readString();
        company_id = in.readString();
        fuel_stations = in.readString();
        driver_id = in.readString();
        rebate_schedule = in.readString();
        fuel_station = in.readString();
        rebate_per_gallon = in.readString();
        method = in.readString();
    }

    public static final Creator<FuelFragmentModel> CREATOR = new Creator<FuelFragmentModel>() {
        @Override
        public FuelFragmentModel createFromParcel(Parcel in) {
            return new FuelFragmentModel(in);
        }

        @Override
        public FuelFragmentModel[] newArray(int size) {
            return new FuelFragmentModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getCompany_id() {
        return company_id;
    }

    @SuppressWarnings("unused")
    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getFuel_stations() {
        return CustomValidator.setModelStringData(fuel_stations);
    }

    public void setFuel_stations(String fuel_stations) {
        this.fuel_stations = fuel_stations;
    }

    @SuppressWarnings("unused")
    public String getDriver_id() {
        return driver_id;
    }

    @SuppressWarnings("unused")
    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getRebate_schedule() {
        return CustomValidator.setModelStringData(rebate_schedule);
    }

    @SuppressWarnings("unused")
    public void setRebate_schedule(String rebate_schedule) {
        this.rebate_schedule = rebate_schedule;
    }

    public String getFuel_station() {
        return fuel_station;
    }

    public void setFuel_station(String fuel_station) {
        this.fuel_station = fuel_station;
    }

    public String getRebate_per_gallon() {
        return CustomValidator.setModelStringData(rebate_per_gallon);
    }

    public void setRebate_per_gallon(String rebate_per_gallon) {
        this.rebate_per_gallon = rebate_per_gallon;
    }

    String fuel_stations;
    String driver_id;
    String rebate_schedule;
    String fuel_station;
    String rebate_per_gallon;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(company_id);
        parcel.writeString(fuel_stations);
        parcel.writeString(driver_id);
        parcel.writeString(rebate_schedule);
        parcel.writeString(fuel_station);
        parcel.writeString(rebate_per_gallon);
        parcel.writeString(method);
    }
}
