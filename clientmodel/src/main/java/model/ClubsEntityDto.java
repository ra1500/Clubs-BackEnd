package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.entity.UserEntity;
import db.entity.VotesEntity;

//import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubsEntityDto implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("clubName")
    private String clubName;

    @JsonProperty("founder")
    private String founder;

    @JsonProperty("description")
    private String description;

    @JsonProperty("maxSize")
    private Long maxSize;

    @JsonProperty("currentSize")
    private Long currentSize;

    @JsonProperty("alpha")
    private String alpha;

    @JsonProperty("headline1")
    private String headline1;

    @JsonProperty("headline2")
    private String headline2;

    @JsonProperty("headline3")
    private String headline3;

    @JsonProperty("members")
    private Set<UserEntity> members;

    @JsonProperty("votes")
    private Set<VotesEntity> votes;

    @JsonProperty("betaCount")
    private Long betaCount;

    @JsonProperty("betaMember")
    private String betaMember;

    public ClubsEntityDto() {
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

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getFounder() {return founder;}

    public void setFounder(String founder) {this.founder = founder;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }

    public Long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(Long currentSize) {
        this.currentSize = currentSize;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getHeadline1() { return headline1; }

    public void setHeadline1(String headline1) { this.headline1 = headline1; }

    public String getHeadline2() { return headline2; }

    public void setHeadline2(String headline2) { this.headline2 = headline2; }

    public String getHeadline3() { return headline3; }

    public void setHeadline3(String headline3) { this.headline3 = headline3; }

    public Set<UserEntity> getMembers() {
        return members;
    }

    public void setMembers(Set<UserEntity> members) {
        this.members = members;
    }

    public Set<VotesEntity> getVotes() {return votes;}

    public void setVotes(Set<VotesEntity> votes) {
        this.votes = votes;
    }

    public Long getBetaCount() {return betaCount;}

    public void setBetaCount(Long betaCount) {this.betaCount = betaCount;}

    public String getBetaMember() {return betaMember;}

    public void setBetaMember(String betaMember) {this.betaMember = betaMember;}
}
