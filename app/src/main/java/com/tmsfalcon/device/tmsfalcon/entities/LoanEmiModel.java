package com.tmsfalcon.device.tmsfalcon.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dell on 9/25/2018.
 */
public class LoanEmiModel
{
    @SerializedName("company_id")
    @Expose
    private String company_id;

    @SerializedName("schedule_id")
    @Expose
    private String schedule_id;

    @SerializedName("skipped_date")
    @Expose
    private String skipped_date;

    @SerializedName("sindex")
    @Expose
    private String sindex;

    @SerializedName("emi")
    @Expose
    private String emi;

    @SerializedName("principal_left")
    @Expose
    private String principal_left;

    @SerializedName("skipped_times")
    @Expose
    private String skipped_times;

    @SerializedName("paid")
    @Expose
    private String paid;

    @SerializedName("principal")
    @Expose
    private String principal;

    @SerializedName("interest")
    @Expose
    private String interest;

    @SerializedName("paid_emi")
    @Expose
    private String paid_emi;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("driver_id")
    @Expose
    private String driver_id;

    @SerializedName("period_end")
    @Expose
    private String period_end;

    @SerializedName("payment_date")
    @Expose
    private String payment_date;

    @SerializedName("interest_collected")
    @Expose
    private String interest_collected;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("period_start")
    @Expose
    private String period_start;

    @SerializedName("late_payment_fee")
    @Expose
    private String late_payment_fee;

    @SerializedName("ip_address")
    @Expose
    private String ip_address;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("skipped")
    @Expose
    private String skipped;

    @SerializedName("compound_interest")
    @Expose
    private String compound_interest;

    @SerializedName("settlement_id")
    @Expose
    private String settlement_id;

    @SerializedName("loan_id")
    @Expose
    private String loan_id;

    public String getCompany_id ()
    {
        return company_id;
    }

    public void setCompany_id (String company_id)
    {
        this.company_id = company_id;
    }

    public String getSchedule_id ()
    {
        return schedule_id;
    }

    public void setSchedule_id (String schedule_id)
    {
        this.schedule_id = schedule_id;
    }

    public String getSkipped_date ()
{
    return skipped_date;
}

    public void setSkipped_date (String skipped_date)
    {
        this.skipped_date = skipped_date;
    }

    public String getSindex ()
    {
        return sindex;
    }

    public void setSindex (String sindex)
    {
        this.sindex = sindex;
    }

    public String getEmi ()
    {
        return emi;
    }

    public void setEmi (String emi)
    {
        this.emi = emi;
    }

    public String getPrincipal_left ()
    {
        return principal_left;
    }

    public void setPrincipal_left (String principal_left)
    {
        this.principal_left = principal_left;
    }

    public String getSkipped_times ()
    {
        return skipped_times;
    }

    public void setSkipped_times (String skipped_times)
    {
        this.skipped_times = skipped_times;
    }

    public String getPaid ()
    {
        return paid;
    }

    public void setPaid (String paid)
    {
        this.paid = paid;
    }

    public String getPrincipal ()
    {
        return principal;
    }

    public void setPrincipal (String principal)
    {
        this.principal = principal;
    }

    public String getInterest ()
    {
        return interest;
    }

    public void setInterest (String interest)
    {
        this.interest = interest;
    }

    public String getPaid_emi ()
    {
        return paid_emi;
    }

    public void setPaid_emi (String paid_emi)
    {
        this.paid_emi = paid_emi;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public String getYear ()
    {
        return year;
    }

    public void setYear (String year)
    {
        this.year = year;
    }

    public String getDriver_id ()
    {
        return driver_id;
    }

    public void setDriver_id (String driver_id)
    {
        this.driver_id = driver_id;
    }

    public String getPeriod_end ()
    {
        return period_end;
    }

    public void setPeriod_end (String period_end)
    {
        this.period_end = period_end;
    }

    public String getPayment_date ()
{
    return payment_date;
}

    public void setPayment_date (String payment_date)
    {
        this.payment_date = payment_date;
    }

    public String getInterest_collected ()
{
    return interest_collected;
}

    public void setInterest_collected (String interest_collected)
    {
        this.interest_collected = interest_collected;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getPeriod_start ()
    {
        return period_start;
    }

    public void setPeriod_start (String period_start)
    {
        this.period_start = period_start;
    }

    public String getLate_payment_fee ()
    {
        return late_payment_fee;
    }

    public void setLate_payment_fee (String late_payment_fee)
    {
        this.late_payment_fee = late_payment_fee;
    }

    public String getIp_address ()
    {
        return ip_address;
    }

    public void setIp_address (String ip_address)
    {
        this.ip_address = ip_address;
    }

    public String getUpdated_at ()
{
    return updated_at;
}

    public void setUpdated_at (String updated_at)
    {
        this.updated_at = updated_at;
    }

    public String getSkipped ()
    {
        return skipped;
    }

    public void setSkipped (String skipped)
    {
        this.skipped = skipped;
    }

    public String getCompound_interest ()
    {
        return compound_interest;
    }

    public void setCompound_interest (String compound_interest)
    {
        this.compound_interest = compound_interest;
    }

    public String getSettlement_id ()
{
    return settlement_id;
}

    public void setSettlement_id (String settlement_id)
    {
        this.settlement_id = settlement_id;
    }

    public String getLoan_id ()
    {
        return loan_id;
    }

    public void setLoan_id (String loan_id)
    {
        this.loan_id = loan_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [company_id = "+company_id+", schedule_id = "+schedule_id+", skipped_date = "+skipped_date+", sindex = "+sindex+", emi = "+emi+", principal_left = "+principal_left+", skipped_times = "+skipped_times+", paid = "+paid+", principal = "+principal+", interest = "+interest+", paid_emi = "+paid_emi+", created_at = "+created_at+", year = "+year+", driver_id = "+driver_id+", period_end = "+period_end+", payment_date = "+payment_date+", interest_collected = "+interest_collected+", status = "+status+", period_start = "+period_start+", late_payment_fee = "+late_payment_fee+", ip_address = "+ip_address+", updated_at = "+updated_at+", skipped = "+skipped+", compound_interest = "+compound_interest+", settlement_id = "+settlement_id+", loan_id = "+loan_id+"]";
    }
}
