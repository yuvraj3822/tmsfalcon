package com.tmsfalcon.device.tmsfalcon.customtools;

/**
 * Created by Android on 7/4/2017.
 */

public class UrlController {


    public static final String HOST = "";
    //public static final String BASE = "http://tmsfalcon.gov:8081/api-v1/Driver/";
    public static final String BASE = "https://tmsfalcon.com/api-v1/driver/";
    //public static final String BASE = "http://tmsfalcon.com/api-v1/driver/";
    public static final String LOGIN_URL = BASE + "login";
    public static final String FORGOT_PASSWORD_URL = BASE +  "login/forgot";
    public static final String SHOULD_UPDATE_PASSWORD_URL = BASE +  "login/set_password";
    public static final String PROFILE = BASE +  "login/profile";
    public static final String RESOURCES = BASE +  "login/resources";
    public static final String TRUCK = BASE +  "login/truck";
    public static final String TRAILER = BASE +  "login/trailer";
    public static final String TRUCK_DOCUMENTS = BASE + "login/truck_documents";
    public static final String TRAILER_DOCUMENTS = BASE + "login/trailer_documents";
    public static final String COMPANY_INFO = BASE + "login/company_info";
    public static final String DISPACHER_INFO = BASE + "login/dispatcher_info";
    public static final String DAY_OFF = BASE + "login/day_off";
    public static final String SEND_LOCATION = BASE + "login/set_location";
    public static final String LOAN = BASE + "login/loans";
    public static final String ESCROW_ACCOUNTS = BASE + "login/escrow_accounts";
    public static final String FUEL_REBATE_SCHEDULE = BASE + "login/fuel_rebate_schedule";
    public static final String DOCUMENTS_REQUESTS = BASE + "login/document_requests";
    public static final String UPLOADED_DOCUMENTS = BASE + "login/document_uploaded";
    public static final String EMAIL_SUGGESTIONS = BASE + "login/email_suggestions";
    public static final String PROFILE_DOCUMENTS = BASE +  "login/documents";
    public static final String UPCOMING_TRIPS = BASE +  "login/upcoming_trips";
    public static final String COMPLETED_TRIPS = BASE +  "login/completed_trips";
    public static final String CURRENT_TRIPS = BASE +  "login/current_trip";
    public static final String TRIP_DOCUMENTS = BASE +  "login/trip_documents";
    public static final String TRIP_SETTLEMENT = BASE +  "login/trip_settlement";
    public static final String TRIP_SETTLEMENT_LIST = BASE +  "login/driver_settlements";
    public static final String PHONES = BASE +  "login/get_phones";
    public static final String RINGCENTRAL = BASE +  "login/ring_central";
    public static final String LOGOUT = BASE +  "login/logout";
    public static final String LOAN_DETAIL = BASE +  "login/loan_detail";
    public static final String HOS_VIOLATION = BASE +  "login/hos_violation";
    public static final String FAULT_CODES = BASE +  "login/truck_fault_codes";
    public static final String EXPIRED_DOCUMENTS = BASE +  "login/get_expired_document";
    public static final String RINGOUT = BASE +  "login/ringout";
    public static final String GET_DOCUMENT_TYPES = BASE +  "login/get_document_types";
    public static final String CAPTURED_UPLOADED_DOCUMENTS = BASE +  "login/document_direct_uploaded";
    public static final String UPDATE_USER_PRESENCE_STATUS = BASE +  "login/update_user_presence_status";
    public static final String UPDATE_NOTIFICATION_STATUS = BASE +  "login/update_notification_status";
    public static final String FETCH_DOCUMENT_DETAIL = BASE +  "login/get_document_details";
    public static final String FETCH_SUGGESTED_LOADS = BASE +  "login/get_suggested_loads";
    public static final String SET_LOAD_OFFER_RESPONSE = BASE +  "login/set_response";
    public static final String LOAD_BOARD_SETTINGS_DATA = BASE +"login/calllog_settings_data";
    public static final String FUEL_REBATE_SUMMARY = BASE +"login/fuel_rebate_summary";
    public static final String SETTLEMENT_BATCHES = BASE + "login/driver_settlement_batches";
    public static final String FETCH_CITY_STATES = BASE + "login/fetch_city_states";
    public static final String LOAN_INSTALLMENTS = BASE +"login/loan_installments";
    public static final String GET_CASH_OPTIONS = BASE+"login/get_cash_options";
    public static final String GET_CASH = BASE+"login/get_cash";
    public static final String CASH_HISTORY = BASE+"login/get_cash_history";
    public static final String SEND_EMAIL = BASE+"login/send_email";
    public static final String ACCIDENT_DETAIL = BASE+"login/accident_detail";
    public static final String ACCIDENT_DOCUMENTS = BASE+"login/upload_accident_document";
    public static final String VIOLATIONS = BASE+"login/violations";
    public static final String ACCIDENT_RECORDS = BASE+"login/accident_records";
    public static final String DRIVER_INFO = BASE+"login/driver_info";

    public static final String DEV_TLL_SETTLEMENT_LIST = "https://tmsfalcon.com/settlements/get_settlements";
    //public static final String DEV_TLL_SETTLEMENT_LIST = "http://tmsfalcon.com/settlements/get_settlements";
    //FCM
    public static final String REGISTER_DEVICE_TOKEN = BASE +  "notifications/register";
    public static final String FETCH_ALL_NOTIFICATIONS = BASE +  "notifications/get_all_notification";
    public static final String UPDATE_SEEN_AT = BASE +  "notifications/updateSeenat";



}
