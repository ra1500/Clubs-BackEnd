package db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table
public class VotesEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date created;

    @ManyToOne
    @JoinColumn(name = "clubsEntityId")
    private ClubsEntity club;

    @ManyToOne
    @JoinColumn(name = "userEntityId")
    private UserEntity voter;

    @Column(length = 3)
    private Long voteType; //1-Alpha,

    @Column
    private String voteCast;

    public VotesEntity() {
        super();
    }

    public VotesEntity(Long id, Date created, ClubsEntity club, UserEntity voter, Long voteType, String voteCast) {
        this.id = id;
        this.created = created;
        this.club = club;
        this.voter = voter;
        this.voteType = voteType;
        this.voteCast = voteCast;
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