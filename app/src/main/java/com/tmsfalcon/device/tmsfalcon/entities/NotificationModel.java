package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Android on 1/3/2018.
 */

public class NotificationModel {

    String doc_id;
    String doc_name;
    String doc_title;
    String doc_due_date;
    String public_link;

    public String getPublic_link() {
        return public_link;
    }

    public void setPublic_link(String public_link) {
        this.public_link = public_link;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getDoc_title() {
        return doc_title;
    }

    public void setDoc_title(String doc_title) {
        this.doc_title = doc_title;
    }

    public String getDoc_due_date() {
        return doc_due_date;
    }

    public void setDoc_due_date(String doc_due_date) {
        this.doc_due_date = doc_due_date;
    }

    public String getDoc_comment() {
        return doc_comment;
    }

    public void setDoc_comment(String doc_comment) {
        this.doc_comment = doc_comment;
    }

    public String getDoc_key() {
        return doc_key;
    }

    public void setDoc_key(String doc_key) {
        this.doc_key = doc_key;
    }

    public String getDoc_status() {
        return doc_status;
    }

    public void setDoc_status(String doc_status) {
        this.doc_status = doc_status;
    }

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

    String doc_comment;
    String doc_key;
    String doc_status;
    String doc_type;

    public String getNotification_title() {
        return notification_title;
    }

    public void setNotification_title(String notification_title) {
        this.notification_title = notification_title;
    }

    public String getNotification_time() {
        return notification_time;
    }

    @SuppressWarnings("unused")
    public void setNotification_time(String notification_time) {
        this.notification_time = notification_time;
    }

    public NotificationModel(String notification_time, String notification_message, String notification_event,
                             String notification_resource_type, String notification_event_id,String notification_trip_id,
                             String resource_id,String seen_status,String doc_id,String doc_name,String doc_title,String doc_type,
                             String doc_due_date,String doc_comment,String doc_status,String doc_key,String doc_belongs_to,
                             String public_link) {
        this.notification_time = notification_time;
        this.notification_message = notification_message;
        this.notification_event = notification_event;
        this.notification_resource_type = notification_resource_type;
        this.notification_event_id = notification_event_id;
        this.notification_trip_id = notification_trip_id;
        this.resource_id = resource_id;
        this.seen_status = seen_status;
        this.doc_id = doc_id;
        this.doc_name = doc_name;
        this.doc_title = doc_title;
        this.doc_type = doc_type;
        this.doc_due_date = doc_due_date;
        this.doc_comment = doc_comment;
        this.doc_status = doc_status;
        this.doc_key = doc_key;
        this.doc_belongs_to = doc_belongs_to;
        this.public_link = public_link;

    }

    public String getDoc_belongs_to() {
        return doc_belongs_to;
    }

    public void setDoc_belongs_to(String doc_belongs_to) {
        this.doc_belongs_to = doc_belongs_to;
    }

    String notification_title;
    String notification_time;
    String doc_belongs_to;

    public String getSeen_status() {
        return seen_status;
    }

    public void setSeen_status(String seen_status) {
        this.seen_status = seen_status;
    }

    String seen_status;

    public String getNotification_trip_id() {
        return notification_trip_id;
    }

    public void setNotification_trip_id(String notification_trip_id) {
        this.notification_trip_id = notification_trip_id;
    }

    String notification_trip_id;

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    String resource_id;

    public String getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(String notification_message) {
        this.notification_message = notification_message;
    }

    public String getNotification_event() {
        return notification_event;
    }

    public void setNotification_event(String notification_event) {
        this.notification_event = notification_event;
    }

    public String getNotification_resource_type() {
        return notification_resource_type;
    }

    public void setNotification_resource_type(String notification_resource_type) {
        this.notification_resource_type = notification_resource_type;
    }

    public String getNotification_event_id() {
        return notification_event_id;
    }

    public void setNotification_event_id(String notification_event_id) {
        this.notification_event_id = notification_event_id;
    }

    String notification_message;
    String notification_event;
    String notification_resource_type;
    String notification_event_id;
}
