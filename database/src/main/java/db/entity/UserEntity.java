package db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@DynamicUpdate
@Table(uniqueConstraints= @UniqueConstraint(columnNames={"userName"}))
@Entity
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date created;

    @Column (unique=true, nullable = false, length = 100)
    private String userName;

    @Column (nullable = false, length = 100)
    private String password;

    @Column (length = 20)
    private String publicProfile; // not boolean. permission for viewing user's public profile page on internet.

    @Column
    private String creator;

    @Column (length = 100)
    private String title;

    @Lob
    @Column
    private String blurb;

    @Column
    private Long education;

    @Column (length = 100)
    private String occupation;

    @Column
    private Long relationshipStatus;

    @Column (length = 100)
    private String location;

    @Column (length = 100)
    private String contactInfo;

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity", fetch = FetchType.EAGER)
    private Set<FriendshipsEntity> friendsSet = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "ClubsEntityId")
    @Column
    private Set<ClubsEntity> clubs;


    public UserEntity() {
        super();
    }

    public UserEntity (String userName, String password, String publicProfile) {
        super();
        this.userName = userName;
        this.password = password;
        this.publicProfile = publicProfile;
    }

    public UserEntity (String userName, String password, String publicProfile, String creator) {
        super();
        this.userName = userName;
        this.password = password;
        this.publicProfile = publicProfile;
        this.creator = creator;
    }

    public UserEntity (String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }

    public UserEntity (String userName) {
        super();
        this.userName = userName;
    }

    public Long getId() { return id; }

    public Date getCreated() { return created; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(String publicProfile) {
        this.publicProfile = publicProfile;
    }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

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

    public Set<FriendshipsEntity> getFriendsSet() { return friendsSet; }

    public void setFriendsSet(Set<FriendshipsEntity> friendsSet) {
        this.friendsSet = friendsSet;
    }

    public Set<ClubsEntity> getClubs() {
        return clubs;
    }

    public void setClubs(Set<ClubsEntity> clubs) {
        this.clubs = clubs;
    }

    @Override
    public String toString() {
        return String.format("User Profile", id, created, userName, password, publicProfile);
    }

}