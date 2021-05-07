package com.tmsfalcon.device.tmsfalcon.entities;

import java.io.File;

public class AccidentWitnessFileModel {

    int id;
    String witness_name;
    String witness_phone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public File getAudio_file() {
        return audio_file;
    }

    public void setAudio_file(File audio_file) {
        this.audio_file = audio_file;
    }

    String witness_statement;
    int accident_report_id,driver_id;
    File audio_file;
}
