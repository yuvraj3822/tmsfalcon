package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tmsfalcon.device.tmsfalcon.entities.TripItineraryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 3/26/2018.
 */

public class TripDetailResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("messages")
    @Expose
    private List<Object> messages = null;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public Boolean getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Datum> getData() {
        return data;
    }

    public List<Object> getMessages() {
        return messages;
    }

    @SuppressWarnings("unused")
    public void setMessages(List<Object> messages) {
        this.messages = messages;
    }

    @SuppressWarnings("unused")
    public void setData(List<Datum> data) {
        this.data = data;
    }

    public class Datum {

        public String getStart_location() {
            return start_location;
        }

        public void setStart_location(String start_location) {
            this.start_location = start_location;
        }

        public String getEnd_location() {
            return end_location;
        }

        public void setEnd_location(String end_location) {
            this.end_location = end_location;
        }


        public Integer getTrip_id() {
            return trip_id;
        }

        public void setTrip_id(Integer trip_id) {
            this.trip_id = trip_id;
        }

        public Integer getStops() {
            return stops;
        }

        public void setStops(Integer stops) {
            this.stops = stops;
        }

        public Integer getLoads() {
            return loads;
        }

        public void setLoads(Integer loads) {
            this.loads = loads;
        }

        public Float getDistance() {
            return distance;
        }

        public void setDistance(Float distance) {
            this.distance = distance;
        }

        @SerializedName("start_location")
        @Expose
        private String start_location;
        @SerializedName("end_location")
        @Expose
        private String end_location;
        @SerializedName("start_date")
        @Expose
        private String start_date;
        @SerializedName("end_date")
        @Expose
        private String end_date;
        @SerializedName("trip_id")
        @Expose
        private Integer trip_id;


        public Integer getIsBatchSettlement() {
            return isBatchSettlement;
        }

        public void setIsBatchSettlement(Integer isBatchSettlement) {
            this.isBatchSettlement = isBatchSettlement;
        }

        @SerializedName("is_batch_settlement")
        @Expose
        private Integer isBatchSettlement;

        @SerializedName("type")
        @Expose
        private String type;

        @SerializedName("trip_number")
        @Expose
        private String trip_number;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTrip_number() {
            return trip_number;
        }

        public void setTrip_number(String trip_number) {
            this.trip_number = trip_number;
        }

        public String getStart_date() {
           String finalStartDate = start_date.replace("@"," @");
            return finalStartDate;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            String finalEndDate = end_date.replace("@"," @");
            return finalEndDate;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public ArrayList<TripItineraryModel> getLocations() {
            return locations;
        }

        public void setLocations(ArrayList<TripItineraryModel> locations) {
            this.locations = locations;
        }

        @SerializedName("stops")
        @Expose
        private Integer stops;

        @SerializedName("loads")
        @Expose
        private Integer loads;

        @SerializedName("distance")
        @Expose
        private Float distance;
        @SerializedName("locations")
        @Expose
        private ArrayList<TripItineraryModel> locations;


    }
    public class TripLocation{

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("location")
        @Expose
        private String location;

        @SerializedName("city")
        @Expose
        private String city;

        @SerializedName("state")
        @Expose
        private String state;

        @SerializedName("type")
        @Expose
        private String type;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("date")
        @Expose
        private String date;

        @SerializedName("date_parsed")
        @Expose
        private String date_parsed;

        @SerializedName("time")
        @Expose
        private String time;

        @SerializedName("timestamp")
        @Expose
        private Integer timestamp;

        @SerializedName("is_shipper_consignee")
        @Expose
        private Integer is_shipper_consignee;

        @SerializedName("end_time")
        @Expose
        private String end_time;

        @SerializedName("zipcode")
        @Expose
        private String zipcode;

        @SerializedName("contact_person")
        @Expose
        private String contact_person;

        @SerializedName("bol")
        @Expose
        private String bol;

        @SerializedName("phone")
        @Expose
        private String phone;

        @SerializedName("fax")
        @Expose
        private String fax;

        @SerializedName("instructions")
        @Expose
        private String instructions;


    }
}
