package com.tmsfalcon.device.tmsfalcon.entities;

public class AccidentWitnessModel {

    String witness_name;
    String witness_audio_url;

    public String getTemp_audio_name() {
        return temp_audio_name;
    }

    public void setTemp_audio_name(String temp_audio_name) {
        this.temp_audio_name = temp_audio_name;
    }

    String temp_audio_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AccidentWitnessModel{" +
                "witness_name='" + witness_name + '\'' +
                ", witness_audio_url='" + witness_audio_url + '\'' +
                ", id=" + id +
                ", witness_phone='" + witness_phone + '\'' +
                ", witness_statement='" + witness_statement + '\'' +
                ", accident_report_id=" + accident_report_id +
                ", driver_id=" + driver_id +
                '}';
    }

    int id;

    public AccidentWitnessModel(long id,String witness_name, String witness_phone, String witness_statement, int accident_report_id,
                                int driver_id,String witness_audio_url ) {
        this.witness_name = witness_name;
        this.witness_phone = witness_phone;
        this.witness_statement = witness_statement;
        this.accident_report_id = accident_report_id;
        this.driver_id = driver_id;
        this.witness_audio_url = witness_audio_url;
        this.id = (int) id;
    }

    public String getWitness_audio_url() {
        return witness_audio_url;
    }

    public void setWitness_audio_url(String witness_audio_url) {
        this.witness_audio_url = witness_audio_url;
    }

    String witness_phone;

    public AccidentWitnessModel() {
    }

    public String getWitness_name() {
        return witness_name;
    }

    public void setWitness_name(String witness_name) {
        this.witness_name = witness_name;
    }

    public String getWitness_phone() {
        return witness_phone;
    }

    public void setWitness_phone(String witness_phone) {
        this.witness_phone = witness_phone;
    }

    public String getWitness_statement() {
        return witness_statement;
    }

    public void setWitness_statement(String witness_statement) {
        this.witness_statement = witness_statement;
    }

    public int getAccident_report_id() {
        return accident_report_id;
    }

    public void setAccident_report_id(int accident_report_id) {
        this.accident_report_id = accident_report_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    String witness_statement;
    int accident_report_id,driver_id;
}
