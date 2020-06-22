package db.repository;

import db.entity.MessagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Repository
public interface MessagesRepositoryDAO extends JpaRepository<MessagesEntity, Long> {

    MessagesEntity findOneById(Long id);

    @Query("SELECT m FROM MessagesEntity m WHERE m.receiverId = :receiverId AND m.receiverType = 2")
    Set<MessagesEntity> getClubMessages(@Param("receiverId") Long receiverId);

    @Transactional
    Integer deleteOneById(Long id);
}
