package core.services;

import core.transformers.ClubsEntityDtoTransformer;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.UserRepositoryDAO;
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

    public ClubsEntityService(final ClubsRepositoryDAO clubsRepositoryDAO,
                                  final ClubsEntityDtoTransformer clubsEntityDtoTransformer, final UserRepositoryDAO userRepositoryDAO) {
        this.clubsRepositoryDAO = clubsRepositoryDAO;
        this.clubsEntityDtoTransformer = clubsEntityDtoTransformer;
        this.userRepositoryDAO = userRepositoryDAO;
    }

    // GET
    public ClubsEntityDto getClubsEntity(final Long clubsEntityId) {
        return clubsEntityDtoTransformer.generate(clubsRepositoryDAO.findOneById(clubsEntityId));
    }

    // POST a new Club
    public ClubsEntityDto createClubsEntity(final ClubsEntityDto clubsEntityDto, final String userName) {

        // a new club includes a set of members.
       Set<UserEntity> newMembersSet = new HashSet<>();

       // add the creator's UserEntity to the new set of members. Add the set of members to the new club.
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        newMembersSet.add(foundUserEntity);
        clubsEntityDto.setMembers(newMembersSet);

        // add the new club to the user's set of club memberships
        ClubsEntity newClubsEntity = (clubsEntityDtoTransformer.generate(clubsEntityDto));
        foundUserEntity.getClubs().add(newClubsEntity);
        //userRepositoryDAO.saveAndFlush(foundUserEntity);
        clubsRepositoryDAO.saveAndFlush(newClubsEntity);

        return clubsEntityDtoTransformer.generate(newClubsEntity);
    }

}
