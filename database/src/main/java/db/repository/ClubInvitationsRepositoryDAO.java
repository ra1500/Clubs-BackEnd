package db.repository;

import db.entity.ClubInvitationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Repository
public interface ClubInvitationsRepositoryDAO extends JpaRepository<ClubInvitationsEntity, Long> {

    ClubInvitationsEntity findOneById(Long id);

    Set<ClubInvitationsEntity> findAllByReceiverAndStatus(String receiver, Long status);

    ClubInvitationsEntity findOneBySenderAndReceiverAndClub(Long senderId, String receiver, Long clubId );

    @Transactional
    Integer deleteOneById(Long id);
}
