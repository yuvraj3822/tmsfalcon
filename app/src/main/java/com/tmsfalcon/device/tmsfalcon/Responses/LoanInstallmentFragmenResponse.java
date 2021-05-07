package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tmsfalcon.device.tmsfalcon.entities.LoanDetailDataModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoanInstallmentModel;

import java.util.ArrayList;
import java.util.List;

public class LoanInstallmentFragmenResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @SerializedName("total")
    @Expose
    private int total;

    public ArrayList<LoanInstallmentModel> getData() {
        return data;
    }

    public void setData(ArrayList<LoanInstallmentModel> data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private ArrayList<LoanInstallmentModel> data;

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
