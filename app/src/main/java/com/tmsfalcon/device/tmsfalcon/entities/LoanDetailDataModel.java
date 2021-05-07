package com.tmsfalcon.device.tmsfalcon.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Dell on 9/25/2018.
 */

public class LoanDetailDataModel implements Parcelable {

    public LoanDetailDataModel(String first_payment_due, String payment_schedule, String total_payments, String loan_date, String interest, String total_loan_amount, String last_payment_due, String installment_amount, String total_Interest_due, String no_installment) {
        this.first_payment_due = first_payment_due;
        this.payment_schedule = payment_schedule;
        this.total_payments = total_payments;
        this.loan_date = loan_date;
        this.interest = interest;
        this.total_loan_amount = total_loan_amount;
        this.last_payment_due = last_payment_due;
        this.installment_amount = installment_amount;
        this.total_Interest_due = total_Interest_due;
        this.no_installment = no_installment;
    }
    @SerializedName("first_payment_due")
    @Expose
    private String first_payment_due;

    @SerializedName("payment_schedule")
    @Expose
    private String payment_schedule;

    @SerializedName("total_payments")
    @Expose
    private String total_payments;

    @SerializedName("loan_date")
    @Expose
    private String loan_date;

    public String getLoan_title() {
        return loan_title;
    }

    public void setLoan_title(String loan_title) {
        this.loan_title = loan_title;
    }

    @SerializedName("loan_title")
    @Expose
    private String loan_title;

    @SerializedName("interest")
    @Expose
    private String interest;

    @SerializedName("total_loan_amount")
    @Expose
    private String total_loan_amount;

    @SerializedName("last_payment_due")
    @Expose
    private String last_payment_due;

    @SerializedName("installment_amount")
    @Expose
    private String installment_amount;

    @SerializedName("total_Interest_due")
    @Expose
    private String total_Interest_due;

    @SerializedName("installments")
    @Expose
    private ArrayList<LoanInstallmentModel> installments;

    @SerializedName("no_installment")
    @Expose
    private String no_installment;

    protected LoanDetailDataModel(Parcel in) {
        first_payment_due = in.readString();
        payment_schedule = in.readString();
        total_payments = in.readString();
        loan_date = in.readString();
        interest = in.readString();
        total_loan_amount = in.readString();
        last_payment_due = in.readString();
        installment_amount = in.readString();
        total_Interest_due = in.readString();
        no_installment = in.readString();
    }

    public static final Creator<LoanDetailDataModel> CREATOR = new Creator<LoanDetailDataModel>() {
        @Override
        public LoanDetailDataModel createFromParcel(Parcel in) {
            return new LoanDetailDataModel(in);
        }

        @Override
        public LoanDetailDataModel[] newArray(int size) {
            return new LoanDetailDataModel[size];
        }
    };

    public String getFirst_payment_due ()
    {
        return first_payment_due;
    }

    public void setFirst_payment_due (String first_payment_due)
    {
        this.first_payment_due = first_payment_due;
    }

    public String getPayment_schedule ()
    {
        return payment_schedule;
    }

    public void setPayment_schedule (String payment_schedule)
    {
        this.payment_schedule = payment_schedule;
    }

    public String getTotal_payments ()
    {
        return total_payments;
    }

    public void setTotal_payments (String total_payments)
    {
        this.total_payments = total_payments;
    }

    public String getLoan_date ()
    {
        return loan_date;
    }

    public void setLoan_date (String loan_date)
    {
        this.loan_date = loan_date;
    }

    public String getInterest ()
    {
        return interest;
    }

    public void setInterest (String interest)
    {
        this.interest = interest;
    }

    public String getTotal_loan_amount ()
    {
        return total_loan_amount;
    }

    public void setTotal_loan_amount (String total_loan_amount)
    {
        this.total_loan_amount = total_loan_amount;
    }

    public String getLast_payment_due ()
    {
        return last_payment_due;
    }

    public void setLast_payment_due (String last_payment_due)
    {
        this.last_payment_due = last_payment_due;
    }

    public String getInstallment_amount ()
    {
        return installment_amount;
    }

    public void setInstallment_amount (String installment_amount)
    {
        this.installment_amount = installment_amount;
    }

    public String getTotal_Interest_due ()
    {
        return total_Interest_due;
    }

    public void setTotal_Interest_due (String total_Interest_due)
    {
        this.total_Interest_due = total_Interest_due;
    }

    public ArrayList<LoanInstallmentModel> getInstallments ()
    {
        return installments;
    }

    public void setInstallments (ArrayList<LoanInstallmentModel> installments)
    {
        this.installments = installments;
    }

    public String getNo_installment ()
    {
        return no_installment;
    }

    public void setNo_installment (String no_installment)
    {
        this.no_installment = no_installment;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [first_payment_due = "+first_payment_due+", payment_schedule = "+payment_schedule+", total_payments = "+total_payments+", loan_date = "+loan_date+", interest = "+interest+", total_loan_amount = "+total_loan_amount+", last_payment_due = "+last_payment_due+", installment_amount = "+installment_amount+", total_Interest_due = "+total_Interest_due+", installments = "+installments+", no_installment = "+no_installment+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(first_payment_due);
        dest.writeString(payment_schedule);
        dest.writeString(total_payments);
        dest.writeString(loan_date);
        dest.writeString(interest);
        dest.writeString(total_loan_amount);
        dest.writeString(last_payment_due);
        dest.writeString(installment_amount);
        dest.writeString(total_Interest_due);
        dest.writeString(no_installment);
    }
}
