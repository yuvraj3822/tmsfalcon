package com.tmsfalcon.device.tmsfalcon.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dell on 2/21/2019.
 */

public class GpsLocationParams {

    @SerializedName("dt")
    String dt;

    @SerializedName("lat")
    String lat;

    @SerializedName("lng")
    String lng;

    @SerializedName("altitude")
    String altitude;

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getLoc_valid() {
        return loc_valid;
    }

    public void setLoc_valid(String loc_valid) {
        this.loc_valid = loc_valid;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }


    @SerializedName("angle")
    String angle;

    @SerializedName("speed")
    String speed;

    @SerializedName("loc_valid")
    String loc_valid;

    @SerializedName("params")
    String params;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
}
