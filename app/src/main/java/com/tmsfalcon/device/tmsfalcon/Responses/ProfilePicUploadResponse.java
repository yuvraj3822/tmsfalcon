package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dell on 10/26/2018.
 */

public class ProfilePicUploadResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("messages")
    @Expose
    private List<String> messages = null;

    public Datum getData() {
        return data;
    }

    public void setData(Datum data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private Datum data = null;


    public Boolean getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public class Datum {

        @SerializedName("file_url")
        @Expose
        private String file_url;

        public String getFile_url() {
            return file_url;
        }

        public void setFile_url(String file_url) {
            this.file_url = file_url;
        }

    }


}
