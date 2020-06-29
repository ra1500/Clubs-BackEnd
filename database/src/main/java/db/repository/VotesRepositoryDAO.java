package db.repository;

import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VotesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Repository
public interface VotesRepositoryDAO extends JpaRepository<VotesEntity, Long> {

    VotesEntity findOneById(Long id);

    Set<VotesEntity> findAllByVoterAndClub(UserEntity userEntity, ClubsEntity clubsEntity);
    VotesEntity findOneByVoterAndVoteTypeAndClub(UserEntity userEntity, Long clubType, ClubsEntity clubsEntity);

    @Transactional
    Integer deleteOneById(Long id);
}
