package core.services;

import core.transformers.ClubInvitationsEntityDtoTransformer;
import db.entity.ClubInvitationsEntity;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.repository.ClubInvitationsRepositoryDAO;
import db.repository.ClubsRepositoryDAO;
import db.repository.UserRepositoryDAO;
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

    public ClubInvitationsEntityService(final ClubInvitationsRepositoryDAO clubInvitationsRepositoryDAO,
                                        final ClubInvitationsEntityDtoTransformer clubInvitationsEntityDtoTransformer, final UserRepositoryDAO userRepositoryDAO, ClubsRepositoryDAO clubsRepositoryDAO) {
        this.clubInvitationsRepositoryDAO = clubInvitationsRepositoryDAO;
        this.clubInvitationsEntityDtoTransformer = clubInvitationsEntityDtoTransformer;
        this.userRepositoryDAO = userRepositoryDAO;
        this.clubsRepositoryDAO = clubsRepositoryDAO;
    }

    // GET
    public ClubInvitationsEntityDto getClubInvitationsEntityDto(final Long clubInvitationsEntityId) {
        return clubInvitationsEntityDtoTransformer.generate(clubInvitationsRepositoryDAO.findOneById(clubInvitationsEntityId));
    }

    // POST a new club invitation
    public ClubInvitationsEntityDto createClubInvitationsEntity(final ClubInvitationsEntityDto clubInvitationsEntityDto, final String userName, final Long clubId) {

        // check db if receiver exists
        UserEntity receiverUserEntity = userRepositoryDAO.findOneByUserName(clubInvitationsEntityDto.getReceiver());
        // TODO if receiver not found, return a not found message.
        // TODO check if an invitation had already been sent before.
        // TODO check and make sure user is indeed in club
        // add the sender's UserEntity
        UserEntity senderUserEntity = userRepositoryDAO.findOneByUserName(userName);
        clubInvitationsEntityDto.setSender(senderUserEntity);

        // add the club
        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubId);
        clubInvitationsEntityDto.setClub(foundClubsEntity);

        // add status of pending
        clubInvitationsEntityDto.setStatus(new Long(1));

        ClubInvitationsEntity newClubInvitationsEntity = (clubInvitationsEntityDtoTransformer.generate(clubInvitationsEntityDto));
        clubInvitationsRepositoryDAO.saveAndFlush(newClubInvitationsEntity);

        return clubInvitationsEntityDtoTransformer.generate(newClubInvitationsEntity);
    }

}