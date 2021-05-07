package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Android on 11/30/2017.
 */

public class FetchDispatcherResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("messages")
    @Expose
    private Messages messages;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Messages getMessages() {
        return messages;
    }

    @SuppressWarnings("unused")
    public void setMessages(Messages messages) {
        this.messages = messages;
    }
    public class Messages {

        @SerializedName("data")
        @Expose
        private List<String> data = null;

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }

    }
}
