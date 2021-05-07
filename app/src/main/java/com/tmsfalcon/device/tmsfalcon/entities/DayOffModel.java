package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Android on 12/12/2017.
 */

public class DayOffModel {

    int is_full_off;

    public String getIs_multiple() {
        return is_multiple;
    }

    public void setIs_multiple(String is_multiple) {
        this.is_multiple = is_multiple;
    }

    String is_multiple;

    public DayOffModel(int is_full_off, String reason, String date, String start, String end, String status, String is_multiple) {
        this.is_full_off = is_full_off;
        this.reason = reason;
        this.date = date;
        this.start = start;
        this.end = end;
        this.status = status;
        this.is_multiple = is_multiple;
    }

    public int getIs_full_off() {
        return is_full_off;
    }

    public void setIs_full_off(int is_full_off) {
        this.is_full_off = is_full_off;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate()  {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    @SuppressWarnings("unused")
    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String reason,date,start,end,status;
}
