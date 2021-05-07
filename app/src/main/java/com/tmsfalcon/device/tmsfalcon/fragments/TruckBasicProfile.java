package com.tmsfalcon.device.tmsfalcon.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class TruckBasicProfile extends Fragment {

    JSONObject truckDetailJson;
    TextView unitNo,model,make,year,color,license_plate,license_expiration,registered_state,
            city_state,zipcode,available_date,lat_long,
            year_purchased,purchase_price,weight,height,fuel_type,no_of_axles,gross_weight,current_odometer,current_ewh,tank_capacity,tank_reserve,front_tyre_size,rear_tyre_size,description,
            insurance_name,insurance_expiration,insurance_policy_number,excluded_state,vin,serial;
    public TruckBasicProfile() {
        // Required empty public constructor
    }
    private void initIds(View view) {
        //Basic
        unitNo = view.findViewById(R.id.unit_number);
        model = view.findViewById(R.id.model);
        make = view.findViewById(R.id.make);
        year = view.findViewById(R.id.year);
        color = view.findViewById(R.id.color);
        license_plate = view.findViewById(R.id.license_plate_number);
        license_expiration = view.findViewById(R.id.license_expiration);
        registered_state = view.findViewById(R.id.resgistered_state);

        //Location
        city_state = view.findViewById(R.id.city_state);
        zipcode = view.findViewById(R.id.zipcode);
        available_date = view.findViewById(R.id.available_date);
        lat_long = view.findViewById(R.id.latitude_longitude);

        //Vehicle Details
        vin = view.findViewById(R.id.vin);
        serial = view.findViewById(R.id.serial_number);
        //year_purchased = (TextView) view.findViewById(R.id.year_purchased);
        //purchase_price = (TextView) view.findViewById(R.id.purchase_price);
        weight = view.findViewById(R.id.weight);
        height = view.findViewById(R.id.height);
        fuel_type = view.findViewById(R.id.fuel_type);
        no_of_axles = view.findViewById(R.id.no_of_axles);
        gross_weight = view.findViewById(R.id.gross_weight);
        current_odometer = view.findViewById(R.id.current_odometer);
        current_ewh = view.findViewById(R.id.current_ewh);
        tank_capacity = view.findViewById(R.id.tank_capacity);
        tank_reserve = view.findViewById(R.id.tank_reserve);
        front_tyre_size = view.findViewById(R.id.front_tyre_size);
        rear_tyre_size = view.findViewById(R.id.rear_tyre_size);
        description = view.findViewById(R.id.description);

        //Insurance
        insurance_name = view.findViewById(R.id.insurance_name);
        insurance_expiration = view.findViewById(R.id.insurance_expiration);
        insurance_policy_number = view.findViewById(R.id.insurance_policy_number);
        excluded_state = view.findViewById(R.id.excluded_state);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_truck_basic_profile, container, false);
        try {
            truckDetailJson = new JSONObject(getArguments().getString("truck_detail"));
        } catch (JSONException e) {
             Log.e("exception ", String.valueOf(e));
        }
        initIds(view);
        if(truckDetailJson != null){
            setText();
        }
        else{
            
        }

        return  view;
    }

    private void setText() {
        try {
            //Basic
            CustomValidator.setCustomText(unitNo,truckDetailJson.getString("unit_number"), (View) unitNo.getParent());
            CustomValidator.setCustomText(model,truckDetailJson.getString("model"), (View) model.getParent());
            CustomValidator.setCustomText(make,truckDetailJson.getString("make"), (View) make.getParent());
            CustomValidator.setCustomText(year,truckDetailJson.getString("year"), (View) year.getParent());
            CustomValidator.setCustomText(license_plate,truckDetailJson.getString("license_plate_number"), (View) license_plate.getParent());
            CustomValidator.setCustomText(license_expiration,truckDetailJson.getString("license_expiration"), (View) license_expiration.getParent());
            CustomValidator.setCustomText(registered_state,truckDetailJson.getString("registered_state"), (View) registered_state.getParent());
            CustomValidator.setCustomText(color,truckDetailJson.getString("color"), (View) color.getParent());


            //Location
            CustomValidator.setCombinedStringData(city_state,",",truckDetailJson.getString("location_city"),truckDetailJson.getString("location_state"));
            CustomValidator.setCustomText(zipcode,truckDetailJson.getString("location_zipcode"), (View) zipcode.getParent());
            CustomValidator.setCustomText(available_date,truckDetailJson.getString("location_available_date"), (View) available_date.getParent());

            CustomValidator.setCombinedStringData(lat_long,"/",truckDetailJson.getString("location_lat"),truckDetailJson.getString("location_lng"));


            //Vehicle Details
            CustomValidator.setCustomText(vin,truckDetailJson.getString("vin"), (View) vin.getParent());
            CustomValidator.setCustomText(serial,truckDetailJson.getString("serial_number"), (View) serial.getParent());
            //CustomValidator.setStringData(year_purchased,truckDetailJson.getString("year_purchased"));
            //CustomValidator.setStringData(purchase_price,truckDetailJson.getString("purchase_price"));
            CustomValidator.setCustomText(weight,truckDetailJson.getString("weight"), (View) weight.getParent());
            CustomValidator.setCustomText(height,truckDetailJson.getString("height"), (View) height.getParent());
            CustomValidator.setCustomText(fuel_type,truckDetailJson.getString("fuel_type"), (View) fuel_type.getParent());
            CustomValidator.setCustomText(no_of_axles,truckDetailJson.getString("no_of_axles"), (View) no_of_axles.getParent());
            CustomValidator.setCustomText(gross_weight,truckDetailJson.getString("gross_weight"), (View) gross_weight.getParent());
            CustomValidator.setCustomText(current_odometer,truckDetailJson.getString("current_odometer"), (View) current_odometer.getParent());
            CustomValidator.setCustomText(current_ewh,truckDetailJson.getString("current_ewh"), (View) current_ewh.getParent());
            CustomValidator.setCustomText(description,truckDetailJson.getString("description"), (View) description.getParent());
            CustomValidator.setCustomText(tank_capacity,truckDetailJson.getString("tank_capacity"), (View) tank_capacity.getParent());
            CustomValidator.setCustomText(tank_reserve,truckDetailJson.getString("tank_reserve"), (View) tank_reserve.getParent());
            CustomValidator.setCustomText(front_tyre_size,truckDetailJson.getString("front_tire_size"), (View) front_tyre_size.getParent());
            CustomValidator.setCustomText(rear_tyre_size,truckDetailJson.getString("rear_tire_size"), (View) rear_tyre_size.getParent());


            //Insurance
            CustomValidator.setCustomText(insurance_policy_number,truckDetailJson.getString("insurance_policy_number"), (View) insurance_policy_number.getParent());
            CustomValidator.setCustomText(insurance_name,truckDetailJson.getString("insurance_name"), (View) insurance_name.getParent());
            CustomValidator.setCustomText(insurance_expiration,truckDetailJson.getString("insurance_expiration"), (View) insurance_expiration.getParent());
            CustomValidator.setCustomText(excluded_state,truckDetailJson.getString("excluded_states"), (View) excluded_state.getParent());



        } catch (JSONException e) {
             Log.e("exception ", String.valueOf(e));
        }
    }


}
