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
public class TrailerBasicProfile extends Fragment {

    JSONObject trailerDetailJson;
    TextView unitNo,model,make,year,trailer_group,trailer_type,license_plate,license_expiration,registered_state,
            city_state,zipcode,available_date,lat_long,
            vin,year_purchased,serial_number,purchase_price,weight,height,max_pallets,length,length_dimension,width,width_dimension,gross_weight,current_odometer,current_ewh,tyre_size_width,tyre_size_section_height,tyre_size_rim_diameter,description,
            insurance_name,insurance_expiration,insurance_policy_number,excluded_state;
    public TrailerBasicProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trailer_basic_profile, container, false);
        try {
            trailerDetailJson = new JSONObject(getArguments().getString("trailer_detail"));
        } catch (JSONException e) {
            Log.e("exception ", String.valueOf(e));
        }
        initIds(view);
        Log.e("trailerDetailJson", String.valueOf(trailerDetailJson));
        if(trailerDetailJson != null){
            setText();
        }
        else{

        }
        return view;
    }

    public void initIds(View view){
        //Basic
        unitNo = view.findViewById(R.id.unit_number);
        model = view.findViewById(R.id.model);
        make = view.findViewById(R.id.make);
        year = view.findViewById(R.id.year);
        license_plate = view.findViewById(R.id.license_plate_number);
        license_expiration = view.findViewById(R.id.license_expiration);
        registered_state = view.findViewById(R.id.resgistered_state);
        trailer_group = view.findViewById(R.id.trailer_group);
        trailer_type = view.findViewById(R.id.trailer_type);

        //Location
        city_state = view.findViewById(R.id.city_state);
        zipcode = view.findViewById(R.id.zipcode);
        available_date = view.findViewById(R.id.available_date);
        lat_long = view.findViewById(R.id.latitude_longitude);

        //Vehicle Details
        tyre_size_width = view.findViewById(R.id.tyre_size_width);
        tyre_size_section_height = view.findViewById(R.id.tyre_size_section_height);
        tyre_size_rim_diameter = view.findViewById(R.id.tyre_size_rim_diameter);
        vin = view.findViewById(R.id.vin);
        max_pallets = view.findViewById(R.id.max_pallets);
        length = view.findViewById(R.id.length);
        length_dimension = view.findViewById(R.id.length_dimension);
        width = view.findViewById(R.id.width);
        width_dimension =  view.findViewById(R.id.width_dimension);
        serial_number = view.findViewById(R.id.serial_number);
        //year_purchased = (TextView) view.findViewById(R.id.year_purchased);
        //purchase_price = (TextView) view.findViewById(R.id.purchase_price);
        weight = view.findViewById(R.id.weight);
        height = view.findViewById(R.id.height);
        gross_weight = view.findViewById(R.id.gross_weight);
        current_odometer = view.findViewById(R.id.current_odometer);
        current_ewh =  view.findViewById(R.id.current_ewh);
        description = view.findViewById(R.id.description);

        //Insurance
        insurance_name =  view.findViewById(R.id.insurance_name);
        insurance_expiration = view.findViewById(R.id.insurance_expiration);
        insurance_policy_number = view.findViewById(R.id.insurance_policy_number);
        excluded_state = view.findViewById(R.id.excluded_state);
    }

    private void setText() {
        try {
            //Basic
            CustomValidator.setCustomText(unitNo,trailerDetailJson.getString("unit_number"), (View) unitNo.getParent());
            CustomValidator.setCustomText(model,trailerDetailJson.getString("model"), (View) model.getParent());
            CustomValidator.setCustomText(make,trailerDetailJson.getString("make"), (View) make.getParent());
            CustomValidator.setCustomText(year,trailerDetailJson.getString("year"), (View) year.getParent());
            CustomValidator.setCustomText(license_plate,trailerDetailJson.getString("license_plate_number"), (View) license_plate.getParent());
            CustomValidator.setCustomText(license_expiration,trailerDetailJson.getString("license_expiration"), (View) license_expiration.getParent());
            CustomValidator.setCustomText(registered_state,trailerDetailJson.getString("registered_state"), (View) registered_state.getParent());
            CustomValidator.setCustomText(trailer_group,trailerDetailJson.getString("trailer_group"), (View) trailer_group.getParent());
            CustomValidator.setCustomText(trailer_type,trailerDetailJson.getString("trailer_type"), (View) trailer_type.getParent());


            //Location

            CustomValidator.setCombinedStringData(city_state,",",trailerDetailJson.getString("location_city"),trailerDetailJson.getString("location_state"));
            CustomValidator.setCustomText(zipcode,trailerDetailJson.getString("location_zipcode"), (View) zipcode.getParent());
            CustomValidator.setCustomText(available_date,trailerDetailJson.getString("location_available_date"), (View) available_date.getParent());
            CustomValidator.setCombinedStringData(lat_long,"/",trailerDetailJson.getString("location_lat"),trailerDetailJson.getString("location_lng"));


            //Vehicle Details
            //CustomValidator.setStringData(year_purchased,trailerDetailJson.getString("year_purchased"));
            //CustomValidator.setStringData(purchase_price,trailerDetailJson.getString("purchase_price"));
            CustomValidator.setCustomText(weight,trailerDetailJson.getString("max_weight"), (View) weight.getParent());
            CustomValidator.setCustomText(height,trailerDetailJson.getString("height"), (View) height.getParent());
            CustomValidator.setCustomText(gross_weight,trailerDetailJson.getString("gross_weight"), (View) gross_weight.getParent());
            CustomValidator.setCustomText(current_odometer,trailerDetailJson.getString("current_odometer"), (View) current_odometer.getParent());
            CustomValidator.setCustomText(current_ewh,trailerDetailJson.getString("current_ewh"), (View) current_ewh.getParent());
            CustomValidator.setCustomText(description,trailerDetailJson.getString("description"), (View) description.getParent());


            CustomValidator.setCustomText(tyre_size_width,trailerDetailJson.getString("tire_width"), (View) tyre_size_width.getParent());
            CustomValidator.setCustomText(tyre_size_section_height,trailerDetailJson.getString("section_height"), (View) tyre_size_section_height.getParent());
            CustomValidator.setCustomText(tyre_size_rim_diameter,trailerDetailJson.getString("rim_diameter"), (View) tyre_size_rim_diameter.getParent());
            CustomValidator.setCustomText(vin,trailerDetailJson.getString("vin"), (View) vin.getParent());
            CustomValidator.setCustomText(max_pallets,trailerDetailJson.getString("max_pallets"), (View) max_pallets.getParent());
            CustomValidator.setCustomText(length,trailerDetailJson.getString("length"), (View) length.getParent());
            CustomValidator.setCustomText(length_dimension,trailerDetailJson.getString("length_type"), (View) length_dimension.getParent());
            CustomValidator.setCustomText(width,trailerDetailJson.getString("width"), (View) width.getParent());
            CustomValidator.setCustomText(width_dimension,trailerDetailJson.getString("width_type"), (View) width_dimension.getParent());
            CustomValidator.setCustomText(serial_number,trailerDetailJson.getString("serial_number"), (View) serial_number.getParent());

            //Insurance

            CustomValidator.setCustomText(insurance_policy_number,trailerDetailJson.getString("insurance_policy_number"), (View) insurance_policy_number.getParent());
            CustomValidator.setCustomText(insurance_name,trailerDetailJson.getString("insurance_name"), (View) insurance_name.getParent());
            CustomValidator.setCustomText(insurance_expiration,trailerDetailJson.getString("insurance_expiration"), (View) insurance_expiration.getParent());
            CustomValidator.setCustomText(excluded_state,trailerDetailJson.getString("excluded_states"), (View) excluded_state.getParent());

        } catch (JSONException e) {
            Log.e("exception ", String.valueOf(e));
        }
    }

}
