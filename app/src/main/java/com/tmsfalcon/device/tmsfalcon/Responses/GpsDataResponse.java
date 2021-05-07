package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dell on 1/26/2019.
 */

public class GpsDataResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("messages")
    @Expose
    private List<String> messages = null;

    public Datum getData() {
        return data;
    }

    public void setData(Datum data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose

    private Datum data = null;

    public Boolean getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public class Datum{
        @SerializedName("gps_interval_settings")
        @Expose
        private GpsIntervalSettings gpsIntervalSettings = null;

        public GpsIntervalSettings getGpsIntervalSettings() {
            return gpsIntervalSettings;
        }

        public void setGpsIntervalSettings(GpsIntervalSettings gpsIntervalSettings) {
            this.gpsIntervalSettings = gpsIntervalSettings;
        }
    }

    public class GpsIntervalSettings {

        @SerializedName("off_duty_interval")
        @Expose
        private Integer off_duty_interval;

        @SerializedName("on_duty_interval")
        @Expose
        private Integer on_duty_interval;

        @SerializedName("off_duty_metres")
        @Expose
        private Integer off_duty_metres;

        public Integer getOff_duty_interval() {
            return off_duty_interval;
        }

        public void setOff_duty_interval(Integer off_duty_interval) {
            this.off_duty_interval = off_duty_interval;
        }

        public Integer getOn_duty_interval() {
            return on_duty_interval;
        }

        public void setOn_duty_interval(Integer on_duty_interval) {
            this.on_duty_interval = on_duty_interval;
        }

        public Integer getOff_duty_metres() {
            return off_duty_metres;
        }

        public void setOff_duty_metres(Integer off_duty_metres) {
            this.off_duty_metres = off_duty_metres;
        }

        public Integer getOn_duty_metres() {
            return on_duty_metres;
        }

        public void setOn_duty_metres(Integer on_duty_metres) {
            this.on_duty_metres = on_duty_metres;
        }

        public String getCurrent_status() {
            return current_status;
        }

        public void setCurrent_status(String current_status) {
            this.current_status = current_status;
        }

        @SerializedName("on_duty_metres")

        @Expose
        private Integer on_duty_metres;

        public Boolean getLocation_status() {
            return location_status;
        }

        public void setLocation_status(Boolean location_status) {
            this.location_status = location_status;
        }

        @SerializedName("current_status")
        @Expose
        private String current_status;

        @SerializedName("location_status")
        @Expose
        private Boolean location_status;


    }
}
