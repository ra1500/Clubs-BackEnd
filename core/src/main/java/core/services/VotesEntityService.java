package core.services;

import core.transformers.VotesEntityDtoTransformer;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VoteCountsClubAlpha;
import db.entity.VotesEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import db.repository.VotesRepositoryDAO;
import model.VotesEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class VotesEntityService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ClubsRepositoryDAO clubsRepositoryDAO;
    private final VotesRepositoryDAO votesRepositoryDAO;
    private final VotesEntityDtoTransformer votesEntityDtoTransformer;
    private final UserRepositoryDAO userRepositoryDAO;

    public VotesEntityService(final VotesRepositoryDAO votesRepositoryDAO ,final ClubsRepositoryDAO clubsRepositoryDAO,
                              final VotesEntityDtoTransformer votesEntityDtoTransformer, final UserRepositoryDAO userRepositoryDAO) {
        this.votesRepositoryDAO = votesRepositoryDAO;
        this.clubsRepositoryDAO = clubsRepositoryDAO;
        this.votesEntityDtoTransformer = votesEntityDtoTransformer;
        this.userRepositoryDAO = userRepositoryDAO;
    }

    // GET
    public VotesEntityDto getVotesEntity(final Long votesEntityId) {
        return votesEntityDtoTransformer.generate(votesRepositoryDAO.findOneById(votesEntityId));
    }

    // POST a alpha vote vote
    public VotesEntityDto createVotesEntity(final VotesEntityDto votesEntityDto, String userName, Long clubId) {

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubId);

        // validation. user is indeed in club.
        if ( !foundClubsEntity.getMembers().contains(foundUserEntity) )  { votesEntityDto.setVoteCast("error"); return votesEntityDto;};

        // validation. does proposed alpha user exist?
        UserEntity foundAlphaUserEntity = userRepositoryDAO.findOneByUserName(votesEntityDto.getVoteCast());
        if ( foundAlphaUserEntity == null ) { votesEntityDto.setVoteCast("error. alpha user not found"); return votesEntityDto;};

        // validation. is proposed alpha in club?
        if ( !foundClubsEntity.getMembers().contains(foundAlphaUserEntity) ) { votesEntityDto.setVoteCast("error. alpha not in club"); return votesEntityDto;};

        // check if vote already exists. if it does, just update it.
        VotesEntity foundVotesEntity = votesRepositoryDAO.findOneByVoterAndVoteTypeAndClub(foundUserEntity, new Long(1), foundClubsEntity);
        if (foundVotesEntity != null) {
            foundVotesEntity.setVoteCast(votesEntityDto.getVoteCast());

            // save the vote before it is counted in the coup d'etat below
            votesRepositoryDAO.save(foundVotesEntity);

            // COUP D'ETAT - update alpha if vote changes majority of alpha votes
            String[] voteCountsClubAlphas = votesRepositoryDAO.getAlphaVoteCounts(foundClubsEntity.getId());
            Set<VoteCountsClubAlpha> setOfVotes = new HashSet<>();

            for (String x : voteCountsClubAlphas) {
                String[] y = x.split(",");
                setOfVotes.add(new VoteCountsClubAlpha( y[0], new Integer(y[1]) ));
            }

            int maxCount = 0;
            int currentCount;
            String alpha = "";
            for (VoteCountsClubAlpha x : setOfVotes ) {
                currentCount = x.getCountVotesCast();
                if (currentCount > maxCount) {
                    maxCount = currentCount;
                    alpha = x.getVoteCast();
                }
            }
            if ( !foundClubsEntity.getAlpha().equals(alpha) ) {
                foundClubsEntity.setAlpha(alpha);
                clubsRepositoryDAO.save(foundClubsEntity);
            };

            return votesEntityDtoTransformer.generate(foundVotesEntity);
        }
        else {
        // add the voter UserEntity
        votesEntityDto.setVoter(foundUserEntity);

        // add the club to the vote (based on clubId)
        votesEntityDto.setClub(foundClubsEntity);

        VotesEntity savedVotesEntity = votesRepositoryDAO.saveAndFlush(votesEntityDtoTransformer.generate(votesEntityDto));

        // add the vote to the club's set of votes
        // note this does not invoke 'coup d'etat' since the default vote is the current alpha.
        foundClubsEntity.getVotes().add(savedVotesEntity);

        return votesEntityDtoTransformer.generate(savedVotesEntity);
        }
    }
}
