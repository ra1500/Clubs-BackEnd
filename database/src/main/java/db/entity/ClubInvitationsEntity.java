package db.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table
public class ClubInvitationsEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @ManyToOne
    @JoinColumn(name = "userEntityId")
    private UserEntity sender;

    @Column
    private String receiver;

    @Column
    private Long status; // pending, accepted, declined, blocked

    @ManyToOne
    @JoinColumn(name = "clubsEntityId")
    private ClubsEntity club;

    public ClubInvitationsEntity() {
        super();
    }

    public ClubInvitationsEntity(Long id, Date created, UserEntity sender, String receiver, Long status, ClubsEntity club) {
        this.id = id;
        this.created = created;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.club = club;
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