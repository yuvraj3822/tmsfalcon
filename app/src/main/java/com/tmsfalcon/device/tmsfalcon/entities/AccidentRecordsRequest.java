package com.tmsfalcon.device.tmsfalcon.entities;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;

public class AccidentRecordsRequest {

    public AccidentRecordsRequest() {
    }

    @Override
    public String toString() {
        return "AccidentRecordsRequest{" +
                "accidentBasicDetailsModel=" + accidentBasicDetailsModel +
                ", accidentVehicleDetailsModelArrayList=" + accidentVehicleDetailsModelArrayList +
                ", accidentWitnessModelArrayList=" + accidentWitnessModelArrayList +
                '}';
    }

    public AccidentBasicDetailsModel getAccidentBasicDetailsModel() {
        return accidentBasicDetailsModel;
    }

    public void setAccidentBasicDetailsModel(AccidentBasicDetailsModel accidentBasicDetailsModel) {
        this.accidentBasicDetailsModel = accidentBasicDetailsModel;
    }

    AccidentBasicDetailsModel accidentBasicDetailsModel;

    public ArrayList<AccidentVehicleDetailsModel> getAccidentVehicleDetailsModelArrayList() {
        return accidentVehicleDetailsModelArrayList;
    }

    public void setAccidentVehicleDetailsModelArrayList(ArrayList<AccidentVehicleDetailsModel> accidentVehicleDetailsModelArrayList) {
        this.accidentVehicleDetailsModelArrayList = accidentVehicleDetailsModelArrayList;
    }

    ArrayList<AccidentVehicleDetailsModel> accidentVehicleDetailsModelArrayList;


    public ArrayList<AccidentWitnessModel> getAccidentWitnessModelArrayList() {
        return accidentWitnessModelArrayList;
    }

    public void setAccidentWitnessModelArrayList(ArrayList<AccidentWitnessModel> accidentWitnessModelArrayList) {
        this.accidentWitnessModelArrayList = accidentWitnessModelArrayList;
    }

    ArrayList<AccidentWitnessModel> accidentWitnessModelArrayList;


}
