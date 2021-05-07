package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Android on 12/11/2017.
 */

public class DayOffResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @SerializedName("total")
    @Expose
    private int total;

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

        @SerializedName("reason")
        @Expose
        private String reason;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("start")
        @Expose
        private String start;
        @SerializedName("end")
        @Expose
        private String end;
        @SerializedName("is_full_off")
        @Expose
        private Integer isFullOff;
        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("is_multiple")
        @Expose
        private String is_multiple;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getDate() {
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

        public Integer getIsFullOff() {
            return isFullOff;
        }

        public void setIsFullOff(Integer isFullOff) {
            this.isFullOff = isFullOff;
        }

        public String getIs_multiple() {
            return is_multiple;
        }

        public void setIs_multiple(String is_multiple) {
            this.is_multiple = is_multiple;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
}
