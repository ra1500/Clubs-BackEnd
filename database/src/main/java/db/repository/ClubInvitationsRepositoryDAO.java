package db.repository;

import db.entity.ClubInvitationsEntity;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Repository
public interface ClubInvitationsRepositoryDAO extends JpaRepository<ClubInvitationsEntity, Long> {

    ClubInvitationsEntity findOneById(Long id);

    Set<ClubInvitationsEntity> findAllByReceiverAndStatus(String receiver, Long status);

    ClubInvitationsEntity findOneBySenderAndReceiverAndClub(UserEntity sender, String receiver, ClubsEntity clubsEntity );
    ClubInvitationsEntity findOneByReceiverAndClub(String receiver, ClubsEntity clubsEntity );

    @Transactional
    Integer deleteOneById(Long id);
}
