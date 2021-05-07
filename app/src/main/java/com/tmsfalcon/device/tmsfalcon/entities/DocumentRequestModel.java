package com.tmsfalcon.device.tmsfalcon.entities;

/**
 * Created by Android on 8/30/2017.
 */

public class DocumentRequestModel {
    public DocumentRequestModel(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;
    String rejected_reason;

    public String getRejected_reason() {
        return rejected_reason;
    }

    public void setRejected_reason(String rejected_reason) {
        this.rejected_reason = rejected_reason;
    }

    public String getPrevious_doc_url() {
        return previous_doc_url;
    }

    public void setPrevious_doc_url(String previous_doc_url) {
        this.previous_doc_url = previous_doc_url;
    }

    String previous_doc_url;
    int is_request_documents_module;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getPrevious_doc_type() {
        return previous_doc_type;
    }

    public void setPrevious_doc_type(String previous_doc_type) {
        this.previous_doc_type = previous_doc_type;
    }

    String previous_doc_type;

    public String getDocument_description() {
        return document_description;
    }

    public void setDocument_description(String document_description) {
        this.document_description = document_description;
    }

    String document_description;


    public DocumentRequestModel(String document_code, String document_request_id, String document_belongs_to, String documents, String title, String request_document_from_type, String request_method, String comment, String document_due_date, String status, String document_id, String updated_at,String key,String rejected_reason,String previous_doc_url,String previous_doc_type) {
        this.document_code = document_code;
        this.document_request_id = document_request_id;
        this.document_belongs_to = document_belongs_to;
        this.documents = documents;
        this.title = title;
        this.request_document_from_type = request_document_from_type;
        this.request_method = request_method;
        this.comment = comment;
        this.document_due_date = document_due_date;
        this.status = status;
        this.document_id = document_id;
        this.updated_at = updated_at;
        this.key = key;
        this.rejected_reason = rejected_reason;
        this.previous_doc_url = previous_doc_url;
        this.previous_doc_type = previous_doc_type; //Just to differentiate between the constructors
    }
    //Constructor to be used when displaying data in requested uploaded documents through adapter
    public DocumentRequestModel(String document_code, String document_request_id, String document_belongs_to, String documents, String title, String request_document_from_type, String request_method, String comment, String document_due_date, String status, String document_id, String updated_at,String u_id,String file_url,String file_type) {
        this.document_code = document_code;
        this.document_request_id = document_request_id;
        this.document_belongs_to = document_belongs_to;
        this.documents = documents;
        this.title = title;
        this.request_document_from_type = request_document_from_type;
        this.request_method = request_method;
        this.comment = comment;
        this.document_due_date = document_due_date;
        this.status = status;
        this.document_id = document_id;
        this.updated_at = updated_at;
        this.u_id = u_id;
        this.file_url = file_url;
        this.file_type = file_type;
    }

    //Constructor to be used when displaying data in uploaded documents through adapter
    public DocumentRequestModel(String document_belongs_to, String document_type, String document_due_date, String status, String file_url,String file_type) {
        this.document_belongs_to = document_belongs_to;
        this.document_type = document_type;
        this.document_due_date = document_due_date;
        this.status = status;
        this.file_url = file_url;
        this.file_type = file_type;
    }
    //Constructor to be used when displaying data in captured documents through adapter
    public DocumentRequestModel(String document_belongs_to, String document_type, String document_due_date, String status, String file_url,String file_type,String document_description,String id) {
        this.document_belongs_to = document_belongs_to;
        this.document_type = document_type;
        this.document_due_date = document_due_date;
        this.status = status;
        this.file_url = file_url;
        this.file_type = file_type;
        this.document_description = document_description;
        this.id = id;
    }

    //Constructor for inserting direct upload images
    public DocumentRequestModel(String file_url){
        this.file_url = file_url;
       /* this.document_id = document_id;
        this.document_belongs_to = document_belongs_to;
        this.document_type = document_type;*/
    }

    public String getDb_id() {
        return db_id;
    }

    public void setDb_id(String db_id) {
        this.db_id = db_id;
    }

    String db_id;

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    String document_type;

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    String file_type;
    //This constructor is used for saving Uploaded Documents(by Driver) In Db
    public DocumentRequestModel(String document_request_id,String document_code,String file_url,String documnet_name,String load_number,String due_date,String comment,String status,String key){
        this.document_request_id = document_request_id;
        this.document_code = document_code;
        this.file_url = file_url;
        this.documents = documnet_name;
        this.title = load_number;
        this.document_due_date = due_date;
        this.comment = comment;
        this.status = status;
        this.key = key;
    }

    String document_code;
    String document_request_id;

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    String u_id;

    public String getDocument_code() {
        return document_code;
    }

    public void setDocument_code(String document_code) {
        this.document_code = document_code;
    }

    public String getDocument_request_id() {
        return document_request_id;
    }

    public void setDocument_request_id(String document_request_id) {
        this.document_request_id = document_request_id;
    }

    @SuppressWarnings("unused")
    public String getDocument_belongs_to() {
        return document_belongs_to;
    }

    public void setDocument_belongs_to(String document_belongs_to) {
        this.document_belongs_to = document_belongs_to;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequest_document_from_type() {
        return request_document_from_type;
    }

    public void setRequest_document_from_type(String request_document_from_type) {
        this.request_document_from_type = request_document_from_type;
    }

    public String getRequest_method() {
        return request_method;
    }

    public void setRequest_method(String request_method) {
        this.request_method = request_method;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDocument_due_date() {
        return document_due_date;
    }

    public void setDocument_due_date(String document_due_date) {
        this.document_due_date = document_due_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    String document_belongs_to;
    String documents;
    String title;
    String request_document_from_type;
    String request_method;
    String comment;
    String document_due_date;
    String status;
    String document_id;
    String updated_at;

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    String file_url;
}
