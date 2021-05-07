package com.tmsfalcon.device.tmsfalcon.Responses;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.StringRes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EscrowDetailDataModel {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("balance")
    @Expose
    private String balance;

    @SerializedName("schedule")
    @Expose
    ArrayList<EscrowStatement> escrowStatement;

    public ArrayList<EscrowStatement> getEscrowStatement() {
        return escrowStatement;
    }

    public void setEscrowStatement(ArrayList<EscrowStatement> escrowStatement) {
        this.escrowStatement = escrowStatement;
    }

    public ArrayList<EscrowTransaction> getEscrowTransaction() {
        return escrowTransaction;
    }

    public void setEscrowTransaction(ArrayList<EscrowTransaction> escrowTransaction) {
        this.escrowTransaction = escrowTransaction;
    }

    @SerializedName("transactions")
    @Expose
    ArrayList<EscrowTransaction> escrowTransaction;

    public class EscrowTransaction implements Parcelable {

        @SerializedName("transaction_id")
        @Expose
        private String transaction_id;

        @SerializedName("escrow_account_id")
        @Expose
        private String escrow_account_id;

        @SerializedName("amount")
        @Expose
        private String amount;

        @SerializedName("schedule_id")
        @Expose
        private String schedule_id;

        @SerializedName("type")
        @Expose
        private String type;

        @SerializedName("created_at")
        @Expose
        private String created_at;

        @SerializedName("ip_address")
        @Expose
        private String ip_address;

        @SerializedName("per_mile_rate")
        @Expose
        private String per_mile_rate;

        @SerializedName("miles")
        @Expose
        private String miles;

        @SerializedName("transaction_method")
        @Expose
        private String transaction_method;

        @SerializedName("balance")
        @Expose
        private String balance;

        @SerializedName("settlement_id")
        @Expose
        private String settlement_id;

        @SerializedName("due_date_start")
        @Expose
        private String due_date_start;

        @SerializedName("due_date_end")
        @Expose
        private String due_date_end;

        @SerializedName("trip_general_expense_id")
        @Expose
        private String trip_general_expense_id;

        @SerializedName("trip_general_payee")
        @Expose
        private String trip_general_payee;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Creator<EscrowTransaction> getCREATOR() {
            return CREATOR;
        }

        @SerializedName("description")
        @Expose
        private String description;

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }

        public String getEscrow_account_id() {
            return escrow_account_id;
        }

        public void setEscrow_account_id(String escrow_account_id) {
            this.escrow_account_id = escrow_account_id;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getSchedule_id() {
            return schedule_id;
        }

        public void setSchedule_id(String schedule_id) {
            this.schedule_id = schedule_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getIp_address() {
            return ip_address;
        }

        public void setIp_address(String ip_address) {
            this.ip_address = ip_address;
        }

        public String getPer_mile_rate() {
            return per_mile_rate;
        }

        public void setPer_mile_rate(String per_mile_rate) {
            this.per_mile_rate = per_mile_rate;
        }

        public String getMiles() {
            return miles;
        }

        public void setMiles(String miles) {
            this.miles = miles;
        }

        public String getTransaction_method() {
            return transaction_method;
        }

        public void setTransaction_method(String transaction_method) {
            this.transaction_method = transaction_method;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getSettlement_id() {
            return settlement_id;
        }

        public void setSettlement_id(String settlement_id) {
            this.settlement_id = settlement_id;
        }

        public String getDue_date_start() {
            return due_date_start;
        }

        public void setDue_date_start(String due_date_start) {
            this.due_date_start = due_date_start;
        }

        public String getDue_date_end() {
            return due_date_end;
        }

        public void setDue_date_end(String due_date_end) {
            this.due_date_end = due_date_end;
        }

        public String getTrip_general_expense_id() {
            return trip_general_expense_id;
        }

        public void setTrip_general_expense_id(String trip_general_expense_id) {
            this.trip_general_expense_id = trip_general_expense_id;
        }

        public String getTrip_general_payee() {
            return trip_general_payee;
        }

        public void setTrip_general_payee(String trip_general_payee) {
            this.trip_general_payee = trip_general_payee;
        }

        public String getTrip_fuel_expense_id() {
            return trip_fuel_expense_id;
        }

        public void setTrip_fuel_expense_id(String trip_fuel_expense_id) {
            this.trip_fuel_expense_id = trip_fuel_expense_id;
        }

        public String getStation() {
            return station;
        }

        public void setStation(String station) {
            this.station = station;
        }

        public String getTrip_id() {
            return trip_id;
        }

        public void setTrip_id(String trip_id) {
            this.trip_id = trip_id;
        }

        @SerializedName("trip_fuel_expense_id")
        @Expose
        private String trip_fuel_expense_id;

        @SerializedName("station")
        @Expose
        private String station;

        @SerializedName("trip_id")
        @Expose
        private String trip_id;

        protected EscrowTransaction(Parcel in) {
            transaction_id = in.readString();
            escrow_account_id = in.readString();
            amount = in.readString();
            schedule_id = in.readString();
            type = in.readString();
            created_at = in.readString();
            ip_address = in.readString();
            per_mile_rate = in.readString();
            miles = in.readString();
            transaction_method = in.readString();
            balance = in.readString();
            settlement_id = in.readString();
            due_date_start = in.readString();
            due_date_end = in.readString();
            trip_general_expense_id = in.readString();
            trip_general_payee = in.readString();
            trip_fuel_expense_id = in.readString();
            station = in.readString();
            trip_id = in.readString();
            description = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(transaction_id);
            dest.writeString(escrow_account_id);
            dest.writeString(amount);
            dest.writeString(schedule_id);
            dest.writeString(type);
            dest.writeString(created_at);
            dest.writeString(ip_address);
            dest.writeString(per_mile_rate);
            dest.writeString(miles);
            dest.writeString(transaction_method);
            dest.writeString(balance);
            dest.writeString(settlement_id);
            dest.writeString(due_date_start);
            dest.writeString(due_date_end);
            dest.writeString(trip_general_expense_id);
            dest.writeString(trip_general_payee);
            dest.writeString(trip_fuel_expense_id);
            dest.writeString(station);
            dest.writeString(trip_id);
            dest.writeString(description);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public final Creator<EscrowTransaction> CREATOR = new Creator<EscrowTransaction>() {
            @Override
            public EscrowTransaction createFromParcel(Parcel in) {
                return new EscrowTransaction(in);
            }

            @Override
            public EscrowTransaction[] newArray(int size) {
                return new EscrowTransaction[size];
            }
        };
    }

    public class EscrowStatement implements Parcelable {

        @SerializedName("schedule_id")
        @Expose
        private String schedule_id;

        @SerializedName("escrow_account_id")
        @Expose
        private String escrow_account_id;

        @SerializedName("due_date_start")
        @Expose
        private String due_date_start;

        @SerializedName("due_date_end")
        @Expose
        private String due_date_end;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("created_at")
        @Expose
        private String created_at;

        protected EscrowStatement(Parcel in) {
            schedule_id = in.readString();
            escrow_account_id = in.readString();
            due_date_start = in.readString();
            due_date_end = in.readString();
            status = in.readString();
            created_at = in.readString();
            payment_date = in.readString();
            settlement_id = in.readString();
            skipped_times = in.readString();
            skipped_date = in.readString();
            ip_address = in.readString();
            is_closed = in.readString();
        }

        public final Creator<EscrowStatement> CREATOR = new Creator<EscrowStatement>() {
            @Override
            public EscrowStatement createFromParcel(Parcel in) {
                return new EscrowStatement(in);
            }

            @Override
            public EscrowStatement[] newArray(int size) {
                return new EscrowStatement[size];
            }
        };

        public String getSchedule_id() {
            return schedule_id;
        }

        public void setSchedule_id(String schedule_id) {
            this.schedule_id = schedule_id;
        }

        public String getEscrow_account_id() {
            return escrow_account_id;
        }

        public void setEscrow_account_id(String escrow_account_id) {
            this.escrow_account_id = escrow_account_id;
        }

        public String getDue_date_start() {
            return due_date_start;
        }

        public void setDue_date_start(String due_date_start) {
            this.due_date_start = due_date_start;
        }

        public String getDue_date_end() {
            return due_date_end;
        }

        public void setDue_date_end(String due_date_end) {
            this.due_date_end = due_date_end;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getPayment_date() {
            return payment_date;
        }

        public void setPayment_date(String payment_date) {
            this.payment_date = payment_date;
        }

        public String getSettlement_id() {
            return settlement_id;
        }

        public void setSettlement_id(String settlement_id) {
            this.settlement_id = settlement_id;
        }

        public String getSkipped_times() {
            return skipped_times;
        }

        public void setSkipped_times(String skipped_times) {
            this.skipped_times = skipped_times;
        }

        public String getSkipped_date() {
            return skipped_date;
        }

        public void setSkipped_date(String skipped_date) {
            this.skipped_date = skipped_date;
        }

        public String getIp_address() {
            return ip_address;
        }

        public void setIp_address(String ip_address) {
            this.ip_address = ip_address;
        }

        public String getIs_closed() {
            return is_closed;
        }

        public void setIs_closed(String is_closed) {
            this.is_closed = is_closed;
        }

        @SerializedName("payment_date")
        @Expose
        private String payment_date;

        @SerializedName("settlement_id")
        @Expose
        private String settlement_id;

        @SerializedName("skipped_times")
        @Expose
        private String skipped_times;

        @SerializedName("skipped_date")
        @Expose
        private String skipped_date;

        @SerializedName("ip_address")
        @Expose
        private String ip_address;

        @SerializedName("is_closed")
        @Expose
        private String is_closed;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(schedule_id);
            parcel.writeString(escrow_account_id);
            parcel.writeString(due_date_start);
            parcel.writeString(due_date_end);
            parcel.writeString(status);
            parcel.writeString(created_at);
            parcel.writeString(payment_date);
            parcel.writeString(settlement_id);
            parcel.writeString(skipped_times);
            parcel.writeString(skipped_date);
            parcel.writeString(ip_address);
            parcel.writeString(is_closed);
        }
    }
}
