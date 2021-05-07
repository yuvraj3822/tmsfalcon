package com.tmsfalcon.device.tmsfalcon.entities;

import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;

/**
 * Created by Dell on 2/14/2018.
 */

public class LoanModel {

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getTotal_loan_amount() {
        return total_loan_amount;
    }

    @SuppressWarnings("unused")
    public void setTotal_loan_amount(String total_loan_amount) {
        this.total_loan_amount = total_loan_amount;
    }

    public String getDown_payment() {
        return down_payment;
    }

    public void setDown_payment(String down_payment) {
        this.down_payment = down_payment;
    }

    public String getLast_installment() {
        return last_installment;
    }

    public void setLast_installment(String last_installment) {
        this.last_installment = last_installment;
    }

    public String getNo_installment() {
        return no_installment;
    }

    public void setNo_installment(String no_installment) {
        this.no_installment = no_installment;
    }

    public String getInstallment_amount() {
        return installment_amount;
    }

    @SuppressWarnings("unused")
    public void setInstallment_amount(String installment_amount) {
        this.installment_amount = installment_amount;
    }

    public String getNo_recieved_installment() {
        return no_recieved_installment;
    }

    public void setNo_recieved_installment(String no_recieved_installment) {
        this.no_recieved_installment = no_recieved_installment;
    }

    public String getAmount_paid() {
        return amount_paid;
    }

    @SuppressWarnings("unused")
    public void setAmount_paid(String amount_paid) {
        this.amount_paid = amount_paid;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getExpiring_date() {
        return expiring_date;
    }

    public void setExpiring_date(String expiring_date) {
        this.expiring_date = expiring_date;
    }

    public String getNotes() {
        return CustomValidator.setModelStringData(notes);
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPayment_schedule() {
        return CustomValidator.setModelStringData(payment_schedule);
    }

    public void setPayment_schedule(String payment_schedule) {
        this.payment_schedule = payment_schedule;
    }

    String company_id;
    String driver_id;

    public String getLoan_title() {
        return loan_title;
    }

    public void setLoan_title(String loan_title) {
        this.loan_title = loan_title;
    }

    String loan_title;
    int deferred;


    public int getDeferred() {
        return deferred;
    }

    public void setDeferred(int deferred) {
        this.deferred = deferred;
    }

    public LoanModel(String id, String company_id, String driver_id, String total_loan_amount, String down_payment, String last_installment, String no_installment, String installment_amount, String no_recieved_installment, String amount_paid, String start_date, String expiring_date, String notes, String payment_schedule, int deferred, String loan_title) {
        this.id = id;
        this.company_id = company_id;
        this.driver_id = driver_id;
        this.total_loan_amount = total_loan_amount;
        this.down_payment = down_payment;
        this.last_installment = last_installment;
        this.no_installment = no_installment;
        this.installment_amount = installment_amount;
        this.no_recieved_installment = no_recieved_installment;
        this.amount_paid = amount_paid;
        this.start_date = start_date;
        this.expiring_date = expiring_date;
        this.notes = notes;
        this.payment_schedule = payment_schedule;
        this.deferred = deferred;
        this.loan_title = loan_title;
    }

    String total_loan_amount;
    String down_payment;
    String last_installment;
    String no_installment;
    String installment_amount;
    String no_recieved_installment;
    String amount_paid;
    String start_date;
    String expiring_date;
    String notes;
    String payment_schedule;
}
