package com.tmsfalcon.device.tmsfalcon.entities;

public class SettlementBatchModel {

    public SettlementBatchModel(String settlement_batch_id, String company_id, String date_settled, String settlement_total, String notes, String status, String paid_on, String batch_csv_id, String u_id) {
        this.settlement_batch_id = settlement_batch_id;
        this.company_id = company_id;
        this.date_settled = date_settled;
        this.settlement_total = settlement_total;
        this.notes = notes;
        this.status = status;
        this.paid_on = paid_on;
        this.batch_csv_id = batch_csv_id;
        this.u_id = u_id;
    }

    public String getSettlement_batch_id() {
        return settlement_batch_id;
    }

    public void setSettlement_batch_id(String settlement_batch_id) {
        this.settlement_batch_id = settlement_batch_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getDate_settled() {
        return date_settled;
    }

    public void setDate_settled(String date_settled) {
        this.date_settled = date_settled;
    }

    public String getSettlement_total() {
        return settlement_total;
    }

    public void setSettlement_total(String settlement_total) {
        this.settlement_total = settlement_total;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaid_on() {
        return paid_on;
    }

    public void setPaid_on(String paid_on) {
        this.paid_on = paid_on;
    }

    public String getBatch_csv_id() {
        return batch_csv_id;
    }

    public void setBatch_csv_id(String batch_csv_id) {
        this.batch_csv_id = batch_csv_id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    String settlement_batch_id,company_id,date_settled,settlement_total,notes,status,paid_on,batch_csv_id,u_id;
}
