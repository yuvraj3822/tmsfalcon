package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Dell on 11/21/2018.
 */

public class FaultCodeModel {

    String code;
    String description;

    public FaultCodeModel(String code, String description, String source, String status, String type, String first_on, String last_on) {
        this.code = code;
        this.description = description;
        this.source = source;
        this.status = status;
        this.type = type;
        this.first_on = first_on;
        this.last_on = last_on;
    }

    String source;
    String status;
    String type;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirst_on() {
        return first_on;
    }

    public void setFirst_on(String first_on) {
        this.first_on = first_on;
    }

    public String getLast_on() {
        return last_on;
    }

    public void setLast_on(String last_on) {
        this.last_on = last_on;
    }

    String first_on;
    String last_on;
}
