package com.tmsfalcon.device.tmsfalcon.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Dell on 9/25/2018.
 */

public class LoanInstallmentModel
{
    @SerializedName("emi")
    @Expose
    private ArrayList<LoanEmiModel> emi;

    @SerializedName("year_text")
    @Expose
    private String year_text;

    @SerializedName("year_total_payment")
    @Expose
    private String year_total_payment;

    @SerializedName("year_total_principal")
    @Expose
    private String year_total_principal;

    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("year_total_interest")
    @Expose
    private String year_total_interest;

    public ArrayList<LoanEmiModel> getEmi ()
    {
        return emi;
    }

    public void setEmi (ArrayList<LoanEmiModel> emi)
    {
        this.emi = emi;
    }

    public String getYear_text ()
    {
        return year_text;
    }

    public void setYear_text (String year_text)
    {
        this.year_text = year_text;
    }

    public String getYear_total_payment ()
    {
        return year_total_payment;
    }

    public void setYear_total_payment (String year_total_payment)
    {
        this.year_total_payment = year_total_payment;
    }

    public String getYear_total_principal ()
    {
        return year_total_principal;
    }

    public void setYear_total_principal (String year_total_principal)
    {
        this.year_total_principal = year_total_principal;
    }

    public String getYear ()
    {
        return year;
    }

    public void setYear (String year)
    {
        this.year = year;
    }

    public String getYear_total_interest ()
    {
        return year_total_interest;
    }

    public void setYear_total_interest (String year_total_interest)
    {
        this.year_total_interest = year_total_interest;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [emi = "+emi+", year_text = "+year_text+", year_total_payment = "+year_total_payment+", year_total_principal = "+year_total_principal+", year = "+year+", year_total_interest = "+year_total_interest+"]";
    }

}

