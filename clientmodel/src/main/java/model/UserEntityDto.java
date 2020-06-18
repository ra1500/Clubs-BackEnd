package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.entity.ClubsEntity;
import db.entity.FriendshipsEntity;

//import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEntityDto implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("password")
    private String password;

    @JsonProperty("publicProfile")
    private String publicProfile;

    @JsonProperty("title")
    private String title;

    @JsonProperty("blurb")
    private String blurb;

    @JsonProperty("education")
    private Long education;

    @JsonProperty("occupation")
    private String occupation;

    @JsonProperty("relationshipStatus")
    private Long relationshipStatus;

    @JsonProperty("location")
    private String location;

    @JsonProperty("contactInfo")
    private String contactInfo;

    @JsonProperty("friendsList")
    private Set<FriendshipsEntity> friendsSet;

    @JsonProperty("clubsList")
    private Set<ClubsEntity> clubsSet;

    public UserEntityDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(String publicProfile) {
        this.publicProfile = publicProfile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public Long getEducation() {
        return education;
    }

    public void setEducation(Long education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Long getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(Long relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Set<FriendshipsEntity> getFriendsSet() {
        return friendsSet;
    }

    public void setFriendsSet(Set<FriendshipsEntity> friendsSet) {
        this.friendsSet = friendsSet;
    }

    public Set<ClubsEntity> getClubsSet() {
        return clubsSet;
    }

    public void setClubsSet(Set<ClubsEntity> clubsSet) {
        this.clubsSet = clubsSet;
    }
}
