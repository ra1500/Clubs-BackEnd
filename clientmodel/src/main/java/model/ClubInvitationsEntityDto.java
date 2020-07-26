package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.entity.ClubsEntity;
import db.entity.UserEntity;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubInvitationsEntityDto implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("sender")
    private UserEntity sender;

    @JsonProperty("receiver")
    private String receiver;

    @JsonProperty("status")
    private Long status; // 1pending, 2accepted, 3declined, 4blocked, 5 clubFull

    @JsonProperty("club")
    private ClubsEntity club;

    public ClubInvitationsEntityDto() {
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

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public ClubsEntity getClub() {
        return club;
    }

    public void setClub(ClubsEntity club) {
        this.club = club;
    }
}