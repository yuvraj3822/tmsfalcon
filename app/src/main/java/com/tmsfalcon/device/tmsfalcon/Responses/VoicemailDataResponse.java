package com.tmsfalcon.device.tmsfalcon.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dell on 4/16/2019.
 */

public class VoicemailDataResponse {

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @SerializedName("status")
    @Expose
    private Boolean status;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @SerializedName("access_token")
    @Expose
    private String access_token;

    @SerializedName("data")
    @Expose
    private Data data;

    public class Data{
        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public List<Records> getRecordsList() {
            return recordsList;
        }

        public void setRecordsList(List<Records> recordsList) {
            this.recordsList = recordsList;
        }

        @SerializedName("uri")
        @Expose
        private String uri;

        @SerializedName("records")
        @Expose
        private List<Records> recordsList = null;

        public class Records{
            public String getRecord_uri() {
                return record_uri;
            }

            public void setRecord_uri(String record_uri) {
                this.record_uri = record_uri;
            }

            public String getRecord_id() {
                return record_id;
            }

            public void setRecord_id(String record_id) {
                this.record_id = record_id;
            }

            public List<MessageTo> getMessageToList() {
                return messageToList;
            }

            public void setMessageToList(List<MessageTo> messageToList) {
                this.messageToList = messageToList;
            }

            public MessageFrom getMessageFrom() {
                return messageFrom;
            }

            public void setMessageFrom(MessageFrom messageFrom) {
                this.messageFrom = messageFrom;
            }

            public String getRecord_type() {
                return record_type;
            }

            public void setRecord_type(String record_type) {
                this.record_type = record_type;
            }

            public String getRecord_creationTime() {
                return record_creationTime;
            }

            public void setRecord_creationTime(String record_creationTime) {
                this.record_creationTime = record_creationTime;
            }

            public String getRecord_readStatus() {
                return record_readStatus;
            }

            public void setRecord_readStatus(String record_readStatus) {
                this.record_readStatus = record_readStatus;
            }

            public String getRecord_priority() {
                return record_priority;
            }

            public void setRecord_priority(String record_priority) {
                this.record_priority = record_priority;
            }

            public List<Attachment> getAttachmentList() {
                return attachmentList;
            }

            public void setAttachmentList(List<Attachment> attachmentList) {
                this.attachmentList = attachmentList;
            }

            public String getRecord_messageStatusy() {
                return record_messageStatusy;
            }

            public void setRecord_messageStatusy(String record_messageStatusy) {
                this.record_messageStatusy = record_messageStatusy;
            }

            public String getRecord_conversationId() {
                return record_conversationId;
            }

            public void setRecord_conversationId(String record_conversationId) {
                this.record_conversationId = record_conversationId;
            }

            @SerializedName("uri")
            @Expose
            private String record_uri;

            @SerializedName("id")
            @Expose
            private String record_id;

            @SerializedName("to")
            @Expose
            private List<MessageTo> messageToList = null;

            public class MessageTo{
                public String getMessage_to_name() {
                    return message_to_name;
                }

                public void setMessage_to_name(String message_to_name) {
                    this.message_to_name = message_to_name;
                }

                @SerializedName("name")
                @Expose

                private String message_to_name;
            }

            @SerializedName("from")
            @Expose
            private MessageFrom messageFrom = null;

            public class MessageFrom{
                public String getMessage_from_phone() {
                    return message_from_phone;
                }

                public void setMessage_from_phone(String message_from_phone) {
                    this.message_from_phone = message_from_phone;
                }

                public String getMessage_from_name() {
                    return message_from_name;
                }

                public void setMessage_from_name(String message_from_name) {
                    this.message_from_name = message_from_name;
                }

                @SerializedName("phoneNumber")
                @Expose

                private String message_from_phone;

                @SerializedName("name")
                @Expose
                private String message_from_name;
            }

            @SerializedName("type")
            @Expose
            private String record_type;

            @SerializedName("creationTime")
            @Expose
            private String record_creationTime;

            @SerializedName("readStatus")
            @Expose
            private String record_readStatus;

            @SerializedName("priority")
            @Expose
            private String record_priority;

            @SerializedName("attachments")
            @Expose
            private List<Attachment> attachmentList = null;

            public class Attachment{
                public String getAttachment_id() {
                    return attachment_id;
                }

                public void setAttachment_id(String attachment_id) {
                    this.attachment_id = attachment_id;
                }

                public String getAttachment_uri() {
                    return attachment_uri;
                }

                public void setAttachment_uri(String attachment_uri) {
                    this.attachment_uri = attachment_uri;
                }

                public String getAttachment_type() {
                    return attachment_type;
                }

                public void setAttachment_type(String attachment_type) {
                    this.attachment_type = attachment_type;
                }

                public String getAttachment_contentType() {
                    return attachment_contentType;
                }

                public void setAttachment_contentType(String attachment_contentType) {
                    this.attachment_contentType = attachment_contentType;
                }

                @SerializedName("id")

                @Expose
                private String attachment_id;

                @SerializedName("uri")
                @Expose
                private String attachment_uri;

                @SerializedName("type")
                @Expose
                private String attachment_type;

                public String getAttachment_vmDuration() {
                    return attachment_vmDuration;
                }

                public void setAttachment_vmDuration(String attachment_vmDuration) {
                    this.attachment_vmDuration = attachment_vmDuration;
                }

                @SerializedName("vmDuration")
                @Expose
                private String attachment_vmDuration;

                @SerializedName("contentType")
                @Expose
                private String attachment_contentType;
            }

            @SerializedName("messageStatus")
            @Expose
            private String record_messageStatusy;

            @SerializedName("conversationId")
            @Expose
            private String record_conversationId;

        }
    }
}
