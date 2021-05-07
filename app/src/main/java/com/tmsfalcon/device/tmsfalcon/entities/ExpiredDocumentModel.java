package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Dell on 1/18/2019.
 */

public class ExpiredDocumentModel {

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getDoc_belongs_to() {
        return doc_belongs_to;
    }

    public void setDoc_belongs_to(String doc_belongs_to) {
        this.doc_belongs_to = doc_belongs_to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String file_name;
    String document_type;
    String u_id;
    String tracking;
    String url;
    String file_type;
    String thumb;
    String expiry_date;
    String doc_belongs_to;
    String status;

    //for expired documents
    public ExpiredDocumentModel(String id, String file_name, String document_type,String code, String u_id,  String url, String file_type,String thumb,String expiry_date,String doc_belongs_to,String status ) {
        this.id = id;
        this.file_name = file_name;
        this.document_type = document_type;
        this.u_id = u_id;
        this.tracking = code;
        this.url = url;
        this.file_type = file_type;
        this.thumb = thumb;
        this.expiry_date = expiry_date;
        this.doc_belongs_to = doc_belongs_to;
        this.status = status;
    }
}
