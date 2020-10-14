package db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.apache.catalina.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@DynamicUpdate // needed for patch?
@Table(uniqueConstraints= @UniqueConstraint(columnNames={"userEntityId", "friend"}))
@Entity
public class FriendshipsEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date created;

    @Column(length = 100)
    private String inviter; // the user who initiated the invitation to be a friend

    @Column(length = 100) // the user's friend
    private String friend;

    @Column(length = 20)
    private String connectionStatus;

    @Column(length = 20)
    private String connectionType;

    @Column(length = 20)
    private String visibilityPermission; // not currently used. this should only be for a specific permission between two users.
                                        // for general permissions of what can be seen of a user's profile, that should be held at the UserEntity (under publicProfile).

    @ManyToOne
    @JoinColumn(name = "userEntityId")
    private UserEntity userEntity;

    public FriendshipsEntity() {
        super();
    }

    public FriendshipsEntity(String inviter, String friend, String connectionStatus, String connectionType, String visibilityPermission, UserEntity userEntity) {
        this.inviter = inviter;
        this.friend = friend;
        this.connectionStatus = connectionStatus;
        this.connectionType = connectionType;
        this.visibilityPermission = visibilityPermission;
        this.userEntity = userEntity;
    }

    public FriendshipsEntity(String inviter, String friend, String connectionStatus, String connectionType, String visibilityPermission) {
        this.inviter = inviter;
        this.friend = friend;
        this.connectionStatus = connectionStatus;
        this.connectionType = connectionType;
        this.visibilityPermission = visibilityPermission;
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

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getVisibilityPermission() {
        return visibilityPermission;
    }

    public void setVisibilityPermission(String visibilityPermission) {
        this.visibilityPermission = visibilityPermission;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public String toString() {
        return String.format("friendships profile", id, created, inviter, friend, connectionStatus,
                connectionType, visibilityPermission);
    }

}
