package core.services;

import core.transformers.ClubsEntityDtoTransformer;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VoteCountsClubAlpha;
import db.entity.VotesEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.MessagesRepositoryDAO;
import db.repository.UserRepositoryDAO;
import db.repository.VotesRepositoryDAO;
import model.ClubsEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    private final MessagesRepositoryDAO messagesRepositoryDAO;

    public ClubsEntityService(final ClubsRepositoryDAO clubsRepositoryDAO,
                                  final ClubsEntityDtoTransformer clubsEntityDtoTransformer, final UserRepositoryDAO userRepositoryDAO, VotesRepositoryDAO votesRepositoryDAO, MessagesRepositoryDAO messagesRepositoryDAO) {
        this.clubsRepositoryDAO = clubsRepositoryDAO;
        this.clubsEntityDtoTransformer = clubsEntityDtoTransformer;
        this.userRepositoryDAO = userRepositoryDAO;
        this.votesRepositoryDAO = votesRepositoryDAO;
        this.messagesRepositoryDAO = messagesRepositoryDAO;
    }

    // GET
    public ClubsEntityDto getClubsEntity(final Long clubsEntityId, String user) {

        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubsEntityId);
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);

        // validation. ensure user is in club before providing details and members list.
        if ( !foundClubsEntity.getMembers().contains(foundUserEntity) ) { return new ClubsEntityDto(); };

        Set<UserEntity> members = foundClubsEntity.getMembers();
        for (UserEntity u : members) {
            u.setPassword(null);
            u.setCreated(null); u.setLocation(null); u.setContactInfo(null);
        }
        foundClubsEntity.setMembers(members);

        return clubsEntityDtoTransformer.generate(foundClubsEntity);
    }

    // POST a new Club
    public ClubsEntityDto createClubsEntity(final ClubsEntityDto clubsEntityDto, final String userName) {

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        clubsEntityDto.setAlpha(userName);  // doesnt make sense to have it be anyone else than the creator.

        // first, check that user is under max.# of clubs can join
        Integer countOfClubsJoined = foundUserEntity.getClubs().size();
         if ( countOfClubsJoined > 30 ) {
             clubsEntityDto.setFounder("OVER LIMIT");
             return clubsEntityDto; }

        // a new club includes a set of members. Add it, with the user, to the Dto.
        Set<UserEntity> newMembersSet = new HashSet<>();

        newMembersSet.add(foundUserEntity);
        clubsEntityDto.setMembers(newMembersSet);
        clubsEntityDto.setFounder(userName);
        clubsEntityDto.setCurrentSize(new Long(1));

        if (clubsEntityDto.getMaxSize().equals(null)) { clubsEntityDto.setMaxSize(new Long(20)); };
        try {
            clubsEntityDto.setMaxSize(clubsEntityDto.getMaxSize());
            if (clubsEntityDto.getMaxSize() > 500) { clubsEntityDto.setMaxSize(new Long(500)); };
            if (clubsEntityDto.getMaxSize() < 1) { clubsEntityDto.setMaxSize(new Long(1)); };
        }
        catch (NumberFormatException nfe) { clubsEntityDto.setMaxSize(new Long(20)); };

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
    public ClubsEntityDto updateClubsEntity(final ClubsEntityDto clubsEntityDto, final String user) {

        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubsEntityDto.getId());

        // validation. user is alpha and can therefore update/edit the club.
        if ( !foundClubsEntity.getAlpha().equals(user) ) { clubsEntityDto.setAlpha("error. user is not alpha"); return clubsEntityDto; }

        foundClubsEntity.setClubName(clubsEntityDto.getClubName());
        foundClubsEntity.setDescription(clubsEntityDto.getDescription());
        if (clubsEntityDto.getMaxSize().equals(null)) { foundClubsEntity.setMaxSize(foundClubsEntity.getMaxSize()); };
        if (foundClubsEntity.getMaxSize().equals(null)) { foundClubsEntity.setMaxSize(new Long(20)); };

        try {
        foundClubsEntity.setMaxSize(clubsEntityDto.getMaxSize());
        if (clubsEntityDto.getMaxSize() > 500) { foundClubsEntity.setMaxSize(new Long(500)); };
        if (clubsEntityDto.getMaxSize() < 1) { foundClubsEntity.setMaxSize(new Long(1)); };
        }
        catch (NumberFormatException nfe) { foundClubsEntity.setMaxSize(foundClubsEntity.getMaxSize()); };

        // no change in alpha. should be based on max votes, or oldest create date of membership.

        clubsRepositoryDAO.save(foundClubsEntity);

        foundClubsEntity.setMembers(null);
        foundClubsEntity.setVotes(null); // set to null for now. but might want this later to count alpha votes.

        return clubsEntityDtoTransformer.generate(foundClubsEntity);
    }

    // Quit club
    public String userQuitClub(final String userName, final Long clubId) {

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubId);

        // validation. if club not found, break out.
        if ( foundClubsEntity == null ) { return "Club not found"; };

        // validation. ensure user is indeed in club.
        if ( !foundClubsEntity.getMembers().contains(foundUserEntity) ) { return "error. user is not in club"; };

        // delete all votes by user in club
        votesRepositoryDAO.deleteAllByVoterAndClub(foundUserEntity, foundClubsEntity);

        // remove user from the club
        Set<UserEntity> foundUserEntitySet = foundClubsEntity.getMembers();
        foundUserEntitySet.removeIf(i -> i.getUserName().equals(userName));
        foundClubsEntity.setMembers(foundUserEntitySet);

        // remove the club from the user
        Set<ClubsEntity> foundUserClubSet = foundUserEntity.getClubs();
        foundUserClubSet.removeIf(i -> i.getId().equals(clubId));
        foundUserEntity.setClubs(foundUserClubSet);

        // update the club's currentSize
        foundClubsEntity.setCurrentSize(new Long(foundClubsEntity.getMembers().size()));

        // delete the club if no more members remain
        if ( foundClubsEntity.getCurrentSize() < 1 ) {
            clubsRepositoryDAO.deleteOneById(clubId);
            userRepositoryDAO.save(foundUserEntity);
            messagesRepositoryDAO.deleteAllByClubName(foundClubsEntity.getClubName());
            // TODO: delete all msgs between club members.
            return "Club removed.";
        }

        else {

        // update alpha.

        // if only one member now remains, assign alpha to that person.
        if ( foundClubsEntity.getMembers().size() == 1 ) {
            for (UserEntity x : foundClubsEntity.getMembers()  ) {
                foundClubsEntity.setAlpha(x.getUserName());
            };
        };

         // if quitting member is aplpha. switch to beta.
        if ( foundClubsEntity.getMembers().size() > 1 ) {
            String[] voteCountsClubAlphas = votesRepositoryDAO.getAlphaVoteCounts(clubId);
            Set<VoteCountsClubAlpha> setOfVotes = new HashSet<>();

            for (String x : voteCountsClubAlphas) {
                String[] y = x.split(",");
                setOfVotes.add(new VoteCountsClubAlpha( y[0], new Integer(y[1]) ));
            }

            // remove the alpha before getting the beta.
            setOfVotes.removeIf(i -> i.getVoteCast().equals(foundClubsEntity.getAlpha()));

            // get the beta. note if there is a tie in beta counts, this will just grab one from 'random' order in the Set.
            // maxCount must be > than 1 in order overcome the alpha
            int maxCount = 0;
            int currentCount;
            String beta = "";
            for (VoteCountsClubAlpha x : setOfVotes ) {
                currentCount = x.getCountVotesCast();
                if (currentCount > maxCount) {
                    maxCount = currentCount;
                    beta = x.getVoteCast();
                }
            }

            foundClubsEntity.setAlpha(beta);
        }; // end if

        userRepositoryDAO.save(foundUserEntity);
        clubsRepositoryDAO.save(foundClubsEntity);

        String userRemoved = "Club removed.";
        return userRemoved;

        } // end else
    }

}
