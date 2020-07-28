package db.repository;

import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VoteCountsClubAlpha;
import db.entity.VotesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Set;

@Repository
public interface VotesRepositoryDAO extends JpaRepository<VotesEntity, Long> {

    VotesEntity findOneById(Long id);

    Set<VotesEntity> findAllByVoterAndClub(UserEntity userEntity, ClubsEntity clubsEntity);
    VotesEntity findOneByVoterAndVoteTypeAndClub(UserEntity userEntity, Long clubType, ClubsEntity clubsEntity);

    // TODO this can be achieved with a JOIN instead.
    @Query("SELECT DISTINCT v.voteCast, COUNT(v.voteCast) FROM VotesEntity v JOIN v.club c WHERE c.id = :clubId AND v.voteType = 1 GROUP BY v.voteCast")
    String[] getAlphaVoteCounts(@Param("clubId") Long clubId);

    @Transactional
    Integer deleteOneById(Long id);

    @Transactional
    Integer deleteAllByVoter(Long id);
}
