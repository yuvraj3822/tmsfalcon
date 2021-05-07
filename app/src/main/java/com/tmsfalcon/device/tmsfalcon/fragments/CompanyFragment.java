package com.tmsfalcon.device.tmsfalcon.fragments;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    TextView nameTextView,emailTextView,phoneTextView,faxTextView,addressTextView,stateTextView,zipcodeTextView;
    CircleImageView company_thumb;

    ProgressBar progressBar;


    public CompanyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_company, container, false);
        initIds(view);
        int colorCodeDark = Color.parseColor("#071528");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        if(networkValidator.isNetworkConnected()){
            getCompanyDetail();
        }
        else {
            Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void initIds(View view) {
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        progressBar = view.findViewById(R.id.progress_bar);
        nameTextView = view.findViewById(R.id.full_name);
        emailTextView = view.findViewById(R.id.email);
        phoneTextView = view.findViewById(R.id.phone);
        addressTextView = view.findViewById(R.id.address);
        faxTextView = view.findViewById(R.id.alternative_fax);
        stateTextView = view.findViewById(R.id.state_country);
        zipcodeTextView = view.findViewById(R.id.zipcode);
        company_thumb = view.findViewById(R.id.company_thumb);
    }

    private void getCompanyDetail() {
        // Tag used to cancel the request
         String tag_json_obj = "company_detail_tag";

        String url = UrlController.COMPANY_INFO;

        showProgessBar();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Boolean status = null;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                JSONObject data = response.getJSONObject("data");
                                Glide.with(getActivity())
                                        .load(data.getString("thumb"))
                                        .error(R.drawable.no_image)
                                        .into(company_thumb);
                               /* Picasso.with(getActivity())
                                        .load(data.getString("thumb"))
                                        .error(R.drawable.no_image)
                                        .into(company_thumb);*/
                                CustomValidator.setCustomText(nameTextView,data.getString("company_name"), (View) nameTextView.getParent());
                                CustomValidator.setCustomText(emailTextView,data.getString("email"), (View) emailTextView.getParent());
                                CustomValidator.setCustomText(phoneTextView,data.getString("phone_number"), (View) phoneTextView.getParent());
                                CustomValidator.setCustomText(faxTextView,data.getString("alternative_fax"), (View) faxTextView.getParent());
                                CustomValidator.setCustomText(addressTextView,data.getString("address"), (View) addressTextView.getParent());
                                CustomValidator.setCustomText(zipcodeTextView,data.getString("zip_code"), (View) zipcodeTextView.getParent());
                                CustomValidator.setCombinedStringData(stateTextView,"/",data.getString("state"),data.getString("country"));

                            }

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(getActivity(),error);
                hideProgressBar();

            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

}
