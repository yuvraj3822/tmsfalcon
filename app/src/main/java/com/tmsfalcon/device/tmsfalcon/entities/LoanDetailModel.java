package com.tmsfalcon.device.tmsfalcon.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dell on 9/25/2018.
 */

public class LoanDetailModel
{
    @SerializedName("status")
    @Expose
    private Boolean status;

    public LoanDetailModel(Boolean status, LoanDetailDataModel data, List<Object> messages) {
        this.status = status;
        this.data = data;
        this.messages = messages;
    }
    @SerializedName("data")
    @Expose
    private LoanDetailDataModel data;

    @SerializedName("messages")
    @Expose
    private List<Object> messages;

    public Boolean getStatus ()
    {
        return status;
    }

    public void setStatus (Boolean status)
    {
        this.status = status;
    }

    public LoanDetailDataModel getData ()
    {
        return data;
    }

    public void setData (LoanDetailDataModel data)
    {
        this.data = data;
    }

    public List<Object> getMessages ()
    {
        return messages;
    }

    public void setMessages (List<Object> messages)
    {
        this.messages = messages;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [status = "+status+", data = "+data+", messages = "+messages+"]";
    }
}