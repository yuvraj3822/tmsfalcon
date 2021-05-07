package com.tmsfalcon.device.tmsfalcon.entities;

import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;

/**
 * Created by Android on 7/13/2017.
 */

public class DriverEmploymentModel {
    String employer_name,employment_address,employment_city,employment_state,employment_country,employment_zip_code,employment_position,
            employment_fax,employment_email,employment_from,employment_to,employment_salary,employment_leaving_reason,employment_mc,
            is_subjected_fmcsr,is_subjected_drug;

    public String getEmployer_id() {
        return employer_id;
    }

    public void setEmployer_id(String employer_id) {
        this.employer_id = employer_id;
    }

    String employer_id;

    public DriverEmploymentModel(String employer_name, String employment_address, String employment_city, String employment_state, String employment_country, String employment_zip_code, String employment_position, String employment_fax, String employment_email, String employment_from, String employment_to, String employment_salary, String employment_leaving_reason, String employment_mc) {
        this.employer_name = employer_name;
        this.employment_address = employment_address;
        this.employment_city = employment_city;
        this.employment_state = employment_state;
        this.employment_country = employment_country;
        this.employment_zip_code = employment_zip_code;
        this.employment_position = employment_position;
        this.employment_fax = employment_fax;
        this.employment_email = employment_email;
        this.employment_from = employment_from;
        this.employment_to = employment_to;
        this.employment_salary = employment_salary;
        this.employment_leaving_reason = employment_leaving_reason;
        this.employment_mc = employment_mc;
    }

    public String getEmployer_name() {
        return CustomValidator.setModelStringData(employer_name);
        //return employer_name;
    }

    public void setEmployer_name(String employer_name) {
        this.employer_name = employer_name;
    }

    public String getEmployment_address() {
        return CustomValidator.setModelStringData(employment_address);
        //return employment_address;
    }

    public void setEmployment_address(String employment_address) {
        this.employment_address = employment_address;
    }

    public String getEmployment_city() {
        return employment_city;
    }

    @SuppressWarnings("unused")
    public void setEmployment_city(String employment_city) {
        this.employment_city = employment_city;
    }

    public String getEmployment_state() {
        return employment_state;
    }

    public void setEmployment_state(String employment_state) {
        this.employment_state = employment_state;
    }

    public String getEmployment_country() {
        return employment_country;
    }

    @SuppressWarnings("unused")
    public void setEmployment_country(String employment_country) {
        this.employment_country = employment_country;
    }

    @SuppressWarnings("unused")
    public String getEmployment_zip_code() {
        return employment_zip_code;
    }

    public void setEmployment_zip_code(String employment_zip_code) {
        this.employment_zip_code = employment_zip_code;
    }

    public String getEmployment_position() {
        return CustomValidator.setModelStringData(employment_position);
        //return employment_position;
    }

    public void setEmployment_position(String employment_position) {
        this.employment_position = employment_position;
    }

    public String getEmployment_fax() {
        return CustomValidator.setModelStringData(employment_fax);
        //return employment_fax;
    }

    @SuppressWarnings("unused")
    public void setEmployment_fax(String employment_fax) {
        this.employment_fax = employment_fax;
    }

    public String getEmployment_email() {
        return CustomValidator.setModelStringData(employment_email);
        //return employment_email;
    }

    public void setEmployment_email(String employment_email) {
        this.employment_email = employment_email;
    }

    public String getEmployment_from() {
        return CustomValidator.setModelStringData(employment_from);
        //return employment_from;
    }

    public void setEmployment_from(String employment_from) {
        this.employment_from = employment_from;
    }

    public String getEmployment_to() {
        return CustomValidator.setModelStringData(employment_to);
        //return employment_to;
    }

    @SuppressWarnings("unused")
    public void setEmployment_to(String employment_to) {
        this.employment_to = employment_to;
    }

    public String getEmployment_salary() {
        return CustomValidator.setModelStringData(employment_salary);
        //return employment_salary;
    }

    public void setEmployment_salary(String employment_salary) {
        this.employment_salary = employment_salary;
    }

    public String getEmployment_leaving_reason() {
        return CustomValidator.setModelStringData(employment_leaving_reason);
        //return employment_leaving_reason;
    }

    @SuppressWarnings("unused")
    public void setEmployment_leaving_reason(String employment_leaving_reason) {
        this.employment_leaving_reason = employment_leaving_reason;
    }

    public String getEmployment_mc() {
        return CustomValidator.setModelStringData(employment_mc);
        //return employment_mc;
    }

    @SuppressWarnings("unused")
    public void setEmployment_mc(String employment_mc) {
        this.employment_mc = employment_mc;
    }

    @SuppressWarnings("unused")
    public String getIs_subjected_fmcsr() {
        return is_subjected_fmcsr;
    }

    @SuppressWarnings("unused")
    public void setIs_subjected_fmcsr(String is_subjected_fmcsr) {
        this.is_subjected_fmcsr = is_subjected_fmcsr;
    }

    public String getIs_subjected_drug() {
        return is_subjected_drug;
    }

    public void setIs_subjected_drug(String is_subjected_drug) {
        this.is_subjected_drug = is_subjected_drug;
    }
}
