package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Android on 7/5/2017.
 */

public class DriverModel {

    int _id,_driver_id,_should_update_password;
    String _nick_name;
    String _thumb;
    String _uid;
    String _full_name;
    String _company_name;
    String _company_address;
    String _company_country;
    String _company_state;
    String _company_city;
    String _company_zip_code;
    String _company_ein;
    String _username;
    String _first_name;
    String _middle_name;
    String _last_name;
    String _gender;
    String _dob;
    String _email;
    String _type;
    String _ssn;
    String _dl;
    String _dl_state;
    String _dl_expiration;
    String _dl_additional;
    String _home_phone;
    String _address;
    String _country;
    String _state;
    String _city;
    String _zip_code;
    String _address_duration_years;
    String _address_duration_months;
    String _emergency_contact_person;
    String _emergency_contact_relationship;
    String _emergency_contact_cell;
    String _token;

    public DriverModel(int _driver_id, String _full_name, int _should_update_password,String _nick_name,  String _thumb, String _uid,  String _company_name, String _company_address, String _company_country, String _company_state, String _company_city,String _company_zip_code, String _company_ein,  String _username, String _first_name, String _middle_name, String _last_name, String _gender, String _dob, String _email, String _type, String _ssn, String _dl, String _dl_state, String _dl_expiration, String _dl_additional, String _home_phone, String _address, String _country, String _state, String _city, String _zip_code, String _address_duration_years, String _address_duration_months, String _emergency_contact_person, String _emergency_contact_relationship, String _emergency_contact_cell, String _token) {
        this._driver_id = _driver_id;
        this._should_update_password = _should_update_password;
        this._nick_name = _nick_name;
        this._thumb = _thumb;
        this._uid = _uid;
        this._full_name = _full_name;
        this._company_name = _company_name;
        this._company_address = _company_address;
        this._company_country = _company_country;
        this._company_state = _company_state;
        this._company_city = _company_city;
        this._company_zip_code = _company_zip_code;
        this._company_ein = _company_ein;
        this._username = _username;
        this._first_name = _first_name;
        this._middle_name = _middle_name;
        this._last_name = _last_name;
        this._gender = _gender;
        this._dob = _dob;
        this._email = _email;
        this._type = _type;
        this._ssn = _ssn;
        this._dl = _dl;
        this._dl_state = _dl_state;
        this._dl_expiration = _dl_expiration;
        this._dl_additional = _dl_additional;
        this._home_phone = _home_phone;
        this._address = _address;
        this._country = _country;
        this._state = _state;
        this._city = _city;
        this._zip_code = _zip_code;
        this._address_duration_years = _address_duration_years;
        this._address_duration_months = _address_duration_months;
        this._emergency_contact_person = _emergency_contact_person;
        this._emergency_contact_relationship = _emergency_contact_relationship;
        this._emergency_contact_cell = _emergency_contact_cell;
        this._token = _token;
    }

    @SuppressWarnings("unused")
    public DriverModel(int _id, int _driver_id, String _full_name, int _should_update_password, String _nick_name, String _thumb, String _uid, String _company_name, String _company_address, String _company_country, String _company_state, String _company_city, String _company_zip_code, String _company_ein, String _username, String _first_name, String _middle_name, String _last_name, String _gender, String _dob, String _email, String _type, String _ssn, String _dl, String _dl_state, String _dl_expiration, String _dl_additional, String _home_phone, String _address, String _country, String _state, String _city, String _zip_code, String _address_duration_years, String _address_duration_months, String _emergency_contact_person, String _emergency_contact_relationship, String _emergency_contact_cell, String _token) {
        this._id = _id;
        this._driver_id = _driver_id;
        this._should_update_password = _should_update_password;
        this._nick_name = _nick_name;
        this._thumb = _thumb;
        this._uid = _uid;
        this._full_name = _full_name;
        this._company_name = _company_name;
        this._company_address = _company_address;
        this._company_country = _company_country;
        this._company_state = _company_state;
        this._company_city = _company_city;
        this._company_zip_code = _company_zip_code;
        this._company_ein = _company_ein;
        this._username = _username;
        this._first_name = _first_name;
        this._middle_name = _middle_name;
        this._last_name = _last_name;
        this._gender = _gender;
        this._dob = _dob;
        this._email = _email;
        this._type = _type;
        this._ssn = _ssn;
        this._dl = _dl;
        this._dl_state = _dl_state;
        this._dl_expiration = _dl_expiration;
        this._dl_additional = _dl_additional;
        this._home_phone = _home_phone;
        this._address = _address;
        this._country = _country;
        this._state = _state;
        this._city = _city;
        this._zip_code = _zip_code;
        this._address_duration_years = _address_duration_years;
        this._address_duration_months = _address_duration_months;
        this._emergency_contact_person = _emergency_contact_person;
        this._emergency_contact_relationship = _emergency_contact_relationship;
        this._emergency_contact_cell = _emergency_contact_cell;
        this._token = _token;
    }

    public String get_token() {
        return _token;
    }

    public void set_token(String _token) {
        this._token = _token;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_driver_id() {
        return _driver_id;
    }

    public void set_driver_id(int _driver_id) {
        this._driver_id = _driver_id;
    }

    public int get_should_update_password() {
        return _should_update_password;
    }

    public void set_should_update_password(int _should_update_password) {
        this._should_update_password = _should_update_password;
    }

    public String get_nick_name() {
        return _nick_name;
    }

    @SuppressWarnings("unused")
    public void set_nick_name(String _nick_name) {
        this._nick_name = _nick_name;
    }

    public String get_thumb() {
        return _thumb;
    }

    public void set_thumb(String _thumb) {
        this._thumb = _thumb;
    }

    public String get_uid() {
        return _uid;
    }

    public void set_uid(String _uid) {
        this._uid = _uid;
    }

    public String get_full_name() {
        return _full_name;
    }

    @SuppressWarnings("unused")
    public void set_full_name(String _full_name) {
        this._full_name = _full_name;
    }

    public String get_company_name() {
        return _company_name;
    }

    @SuppressWarnings("unused")
    public void set_company_name(String _company_name) {
        this._company_name = _company_name;
    }

    public String get_company_address() {
        return _company_address;
    }

    public void set_company_address(String _company_address) {
        this._company_address = _company_address;
    }

    public String get_company_country() {
        return _company_country;
    }

    @SuppressWarnings("unused")
    public void set_company_country(String _company_country) {
        this._company_country = _company_country;
    }

    public String get_company_state() {
        return _company_state;
    }

    public void set_company_state(String _company_state) {
        this._company_state = _company_state;
    }

    public String get_company_city() {
        return _company_city;
    }

    public void set_company_city(String _company_city) {
        this._company_city = _company_city;
    }

    public String get_company_zip_code() {
        return _company_zip_code;
    }

    public void set_company_zip_code(String _company_zip_code) {
        this._company_zip_code = _company_zip_code;
    }

    public String get_company_ein() {
        return _company_ein;
    }

    @SuppressWarnings("unused")
    public void set_company_ein(String _company_ein) {
        this._company_ein = _company_ein;
    }

    public String get_username() {
        return _username;
    }

    @SuppressWarnings("unused")
    public void set_username(String _username) {
        this._username = _username;
    }

    public String get_first_name() {
        return _first_name;
    }

    public void set_first_name(String _first_name) {
        this._first_name = _first_name;
    }

    public String get_middle_name() {
        return _middle_name;
    }

    public void set_middle_name(String _middle_name) {
        this._middle_name = _middle_name;
    }

    public String get_last_name() {
        return _last_name;
    }

    public void set_last_name(String _last_name) {
        this._last_name = _last_name;
    }

    public String get_gender() {
        return _gender;
    }

    @SuppressWarnings("unused")
    public void set_gender(String _gender) {
        this._gender = _gender;
    }

    public String get_dob() {
        return _dob;
    }

    public void set_dob(String _dob) {
        this._dob = _dob;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_type() {
        return _type;
    }

    @SuppressWarnings("unused")
    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_ssn() {
        return _ssn;
    }

    public void set_ssn(String _ssn) {
        this._ssn = _ssn;
    }

    public String get_dl() {
        return _dl;
    }

    @SuppressWarnings("unused")
    public void set_dl(String _dl) {
        this._dl = _dl;
    }

    public String get_dl_state() {
        return _dl_state;
    }

    @SuppressWarnings("unused")
    public void set_dl_state(String _dl_state) {
        this._dl_state = _dl_state;
    }

    public String get_dl_expiration() {
        return _dl_expiration;
    }

    @SuppressWarnings("unused")
    public void set_dl_expiration(String _dl_expiration) {
        this._dl_expiration = _dl_expiration;
    }

    public String get_dl_additional() {
        return _dl_additional;
    }

    @SuppressWarnings("unused")
    public void set_dl_additional(String _dl_additional) {
        this._dl_additional = _dl_additional;
    }

    public String get_home_phone() {
        return _home_phone;
    }

    public void set_home_phone(String _home_phone) {
        this._home_phone = _home_phone;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_country() {
        return _country;
    }

    public void set_country(String _country) {
        this._country = _country;
    }

    public String get_state() {
        return _state;
    }

    public void set_state(String _state) {
        this._state = _state;
    }

    public String get_city() {
        return _city;
    }

    public void set_city(String _city) {
        this._city = _city;
    }

    public String get_zip_code() {
        return _zip_code;
    }

    public void set_zip_code(String _zip_code) {
        this._zip_code = _zip_code;
    }

    public String get_address_duration_years() {
        return _address_duration_years;
    }

    public void set_address_duration_years(String _address_duration_years) {
        this._address_duration_years = _address_duration_years;
    }

    public String get_address_duration_months() {
        return _address_duration_months;
    }

    public void set_address_duration_months(String _address_duration_months) {
        this._address_duration_months = _address_duration_months;
    }

    public String get_emergency_contact_person() {
        return _emergency_contact_person;
    }

    public void set_emergency_contact_person(String _emergency_contact_person) {
        this._emergency_contact_person = _emergency_contact_person;
    }

    public String get_emergency_contact_relationship() {
        return _emergency_contact_relationship;
    }

    public void set_emergency_contact_relationship(String _emergency_contact_relationship) {
        this._emergency_contact_relationship = _emergency_contact_relationship;
    }

    public String get_emergency_contact_cell() {
        return _emergency_contact_cell;
    }

    public void set_emergency_contact_cell(String _emergency_contact_cell) {
        this._emergency_contact_cell = _emergency_contact_cell;
    }



}
