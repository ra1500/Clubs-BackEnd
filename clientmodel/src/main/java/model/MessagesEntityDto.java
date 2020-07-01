package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.entity.UserEntity;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessagesEntityDto implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("message")
    private String message;

    @JsonProperty("sender")
    private UserEntity sender;

    @JsonProperty("receiverType")
    private Long receiverType;

    @JsonProperty("receiverId")
    private Long receiverId;

    @JsonProperty("redFlag")
    private Long redFlag;

    public MessagesEntityDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserEntity getSender() {return sender;}

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public Long getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(Long receiverType) {
        this.receiverType = receiverType;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getRedFlag() {return redFlag;}

    public void setRedFlag(Long redFlag) {this.redFlag = redFlag;}
}
