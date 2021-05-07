package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Dell on 11/16/2018.
 */

public class HosViolationModel {

    public HosViolationModel(String violation, String start, String end) {
        this.violation = violation;
        this.start = start;
        this.end = end;
    }

    String violation;

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    String start;
    String end;

}
