package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dell on 3/19/2018.
 */

public class TripResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("messages")
    @Expose
    private List<Object> messages = null;
    /*@SerializedName("data")
    @Expose
    private List<Datum> data = null;*/

    public Boolean getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /*public List<Datum> getData() {
        return data;
    }*/

    public List<Object> getMessages() {
        return messages;
    }

    @SuppressWarnings("unused")
    public void setMessages(List<Object> messages) {
        this.messages = messages;
    }

    @SuppressWarnings("unused")
   /* public void setData(List<Datum> data) {
        this.data = data;
    }*/

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

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        @SerializedName("start_location")
        @Expose
        private String start_location;
        @SerializedName("end_location")
        @Expose
        private String end_location;

        @SerializedName("trip_id")
        @Expose
        private Integer trip_id;

        @SerializedName("stops")
        @Expose
        private Integer stops;

        @SerializedName("loads")
        @Expose
        private Integer loads;

        @SerializedName("distance")
        @Expose
        private Integer distance;



    }
}
