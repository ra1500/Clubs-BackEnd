package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.entity.ClubsEntity;
import db.entity.UserEntity;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VotesEntityDto implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("club")
    private ClubsEntity club;

    @JsonProperty("voter")
    private UserEntity voter;

    @JsonProperty("voterType")
    private Long voteType;

    @JsonProperty("voteCast")
    private String voteCast;

    public VotesEntityDto() {
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

    public ClubsEntity getClub() {
        return club;
    }

    public void setClub(ClubsEntity club) {
        this.club = club;
    }

    public UserEntity getVoter() {
        return voter;
    }

    public void setVoter(UserEntity voter) {
        this.voter = voter;
    }

    public Long getVoteType() {
        return voteType;
    }

    public void setVoteType(Long voteType) {
        this.voteType = voteType;
    }

    public String getVoteCast() {
        return voteCast;
    }

    public void setVoteCast(String voteCast) {
        this.voteCast = voteCast;
    }
}