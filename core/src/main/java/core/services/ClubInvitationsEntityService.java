package core.services;

import core.transformers.ClubInvitationsEntityDtoTransformer;
import db.entity.ClubInvitationsEntity;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VotesEntity;
import db.repository.ClubInvitationsRepositoryDAO;
import db.repository.ClubsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import db.repository.VotesRepositoryDAO;
import model.ClubInvitationsEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ClubInvitationsEntityService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ClubsRepositoryDAO clubsRepositoryDAO;
    private final ClubInvitationsRepositoryDAO clubInvitationsRepositoryDAO;
    private final ClubInvitationsEntityDtoTransformer clubInvitationsEntityDtoTransformer;
    private final UserRepositoryDAO userRepositoryDAO;
    private final VotesRepositoryDAO votesRepositoryDAO;

    public ClubInvitationsEntityService(final ClubInvitationsRepositoryDAO clubInvitationsRepositoryDAO,
                                        final ClubInvitationsEntityDtoTransformer clubInvitationsEntityDtoTransformer, final UserRepositoryDAO userRepositoryDAO, ClubsRepositoryDAO clubsRepositoryDAO, VotesRepositoryDAO votesRepositoryDAO) {
        this.clubInvitationsRepositoryDAO = clubInvitationsRepositoryDAO;
        this.clubInvitationsEntityDtoTransformer = clubInvitationsEntityDtoTransformer;
        this.userRepositoryDAO = userRepositoryDAO;
        this.clubsRepositoryDAO = clubsRepositoryDAO;
        this.votesRepositoryDAO = votesRepositoryDAO;
    }

    // GET
    public ClubInvitationsEntityDto getClubInvitationsEntity(final Long clubInvitationsEntityId, String user)  {

        // validation. invitation does indeed belong to this user
        ClubInvitationsEntity foundClubInvitationsEntity = clubInvitationsRepositoryDAO.findOneById(clubInvitationsEntityId);
        if ( !(foundClubInvitationsEntity.getReceiver().equals(user)) ) { return new ClubInvitationsEntityDto(); }

        return clubInvitationsEntityDtoTransformer.generate(foundClubInvitationsEntity);
    }

    // GET new club invitations set
    public Set<ClubInvitationsEntity> getNewClubInvitations(final String user) {
        Set<ClubInvitationsEntity> foundNewClubInvitations = clubInvitationsRepositoryDAO.findAllByReceiverAndStatus(user, new Long(1));

        for (ClubInvitationsEntity x : foundNewClubInvitations ) {
            x.getSender().setPassword(null); x.getSender().setCreated(null); x.getSender().setClubs(null);
            x.getSender().setFriendsSet(null); x.getSender().setContactInfo(null);
        }

        return foundNewClubInvitations;
    }

    // POST a new club invitation
    public ClubInvitationsEntityDto createClubInvitationsEntity(final ClubInvitationsEntityDto clubInvitationsEntityDto, final String user, final Long clubId) {

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);

        // check db if receiver exists
        UserEntity receiverUserEntity = userRepositoryDAO.findOneByUserName(clubInvitationsEntityDto.getReceiver());
        if ( receiverUserEntity == null ) { clubInvitationsEntityDto.setReceiver("error. user not found"); return clubInvitationsEntityDto; };

        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubId);

        // validation. ensure not duplicate invitation (status could be 'declined' or 'accepted'. so no dups in db not valid check).
        if ( clubInvitationsRepositoryDAO.findOneBySenderAndReceiverAndClub(foundUserEntity, receiverUserEntity.getUserName(), foundClubsEntity) != null ) { clubInvitationsEntityDto.setReceiver("You already invited this user before!"); return clubInvitationsEntityDto; };

        // check if club already full or not.
        Long maxSize = foundClubsEntity.getMaxSize();
        Set<UserEntity> members = foundClubsEntity.getMembers();
        Integer membersCount = members.size();
        if ( membersCount >= maxSize ) {
            clubInvitationsEntityDto.setStatus(new Long(5)); // status 5 = club is full.
            return clubInvitationsEntityDto; };

        // validation. is user indeed in club
        if ( !foundClubsEntity.getMembers().contains(foundUserEntity) ) { clubInvitationsEntityDto.setReceiver("error. user not in club"); return clubInvitationsEntityDto; };

        // validation. no invitation to self
        if ( clubInvitationsEntityDto.getReceiver().equals(user) ) { clubInvitationsEntityDto.setReceiver("But you're already in the club..."); return clubInvitationsEntityDto; };

        // validation. is receiver already in club?
        if ( foundClubsEntity.getMembers().contains(receiverUserEntity) ) { clubInvitationsEntityDto.setReceiver("They are already in the club!"); return clubInvitationsEntityDto; };

        // validation. not a duplicate invitation (sender, receiver, clubId)
        //TODO:
        // validation. currently, more than one invitation for same club can be produced if from different sender)

        // add the sender's UserEntity
        UserEntity senderUserEntity = userRepositoryDAO.findOneByUserName(user);
        clubInvitationsEntityDto.setSender(senderUserEntity);

        // add the club
        clubInvitationsEntityDto.setClub(foundClubsEntity);

        // add status of pending
        clubInvitationsEntityDto.setStatus(new Long(1));

        ClubInvitationsEntity newClubInvitationsEntity = (clubInvitationsEntityDtoTransformer.generate(clubInvitationsEntityDto));
        clubInvitationsRepositoryDAO.saveAndFlush(newClubInvitationsEntity);

        return clubInvitationsEntityDtoTransformer.generate(newClubInvitationsEntity);
    }

    // POST an updated invitation (accepted or decline).
    public ClubInvitationsEntityDto updateClubInvitationsEntity(final ClubInvitationsEntityDto clubInvitationsEntityDto, final String userName) {

        // get the invitation
        ClubInvitationsEntity foundClubInvitationEntity = clubInvitationsRepositoryDAO.findOneById(clubInvitationsEntityDto.getId());

        // validate that user and receiver are the same
        if (!foundClubInvitationEntity.getReceiver().equals(userName)) { return clubInvitationsEntityDto; };

        // update the invitation status
        foundClubInvitationEntity.setStatus(clubInvitationsEntityDto.getStatus());

        // add the user to the club if accepted. save it. also add club to the user
        if (clubInvitationsEntityDto.getStatus().equals(new Long(2))) {

            UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);

            // validation. is user already in club? (if so, then just break and return the foundClubInvitationEntity).
            ClubsEntity foundClubsEntity = foundClubInvitationEntity.getClub();
            if ( foundClubsEntity.getMembers().contains(foundUserEntity) ) { return clubInvitationsEntityDto; };

            // check that user is under max.# of clubs can join
            Integer countOfClubsJoined = foundUserEntity.getClubs().size();
            if ( countOfClubsJoined > 30 ) {
                clubInvitationsEntityDto.setReceiver("OVER LIMIT");
                return clubInvitationsEntityDto; }

            //ClubsEntity foundClubsEntity = foundClubInvitationEntity.getClub();
            Long maxSize = foundClubsEntity.getMaxSize();
            Set<UserEntity> members = foundClubsEntity.getMembers();
            Integer membersCount = members.size();

            // check if club full or not (under max #users). break if true.
            if ( membersCount >= maxSize ) { return clubInvitationsEntityDto; };

            members.add(foundUserEntity);
            foundClubsEntity.setMembers(members);

            // create the new 'alpha' vote. save it.
            VotesEntity newAlphaVote = new VotesEntity();
            newAlphaVote.setVoter(foundUserEntity);
            newAlphaVote.setClub(foundClubsEntity);
            newAlphaVote.setVoteType(new Long(1));
            newAlphaVote.setVoteCast(foundClubsEntity.getAlpha());
            votesRepositoryDAO.saveAndFlush(newAlphaVote);

            foundUserEntity.getClubs().add(foundClubsEntity);
            clubsRepositoryDAO.save(foundClubsEntity);
            userRepositoryDAO.save(foundUserEntity);
        }

        //decline an invitation
        else {
            foundClubInvitationEntity.setStatus(clubInvitationsEntityDto.getStatus());
        }

        // save the updated club invitation
        clubInvitationsRepositoryDAO.save(foundClubInvitationEntity);

        // clean up the DTO by deleting excess info.
        foundClubInvitationEntity.setSender(null);
        foundClubInvitationEntity.setClub(null);

        return clubInvitationsEntityDtoTransformer.generate(foundClubInvitationEntity);
    }

}