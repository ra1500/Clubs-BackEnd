package core.services;

import core.transformers.VotesEntityDtoTransformer;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VotesEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import db.repository.VotesRepositoryDAO;
import model.VotesEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

        // TODO validate that user is indeed in this club.
        // TODO validate that proposed alpha exists AND is in the club.

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubId);

        // check if vote already exists. if it does, just update it.
        VotesEntity foundVotesEntity = votesRepositoryDAO.findOneByVoterAndVoteTypeAndClub(foundUserEntity, new Long(1), foundClubsEntity);
        if (foundVotesEntity != null) {
            foundVotesEntity.setVoteCast(votesEntityDto.getVoteCast());
            votesRepositoryDAO.save(foundVotesEntity);
            return votesEntityDtoTransformer.generate(foundVotesEntity);
        }
        else {
        // add the voter UserEntity
        votesEntityDto.setVoter(foundUserEntity);

        // add the club to the vote (based on clubId)
        votesEntityDto.setClub(foundClubsEntity);

        VotesEntity savedVotesEntity = votesRepositoryDAO.saveAndFlush(votesEntityDtoTransformer.generate(votesEntityDto));

        // add the vote to the club's set of votes
        foundClubsEntity.getVotes().add(savedVotesEntity);

        return votesEntityDtoTransformer.generate(savedVotesEntity);
        }
    }
}
