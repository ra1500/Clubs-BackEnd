package db.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table
public class MessagesEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date created;

    @Column(length = 254)
    private String message;

    @ManyToOne
    @JoinColumn(name = "userEntityId")
    private UserEntity sender;

    @Column(length=3)
    private Long receiverType;    // 1=IndividualUserFromClub 2=Club 3=Guild 4=IndividualUserFromContact 5=IndividualUserFromGuild

    @Column
    private Long receiverId; // userId if from individual. clubId if from club message board.

    @Column
    private String clubName;

    @Column
    private Long redFlag = new Long(0);  // was message read by receiver. 0=no 1=yes.

    public MessagesEntity() { super(); }

    public MessagesEntity(Long id, Date created, String message, UserEntity sender, Long receiverType, Long receiverId, String clubName, Long redFlag) {
        this.id = id;
        this.created = created;
        this.message = message;
        this.sender = sender;
        this.receiverType = receiverType;
        this.receiverId = receiverId;
        this.clubName = clubName;
        this.redFlag = redFlag;
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

    public UserEntity getSender() {
        return sender;
    }

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

    public String getClubName() {return clubName;}

    public void setClubName(String clubName) {this.clubName = clubName;}

    public Long getRedFlag() {
        return redFlag;
    }

    public void setRedFlag(Long redFlag) {
        this.redFlag = redFlag;
    }

    @Override
    public String toString() {
        return String.format("messages profile", id, created, message, receiverType, receiverId);
    }
}