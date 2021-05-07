package com.tmsfalcon.device.tmsfalcon.entities;

public class FmcsaHosLogModel {

    int hos_violation_id;
    int violation_id;
    int driver_id;

    public String getUnit_number() {
        return unit_number;
    }

    public void setUnit_number(String unit_number) {
        this.unit_number = unit_number;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    String unit_number,resource_type;

    public int getRequest_type_vehicle() {
        return request_type_vehicle;
    }

    public void setRequest_type_vehicle(int request_type_vehicle) {
        this.request_type_vehicle = request_type_vehicle;
    }

    int request_type_vehicle;

    public FmcsaHosLogModel(int req_type_vehicle,int hos_violation_id, int violation_id, int driver_id, int company_id, int is_deleted, int oos, String report, String state, String location, String section, String description, String datetime, String created_at, String updated_at) {
        this.hos_violation_id = hos_violation_id;
        this.violation_id = violation_id;
        this.driver_id = driver_id;
        this.company_id = company_id;
        this.is_deleted = is_deleted;
        this.oos = oos;
        this.report = report;
        this.state = state;
        this.location = location;
        this.section = section;
        this.description = description;
        this.datetime = datetime;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.request_type_vehicle = req_type_vehicle;
    }

    public FmcsaHosLogModel(int req_type_vehicle,int hos_violation_id, int violation_id, int driver_id,
                            int company_id, int is_deleted, int oos, String report, String state, String location,
                            String section, String description, String datetime, String created_at,
                            String updated_at,String resource_type,String unit_num) {
        this.hos_violation_id = hos_violation_id;
        this.violation_id = violation_id;
        this.driver_id = driver_id;
        this.company_id = company_id;
        this.is_deleted = is_deleted;
        this.oos = oos;
        this.report = report;
        this.state = state;
        this.location = location;
        this.section = section;
        this.description = description;
        this.datetime = datetime;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.request_type_vehicle = req_type_vehicle;
        this.resource_type = resource_type;
        this.unit_number = unit_num;
    }

    int company_id;

    public int getHos_violation_id() {
        return hos_violation_id;
    }

    public void setHos_violation_id(int hos_violation_id) {
        this.hos_violation_id = hos_violation_id;
    }

    public int getViolation_id() {
        return violation_id;
    }

    public void setViolation_id(int violation_id) {
        this.violation_id = violation_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }

    public int getOos() {
        return oos;
    }

    public void setOos(int oos) {
        this.oos = oos;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    int is_deleted;
    int oos;
    String report,state,location,section,description,datetime,created_at,updated_at;
}
