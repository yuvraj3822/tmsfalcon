package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tmsfalcon.device.tmsfalcon.entities.LoanDetailDataModel;

import java.util.List;

public class EscrowDetailResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;

    public EscrowDetailResponse(Boolean status, EscrowDetailDataModel data, List<Object> messages) {
        this.status = status;
        this.data = data;
        this.messages = messages;
    }
    @SerializedName("data")
    @Expose
    private EscrowDetailDataModel data;

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

    public EscrowDetailDataModel getData ()
    {
        return data;
    }

    public void setData (EscrowDetailDataModel data)
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
