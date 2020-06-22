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
    private Long receiverType;    // 1=User 2=Club 3=Guild

    @Column
    private Long receiverId;

    public MessagesEntity() { super(); }

    public MessagesEntity(Long id, Date created, String message, UserEntity sender, Long receiverType, Long receiverId) {
        this.id = id;
        this.created = created;
        this.message = message;
        this.sender = sender;
        this.receiverType = receiverType;
        this.receiverId = receiverId;
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

    @Override
    public String toString() {
        return String.format("messages profile", id, created, message, receiverType, receiverId);
    }
}