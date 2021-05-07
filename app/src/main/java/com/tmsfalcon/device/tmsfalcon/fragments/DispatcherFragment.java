package com.tmsfalcon.device.tmsfalcon.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
public class DispatcherFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    TextView nameTextView,emailTextView,phoneTextView;
    CircleImageView dispatcher_thumb;
    ProgressBar progressBar;
    LinearLayout dispatcherDataLinearLayout;
    TextView no_dispatcher_textview,txtMcNo,txtDotNo;

    public DispatcherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dispatcher, container, false);
        initIds(view);
        if(networkValidator.isNetworkConnected()){
            getDispatcherDetail();
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
        dispatcherDataLinearLayout = view.findViewById(R.id.dispatcher_data);
        no_dispatcher_textview = view.findViewById(R.id.no_dispatcher_textview);
        nameTextView = view.findViewById(R.id.full_name);
        emailTextView = view.findViewById(R.id.email);
        phoneTextView = view.findViewById(R.id.phone);
        dispatcher_thumb = view.findViewById(R.id.dispatcher_thumb);


        txtMcNo = view.findViewById(R.id.mcno);
        txtDotNo = view.findViewById(R.id.dotno);


    }

    private void getDispatcherDetail() {
        // Tag used to cancel the request.
        String tag_json_obj = "dispatcher_detail_tag";

        String url = UrlController.DISPACHER_INFO;

        showProgessBar();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        Boolean dispatcher = null;
                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            dispatcher = response.getBoolean("dispatcher");
                            if(status){
                                JSONObject data = response.getJSONObject("data");
                                dispatcherDataLinearLayout.setVisibility(View.VISIBLE);
                                Glide.with(getActivity())
                                        .load(data.getString("thumb"))
                                        .error(R.drawable.no_image)
                                        .into(dispatcher_thumb);
                               /* Picasso.with(getActivity())
                                        .load(data.getString("thumb"))
                                        .error(R.drawable.no_image)
                                        .into(dispatcher_thumb);*/
                                CustomValidator.setCombinedStringData(nameTextView," ",data.getString("first_name"),data.getString("last_name"));
                                CustomValidator.setCustomText(emailTextView,data.getString("email"), (View) emailTextView.getParent());
                                CustomValidator.setCustomText(phoneTextView,data.getString("phone_number"), (View) phoneTextView.getParent());
                                CustomValidator.setCustomText(phoneTextView,data.getString("phone_number"), (View) phoneTextView.getParent());

                                CustomValidator.setCustomText(txtMcNo,data.getString("mc_number"), (View) txtMcNo.getParent());
                                CustomValidator.setCustomText(txtDotNo,data.getString("dot_number"), (View) txtDotNo.getParent());




                            }
                            else{
                                if(!dispatcher){
                                    dispatcherDataLinearLayout.setVisibility(View.GONE);
                                    no_dispatcher_textview.setVisibility(View.VISIBLE);
                                }
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
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("Content-Type", "application/json");
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
