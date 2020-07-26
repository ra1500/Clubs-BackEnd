package db.repository;

import db.entity.MessagesEntity;
import db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Set;

@Repository
public interface MessagesRepositoryDAO extends JpaRepository<MessagesEntity, Long> {

    MessagesEntity findOneById(Long id);

    @Query("SELECT m FROM MessagesEntity m WHERE m.receiverId = :receiverId AND m.receiverType = 2")
    Set<MessagesEntity> getClubMessages(@Param("receiverId") Long receiverId);

    //@Query("SELECT m FROM MessagesEntity m WHERE m.sender = sender AND m.receiverId = :receiverId AND m.receiverType = 1")
    //Set<MessagesEntity> getTwoUsersMessages(@Param("sender") UserEntity sender, @Param("receiverId") Long receiverId);

    Set<MessagesEntity> findAllBySenderAndReceiverIdAndReceiverType(UserEntity sender, Long receiverId, Long receiverType);

    @Query("SELECT m FROM MessagesEntity m WHERE m.sender = :sender AND m.receiverId = :receiverId AND m.created > :yesterday ")
    Set<MessagesEntity> getAllWithinOneDay(UserEntity sender, Long receiverId, Date yesterday);

    Set<MessagesEntity> findAllBySenderAndReceiverIdAndReceiverTypeAndClubName(UserEntity sender, Long receiverId, Long receiverType, String clubName);

    Set<MessagesEntity> findAllByReceiverIdAndReceiverTypeAndRedFlag(Long receiverId, Long receiverType, Long redFlag);

    @Query("SELECT DISTINCT m.clubName, m.sender.userName FROM MessagesEntity m WHERE m.receiverId = :receiverId AND m.redFlag = 0 AND m.receiverType = 1")
    Set<MessagesEntity> getAlertsNewCLubMessages(@Param("receiverId") Long receiverId);

    @Query("SELECT DISTINCT m.clubName, m.sender.userName FROM MessagesEntity m WHERE m.receiverId = :receiverId AND m.redFlag = 0 AND m.receiverType = 5")
    Set<MessagesEntity> getAlertsNewGuildMessages(@Param("receiverId") Long receiverId);

    @Transactional
    Integer deleteOneById(Long id);
}
