package com.tmsfalcon.device.tmsfalcon.entities;

public class AccidentCaptureModel {

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccident_report_id() {
        return accident_report_id;
    }

    public void setAccident_report_id(int accident_report_id) {
        this.accident_report_id = accident_report_id;
    }


    public AccidentCaptureModel(String image_url, String doc_type, int accident_report_id) {
        this.image_url = image_url;
        this.doc_type = doc_type;
        this.accident_report_id = accident_report_id;
    }

    public AccidentCaptureModel(){

    }

    String image_url,doc_type;
    int id;
    int accident_report_id;

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    int driver_id;

}
