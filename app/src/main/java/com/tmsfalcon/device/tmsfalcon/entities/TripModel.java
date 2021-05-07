package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Dell on 3/20/2018.
 */

public class TripModel {

    private  String trip_no;
    private String start_location;
    private String end_location;
    private Integer trip_id;
    private Integer stops;
    private Integer loads;
    private Double distance;
    private String type;


    public void setTripNo(String trip_no){
        this.trip_no = trip_no;
    }

    public String getTripNo() {
        return trip_no;
    }

    public void setTypess(String type){
        this.type = type;
    }

    public String getTypess() {
        return type;
    }

    public String getStart_time() {
       String finalStartTime =  start_time.replace("@"," @");
        return finalStartTime;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {

        String finalEndTime =  end_time.replace("@"," @");
        return finalEndTime;

    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    private String start_time;
    private String end_time;

    public TripModel(String start_location, String end_location, Integer trip_id, Integer stops, Integer loads, String type,String trip_no,Double distance,String start_time,String end_time) {
        this.start_location = start_location;
        this.end_location = end_location;
        this.trip_id = trip_id;
        this.stops = stops;
        this.loads = loads;
        this.type = type;
        this.trip_no = trip_no;
        this.distance = distance;
        this.start_time = start_time;
        this.end_time = end_time;
    }




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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
