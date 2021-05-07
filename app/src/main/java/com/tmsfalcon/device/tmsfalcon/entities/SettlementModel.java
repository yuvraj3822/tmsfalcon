package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Dell on 5/8/2018.
 */

public class SettlementModel {

    int trip_id,total_settled_amount;
    String start_location;
    String end_location;

    public String getSettlement_amount_string() {
        return settlement_amount_string;
    }

    public void setSettlement_amount_string(String settlement_amount_string) {
        this.settlement_amount_string = settlement_amount_string;
    }

    String settlement_amount_string;

    public int getSettlement_id() {
        return settlement_id;
    }

    public void setSettlement_id(int settlement_id) {
        this.settlement_id = settlement_id;
    }

    int settlement_id;

    public SettlementModel(int trip_id, int total_settled_amount, String start_location, String end_location, String settlement_date, String download_url,String thumb) {
        this.trip_id = trip_id;

        this.start_location = start_location;this.total_settled_amount = total_settled_amount;
        this.end_location = end_location;
        this.settlement_date = settlement_date;
        this.download_url = download_url;
        this.thumb = thumb;
    }
    //for listing driver's settlements
    public SettlementModel(int settlement_id,int trip_id, String total_settled_amount,String settlement_date, String download_url,String thumb) {
        this.trip_id = trip_id;
        this.settlement_amount_string = total_settled_amount;
        this.settlement_id = settlement_id;
        this.settlement_date = settlement_date;
        this.download_url = download_url;
        this.thumb = thumb;
    }

    String settlement_date;

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    String file_type;
    String thumb;

    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public int getTotal_settled_amount() {
        return total_settled_amount;
    }

    public void setTotal_settled_amount(int total_settled_amount) {
        this.total_settled_amount = total_settled_amount;
    }

    public String getStart_location() {
        return start_location;
    }

    public void setStart_location(String start_location) {
        this.start_location = start_location;
    }

    public String getEnd_location() {
        return end_location;
    }

    public void setEnd_location(String end_location) {
        this.end_location = end_location;
    }

    public String getSettlement_date() {
        return settlement_date;
    }

    public void setSettlement_date(String settlement_date) {
        this.settlement_date = settlement_date;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    String download_url;

}
