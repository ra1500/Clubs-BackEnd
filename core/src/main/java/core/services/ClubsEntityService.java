package core.services;

import core.transformers.ClubsEntityDtoTransformer;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VotesEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import db.repository.VotesRepositoryDAO;
import model.ClubsEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ClubsEntityService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ClubsRepositoryDAO clubsRepositoryDAO;
    private final ClubsEntityDtoTransformer clubsEntityDtoTransformer;
    private final UserRepositoryDAO userRepositoryDAO;
    private final VotesRepositoryDAO votesRepositoryDAO;

    public ClubsEntityService(final ClubsRepositoryDAO clubsRepositoryDAO,
                                  final ClubsEntityDtoTransformer clubsEntityDtoTransformer, final UserRepositoryDAO userRepositoryDAO, VotesRepositoryDAO votesRepositoryDAO) {
        this.clubsRepositoryDAO = clubsRepositoryDAO;
        this.clubsEntityDtoTransformer = clubsEntityDtoTransformer;
        this.userRepositoryDAO = userRepositoryDAO;
        this.votesRepositoryDAO = votesRepositoryDAO;
    }

    // GET
    public ClubsEntityDto getClubsEntity(final Long clubsEntityId) {

        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubsEntityId);
        Set<UserEntity> members = foundClubsEntity.getMembers();
        for (UserEntity u : members) {
            u.setPassword(null);
            u.setCreated(null);
        }
        foundClubsEntity.setMembers(members);

        return clubsEntityDtoTransformer.generate(foundClubsEntity);
    }

    // POST a new Club
    public ClubsEntityDto createClubsEntity(final ClubsEntityDto clubsEntityDto, final String userName) {

        // a new club includes a set of members. Add it, with the user, to the Dto.
        Set<UserEntity> newMembersSet = new HashSet<>();
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        newMembersSet.add(foundUserEntity);
        clubsEntityDto.setMembers(newMembersSet);
        ClubsEntity newClubsEntity = (clubsEntityDtoTransformer.generate(clubsEntityDto));
        ClubsEntity savedNewClubsEntity = clubsRepositoryDAO.saveAndFlush(newClubsEntity);

        // create the new 'alpha' vote. save it.
        VotesEntity newAlphaVote = new VotesEntity();
        newAlphaVote.setVoter(foundUserEntity);
        newAlphaVote.setClub(savedNewClubsEntity);
        newAlphaVote.setVoteType(new Long(1));
        newAlphaVote.setVoteCast(userName);
        VotesEntity savedNewVotesEntity = votesRepositoryDAO.saveAndFlush(newAlphaVote);

        // add the new club to the user's set of club memberships. save it.
        // TODO is this redundant?
        foundUserEntity.getClubs().add(savedNewClubsEntity);
        userRepositoryDAO.saveAndFlush(foundUserEntity);

        return clubsEntityDtoTransformer.generate(savedNewClubsEntity);
    }

    // POST edit/update a club (alpha can update).
    public ClubsEntityDto updateClubsEntity(final ClubsEntityDto clubsEntityDto, final String userName) {

        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubsEntityDto.getId());
        foundClubsEntity.setClubName(clubsEntityDto.getClubName());
        foundClubsEntity.setMaxSize(clubsEntityDto.getMaxSize());
        foundClubsEntity.setDescription(clubsEntityDto.getDescription());

        // no change in alpha. should be based on max votes, or oldest create date of membership.

        clubsRepositoryDAO.save(foundClubsEntity);

        return clubsEntityDtoTransformer.generate(foundClubsEntity);
    }

    // Quit club
    public String userQuitClub(final String userName, final Long clubId) {

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);

        // TODO validate
        // remove user from the club
        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubId);
        Set<UserEntity> foundUserEntitySet = foundClubsEntity.getMembers();
        foundUserEntitySet.removeIf(i -> i.getUserName().equals(userName));
        foundClubsEntity.setMembers(foundUserEntitySet);

        // remove the club from the user
        Set<ClubsEntity> foundUserClubSet = foundUserEntity.getClubs();
        foundUserClubSet.removeIf(i -> i.getId().equals(clubId));
        foundUserEntity.setClubs(foundUserClubSet);

        // remove the user's votes from the club.
        //TODO
        // TODO recalibrate vote counts and results

        userRepositoryDAO.saveAndFlush(foundUserEntity);
        clubsRepositoryDAO.saveAndFlush(foundClubsEntity);

        String userRemoved = "user removed from club";
        return userRemoved;
    }

}
