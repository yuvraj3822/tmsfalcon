package com.tmsfalcon.device.tmsfalcon.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dell on 3/27/2018.
 */

public class TripItineraryModel implements Parcelable {

    public TripItineraryModel(String id, String location, String city,
                              String state, String type, String status, String date, String date_parsed,
                              String time, Integer timestamp, Integer is_shipper_consignee, String end_time,
                              String zipcode, String contact_person, String bol, String phone, String fax,
                              String instructions,String name, String location_address, String number,
                              String reference) {
        this.id = id;
        this.location = location;
        this.city = city;
        this.state = state;
        this.type = type;
        this.status = status;
        this.date = date;
        this.date_parsed = date_parsed;
        this.time = time;
        this.timestamp = timestamp;
        this.is_shipper_consignee = is_shipper_consignee;
        this.end_time = end_time;
        this.zipcode = zipcode;
        this.contact_person = contact_person;
        this.bol = bol;
        this.phone = phone;
        this.fax = fax;
        this.instructions = instructions;
        this.name = name;
        this.location_address = location_address;
        this.number = number;
        this.reference = reference;


    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation_address() {
        return location_address;
    }

    public void setLocation_address(String location_address) {
        this.location_address = location_address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("date_parsed")
    @Expose
    private String date_parsed;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("timestamp")
    @Expose
    private Integer timestamp;

    @SerializedName("is_shipper_consignee")
    @Expose
    private Integer is_shipper_consignee;

    @SerializedName("end_time")
    @Expose
    private String end_time;

    @SerializedName("zipcode")
    @Expose
    private String zipcode;

    @SerializedName("contact_person")
    @Expose
    private String contact_person;

    @SerializedName("bol")
    @Expose
    private String bol;




    @SerializedName("fax")
    @Expose
    private String fax;

    @SerializedName("instructions")
    @Expose
    private String instructions;


    @SerializedName("name")
    @Expose
    private String name;



    @SerializedName("location_address")
    @Expose
    private String location_address;


    @SerializedName("date")
    @Expose
    private String date;


    @SerializedName("number")
    @Expose
    private String number;


    @SerializedName("reference")
    @Expose
    private String reference;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_parsed() {
        String finalDateParsed = date_parsed.replace(",",", ");
        return finalDateParsed;
    }

    public void setDate_parsed(String date_parsed) {
        this.date_parsed = date_parsed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getIs_shipper_consignee() {
        return is_shipper_consignee;
    }

    public void setIs_shipper_consignee(Integer is_shipper_consignee) {
        this.is_shipper_consignee = is_shipper_consignee;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }



    protected TripItineraryModel(Parcel in) {
        id = in.readString();
        location = in.readString();
        city = in.readString();
        state = in.readString();
        type = in.readString();
        status = in.readString();
        date = in.readString();
        date_parsed = in.readString();
        time = in.readString();
        timestamp = in.readInt();
        is_shipper_consignee = in.readInt();
        end_time = in.readString();
        zipcode = in.readString();
        contact_person = in.readString();
        bol = in.readString();
        phone = in.readString();
        fax = in.readString();
        instructions = in.readString();
        name = in.readString();
        location_address = in.readString();
        number = in.readString();
        reference = in.readString();





    }

    public static final Creator<TripItineraryModel> CREATOR = new Creator<TripItineraryModel>() {
        @Override
        public TripItineraryModel createFromParcel(Parcel in) {
            return new TripItineraryModel(in);
        }

        @Override
        public TripItineraryModel[] newArray(int size) {
            return new TripItineraryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(location);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(type);
        dest.writeString(status);
        dest.writeString(date);
        dest.writeString(date_parsed);
        dest.writeString(time);
        dest.writeInt(timestamp);
        dest.writeInt(is_shipper_consignee);
        dest.writeString(end_time);
        dest.writeString(zipcode);
        dest.writeString(contact_person);
        dest.writeString(bol);
        dest.writeString(phone);
        dest.writeString(fax);
        dest.writeString(instructions);
        dest.writeString(name);
        dest.writeString(location_address);
        dest.writeString(number);
        dest.writeString(reference);



    }
}
