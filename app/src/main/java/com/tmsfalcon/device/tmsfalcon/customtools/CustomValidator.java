package com.tmsfalcon.device.tmsfalcon.customtools;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Android on 7/3/2017.
 */

public class CustomValidator {

    Context context;

    public CustomValidator(Context c) {
        this.context = c;
    }

    public static void load_settings_widget(EditText editText,String value){
        if(!checkNullState(value)){
            hide_edit_attributes(editText);
            editText.setText(value);

        }
        else{
            show_edit_attributes(editText);
        }

    }

    public static void show_edit_attributes(EditText editText){
        editText.requestFocus();
        editText.setClickable(true);
        editText.setFocusable(true);
    }

    public static void hide_edit_attributes(EditText editText ){

        //editText.setFocusable(false);
        //editText.setBackground(null);
    }

    public boolean setRequired(String str){
        if(str.equals("") || str.length() == 0){
            return false;
        }
        else{
            return true;
        }
    }
    public boolean setEquality(String str1,String str2){
        if(str1.equals("") || str2.equals("")){
            return false;
        }
        else{
            if(!str1.equals(str2)){
                return false;
            }
            else{
                return true;
            }
        }
    }

    public boolean checkEmail(String str){
        if (str == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
        }
    }
    public boolean isValidFax(String fax) {
        boolean check = false;
        if(!Pattern.matches("[a-zA-Z]+", fax)) {
            if(fax.length() < 10 || fax.length() > 11) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }

    public boolean isValidEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;

        final String PATTERN = "^.*(?=.{6,20})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$^";

        pattern = Pattern.compile(PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    public static void setStringData(TextView textView,String value){
        if(value == null || value.trim() == "" || value.trim() == "null" || value.length() == 0){

            textView.setText("NA");
        }
        else{
            textView.setText(value);
        }
    }

    public static void setCombinedStringData(TextView textView,String delimator,String... args){
        //Log.e("args", Arrays.toString(args));
        String value = "";
        for (int i = 0; i < args.length ; i++){
            value += args[i];
            if(args.length >i+1 && args[i+1] != null){
                value += delimator;
            }
        }
        textView.setText(value);
    }




    public static void setAddress2(TextView textView, String address, String city, String state, String zipcode){

        textView.setText(address+"\n"+city+", "+state+ " "+zipcode);

    }

    public static void setAddressData(TextView textView,String delimator,String... args){
        // args will hav ethe address,city,state,zipcode
        String value = "";
        value += args[0];
        for(int i = 1;i < args.length; i++){
            if(i <= args.length){
                //index  exists
                // Log.e("check null at i ",i+" value : "+args[i]+" result: "+(!checkNullState(args[i])));
               if((!checkNullState(args[i])) == true){

                   value += delimator;
                   if(i == 1){
                       value += System.lineSeparator();
                   }
                   value += args[i];
                   Log.e("in",value);
               }
            }
        }
        textView.setText(value);

    }

    public static void setMailingAddress(TextView textView,String delimator,String... args){
        // args will hav ethe address,city,state,zipcode
        String value = "";
        value += args[0];
        for(int i = 1;i < args.length; i++){
            if(i <= args.length){
                //index  exists
                // Log.e("check null at i ",i+" value : "+args[i]+" result: "+(!checkNullState(args[i])));
                if((!checkNullState(args[i])) == true){

                    value += delimator;
                    if(i == 1){
                        value += System.lineSeparator();
                    }
                    value += args[i];
                    Log.e("in",value);
                }
            }
        }
        textView.setText(value);

    }




    public static void setCombinedArrayData(TextView textView,String delimator,String[] args){
        //Log.e("args", Arrays.toString(args));
        String value = "";
        for (int i = 0; i < args.length ; i++){
            value += args[i];
            if(args.length >i+1 && args[i+1] != null){
                value += delimator;
            }
        }
        textView.setText(value);
    }

    public static String setModelStringData(String value){

        if(value == null || value == "" || value == "null"){

            return "NA";
        }
        else{

           return value;

        }
    }

    public static boolean checkNullState(String value){
        if(value == null || value.trim() == "" || value.trim() == "null" || value.length()   == 0 ){

            return true;
        }
        else{

            return  false;
        }
    }

    public static void setCustomText(TextView textView,String value,View parent){
        if(value == null || value.trim() == "" || value.trim() == "null" || value.length()   == 0 || value.equals("0") || value.equals(".00")){

            parent.setVisibility(View.GONE);
        }
        else{

            textView.setText(value);
        }
    }

    public static void setCustomTextAccountingFormat(TextView textView,String value,View parent){
        if(value == null || value.trim() == "" || value.trim() == "null" || value.length()   == 0 || value.equals("0") || value.equals(".00")){

            parent.setVisibility(View.GONE);
        }
        else{
            String val = NumberFormat.getNumberInstance(Locale.getDefault()).format(Double.parseDouble(value));
            textView.setText(val);
        }
    }

    public static void setCustomSalaryText(TextView textView,String value,View parent){
        if(value == null || value.trim() == "" || value.trim() == "null" || value.length() == 0 || value.equals("0") || value.equals(".00")){

            parent.setVisibility(View.GONE);
        }
        else{

            textView.setText("$ "+Utils.convertNumToAccountingFormat(value));
        }
    }

    public static String properCase (String inputVal) {
        // Empty strings should be returned as-is.

        if (inputVal == null || inputVal.trim() == "" || inputVal.trim() == "null" ||inputVal.length() == 0) return "";

        // Strings with only one character uppercased.

        if (inputVal.length() == 1) return inputVal.toUpperCase();
        String inpur_arr[] = inputVal.split(",");
        // Otherwise uppercase first letter, lowercase the rest.

//        return inpur_arr[0].substring(0,1).toUpperCase()
//                + inpur_arr[0].substring(1).toLowerCase()+",\n"+inpur_arr[1];
        return inpur_arr[0].substring(0,1).toUpperCase()
                + inpur_arr[0].substring(1).toLowerCase()+", "+inpur_arr[1].trim();
    }

    public static String capitalizeFirstLetter(String value){
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
