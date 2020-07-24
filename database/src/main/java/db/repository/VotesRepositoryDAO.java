package db.repository;

import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VotesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Set;

@Repository
public interface VotesRepositoryDAO extends JpaRepository<VotesEntity, Long> {

    VotesEntity findOneById(Long id);

    Set<VotesEntity> findAllByVoterAndClub(UserEntity userEntity, ClubsEntity clubsEntity);
    VotesEntity findOneByVoterAndVoteTypeAndClub(UserEntity userEntity, Long clubType, ClubsEntity clubsEntity);

    //@Query("SELECT a FROM UserAnswersEntity a JOIN FETCH a.questionsEntity b WHERE a.userName = :userName AND b.sequenceNumber = 1")

    @Query("SELECT DISTINCT v.voteCast, COUNT(v.voteCast) FROM VotesEntity v JOIN v.club c WHERE c.id = :clubId AND v.voteType = 1")
    HashMap getAlphaVoteCounts(@Param("clubId") Long clubId);
    //Set<VotesEntity> getAlphaVoteCounts(@Param("clubId") Long clubId);

    @Transactional
    Integer deleteOneById(Long id);
}
