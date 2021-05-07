package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Android on 11/23/2017.
 */

public class EmailSuggestionResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("messages")
    @Expose
    private List<String> messages = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    @SuppressWarnings("unused")
    public void setData(Data data) {
        this.data = data;
    }
    public class Data {

        @SerializedName("emails")
        @Expose
        private List<String> emails = null;

        public List<String> getEmails() {
            return emails;
        }

        public void setEmails(List<String> emails) {
            this.emails = emails;
        }
    }
}
