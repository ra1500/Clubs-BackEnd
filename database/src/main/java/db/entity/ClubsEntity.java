package db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
public class ClubsEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date created;

    @Column (length = 100)
    private String clubName;

    @Column
    private String founder;

    @Column (length = 100)
    private String description;

    @Column(length=3)
    private Long maxSize;

    @Column(length=3)
    private Long currentSize = new Long(0);

    @Column
    private String alpha; // leader of club. can edit club and remove members

    @Column
    private String headline1;

    @Column
    private String headline2;

    @Column
    private String headline3;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserEntityId")
    @Column
    private Set<UserEntity> members = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private Set<VotesEntity> votes = new HashSet<>();

    public ClubsEntity() {
        super();
    }

    public ClubsEntity(Long id, Date created, String clubName, String founder, String description, Long maxSize, Long currentSize, String alpha) {
        this.id = id;
        this.created = created;
        this.clubName = clubName;
        this.founder = founder;
        this.description = description;
        this.maxSize = maxSize;
        this.currentSize = currentSize;
        this.alpha = alpha;
    }

    public ClubsEntity(Long id, Date created, String clubName, String founder, String description, Long maxSize, Long currentSize, String alpha, Set<UserEntity> members, Set<VotesEntity> votes) {
        this.id = id;
        this.created = created;
        this.clubName = clubName;
        this.founder = founder;
        this.description = description;
        this.maxSize = maxSize;
        this.currentSize = currentSize;
        this.alpha = alpha;
        this.members = members;
        this.votes = votes;
    }

    public ClubsEntity(Long id, Date created, String clubName, String founder, String description, Long maxSize, Long currentSize, String alpha, String headline1, String headline2, String headline3, Set<UserEntity> members, Set<VotesEntity> votes) {
        this.id = id;
        this.created = created;
        this.clubName = clubName;
        this.founder = founder;
        this.description = description;
        this.maxSize = maxSize;
        this.currentSize = currentSize;
        this.alpha = alpha;
        this.headline1 = headline1;
        this.headline2 = headline2;
        this.headline3 = headline3;
        this.members = members;
        this.votes = votes;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

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

    public Set<VotesEntity> getVotes() {
        return votes;
    }

    public void setVotes(Set<VotesEntity> votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return String.format("clubs profile", id, created, founder,clubName);
    }

}