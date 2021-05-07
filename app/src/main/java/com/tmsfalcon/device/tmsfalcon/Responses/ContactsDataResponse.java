package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dell on 3/6/2019.
 */

public class ContactsDataResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Object> getMessages() {
        return messages;
    }

    public void setMessages(List<Object> messages) {
        this.messages = messages;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    @SerializedName("messages")

    @Expose
    private List<Object> messages = null;

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public class Datum {

        public String toString(){
            return "description : " + description + "\ntype : " + type + "\nphone number : " + phone_number
                    + "\nextension : " + extension
                    + "\nemail : " + email
                    + "\naddress : " + address;
        }

        @SerializedName("description")
        @Expose
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @SerializedName("type")

        @Expose
        private String type;

        @SerializedName("phone_number")
        @Expose
        private String phone_number;

        @SerializedName("extension")
        @Expose
        private String extension;

        @SerializedName("email")
        @Expose
        private String email;

        @SerializedName("address")
        @Expose
        private String address;

    }
}
