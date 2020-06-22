package db.repository;

import db.entity.ClubInvitationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ClubInvitationsRepositoryDAO extends JpaRepository<ClubInvitationsEntity, Long> {

    ClubInvitationsEntity findOneById(Long id);

    @Transactional
    Integer deleteOneById(Long id);
}
