package core.services;

import core.transformers.UserEntityDtoTransformer;
import db.entity.FriendshipsEntity;
import db.entity.UserEntity;
import db.repository.FriendshipsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import model.UserEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Service
public class UserEntityService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepositoryDAO userEntityRepository;
    private final UserEntityDtoTransformer userEntityDtoTransformer;
    private FriendshipsRepositoryDAO friendshipsRepositoryDAO;

    public UserEntityService(final UserRepositoryDAO userEntityRepository, final UserEntityDtoTransformer userEntityDtoTransformer,
                             FriendshipsRepositoryDAO friendshipsRepositoryDAO) {
        this.userEntityRepository = userEntityRepository;
        this.userEntityDtoTransformer = userEntityDtoTransformer;
        this.friendshipsRepositoryDAO = friendshipsRepositoryDAO;
    }

    // GET
    public UserEntityDto getUserEntity(final String userName, final String password) {
        return userEntityDtoTransformer.generate(userEntityRepository.findOneByUserNameAndPassword(userName, password));
    }

    // GET - General
    public UserEntityDto getUserEntity(final String userName) {
        return userEntityDtoTransformer.generate(userEntityRepository.findOneByUserName(userName));
    }

    // GET - without removed friends
    public UserEntityDto getUserEntityWithoutRemovedFriends(final String userName) {
        UserEntityDto foundUser = userEntityDtoTransformer.generate(userEntityRepository.findOneByUserName(userName));
        Set<FriendshipsEntity> foundFriendshipsEntities = foundUser.getFriendsSet();
        foundFriendshipsEntities.removeIf(i -> i.getConnectionStatus().equals("removed"));
        foundUser.setFriendsSet(foundFriendshipsEntities);
        for (FriendshipsEntity x : foundUser.getFriendsSet() ) {
            x.setUserEntity(null); x.setCreated(null); }
        foundUser.setId(null); foundUser.setCreated(null); foundUser.setPublicProfile(null); foundUser.setId(null);
        return foundUser;
    }

    // GET - with removed friends only
    public UserEntityDto getUserEntityRemovedFriendsOnly(final String userName) {
        UserEntityDto foundUser = userEntityDtoTransformer.generate(userEntityRepository.findOneByUserName(userName));
        Set<FriendshipsEntity> foundFriendshipsEntities = foundUser.getFriendsSet();
        foundFriendshipsEntities.removeIf(i -> !i.getConnectionStatus().equals("removed"));
        foundUser.setFriendsSet(foundFriendshipsEntities);
        for (FriendshipsEntity x : foundUser.getFriendsSet() ) {
            x.setUserEntity(null); x.setCreated(null); }
        foundUser.setId(null); foundUser.setCreated(null); foundUser.setPublicProfile(null); foundUser.setId(null);
        return foundUser;
    }

    // GET (for alerts list of recent friendships invitations).
    public UserEntityDto getUserEntityRecentFriends(final String userName) {
        //LocalDate windowDate = LocalDate.now().minusDays(14);
        UserEntityDto foundUser = userEntityDtoTransformer.generate(userEntityRepository.findOneByUserName(userName));
        Set<FriendshipsEntity> foundFriendshipsEntities = foundUser.getFriendsSet();
        foundFriendshipsEntities.removeIf(i -> !i.getConnectionStatus().equals("pending"));
        foundFriendshipsEntities.removeIf(i -> i.getInviter().equals(userName));
        //foundFriendshipsEntities.removeIf(i -> windowDate.isAfter(i.getCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
        foundUser.setFriendsSet(foundFriendshipsEntities);
        for (FriendshipsEntity x : foundUser.getFriendsSet() ) {
            x.setUserEntity(null); }
        foundUser.setId(null); foundUser.setCreated(null); foundUser.setPublicProfile(null); foundUser.setId(null);
        return foundUser;
    }

    // GET friend userEntity for profile text in Network render single contact
    public UserEntityDto getFriendUserEntity(final String user, final Long friendId) {

        // validate that contact is indeed a friend of the user
        UserEntity foundUserEntity = userEntityRepository.findOneByUserName(user);
        Set<FriendshipsEntity> foundFriendshipsEntities = foundUserEntity.getFriendsSet();
        foundFriendshipsEntities.removeIf(i -> !i.getId().equals(friendId));
        if (foundFriendshipsEntities.isEmpty()) { return userEntityDtoTransformer.generate(foundUserEntity); };

        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendId);
        String friendUserName = foundFriendshipsEntity.getFriend();
        UserEntity friendUserEntity = userEntityRepository.findOneByUserName(friendUserName);

        return userEntityDtoTransformer.generate(friendUserEntity);
    }

    // GET friend userEntity for profile text in Network render single contact
    public UserEntityDto getClubMember(final String user, final Long clubId, final Long memberId) {

        //
        UserEntity foundUserEntity = userEntityRepository.findOneByUserName(user);

        UserEntity foundClubMember = userEntityRepository.findOneById(memberId);

        // TODO validate user in club, and this member is in that club.

        return userEntityDtoTransformer.generate(foundClubMember);
    }

    // GET - friends of friend. friend's UserEntity without removed friends
    public UserEntityDto getFriendsUserEntityWithoutRemovedFriends(final String user, final Long friendId) {

        // validate that contact is indeed a friend of the user (if not, just return the 'confusing' user himself)
        UserEntity foundUserEntity = userEntityRepository.findOneByUserName(user);
        Set<FriendshipsEntity> foundFriendshipsEntities = foundUserEntity.getFriendsSet();
        foundFriendshipsEntities.removeIf(i -> !i.getId().equals(friendId));
        if (foundFriendshipsEntities.isEmpty()) { return userEntityDtoTransformer.generate(foundUserEntity); };

        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendId);
        String friendUserName = foundFriendshipsEntity.getFriend();
        UserEntity friendUserEntity = userEntityRepository.findOneByUserName(friendUserName);

        UserEntityDto foundUser = userEntityDtoTransformer.generate(friendUserEntity);
        Set<FriendshipsEntity> foundFriendshipsEntities2 = foundUser.getFriendsSet();
        foundFriendshipsEntities2.removeIf(i -> i.getConnectionStatus().equals("removed"));
        foundFriendshipsEntities2.removeIf(i -> i.getConnectionStatus().equals("pending"));
        foundFriendshipsEntities2.removeIf(i -> i.getFriend().equals(user));
        foundUser.setFriendsSet(foundFriendshipsEntities2);
        for (FriendshipsEntity x : foundUser.getFriendsSet() ) {
            x.setUserEntity(null); x.setCreated(null); }
        foundUser.setId(null); foundUser.setCreated(null); foundUser.setPublicProfile(null); foundUser.setId(null);
        return foundUser;
    }

    // GET - friends of friend. friend's UserEntity without removed friends
    public UserEntityDto getSetofFriendsofFriend(final String user, final Long friendId) {

        // validate that contact is indeed a friend of the user (if not, just return the 'confusing' user himself)
        UserEntity foundUserEntity = userEntityRepository.findOneByUserName(user);
        Set<FriendshipsEntity> foundFriendshipsEntities = foundUserEntity.getFriendsSet();
        foundFriendshipsEntities.removeIf(i -> !i.getId().equals(friendId));
        if (foundFriendshipsEntities.isEmpty()) { return userEntityDtoTransformer.generate(foundUserEntity); };

        // get the UserEntity of the friend
        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendId);
        String friendUserName = foundFriendshipsEntity.getFriend();
        UserEntity friendUserEntity = userEntityRepository.findOneByUserName(friendUserName);

        // must go to transformer first. then reduce Set<FriendshipsEntity>.
        UserEntityDto foundUser = userEntityDtoTransformer.generate(friendUserEntity);
        Set<FriendshipsEntity> foundFriendshipsEntities2 = foundUser.getFriendsSet();
        foundFriendshipsEntities2.removeIf(i -> i.getConnectionStatus().equals("removed"));
        foundFriendshipsEntities2.removeIf(i -> i.getConnectionStatus().equals("pending"));
        foundFriendshipsEntities2.removeIf(i -> i.getFriend().equals(user));
        foundUser.setFriendsSet(foundFriendshipsEntities2);

        // reduce data
        foundUser.setId(null); foundUser.setCreated(null); foundUser.setPublicProfile(null);
        foundUser.setEducation(null); foundUser.setOccupation(null); foundUser.setRelationshipStatus(null);
        foundUser.setContactInfo(null); foundUser.setLocation(null); foundUser.setBlurb(null);
        foundUser.setTitle(null); foundUser.setPassword(null);
        for (FriendshipsEntity x : foundUser.getFriendsSet() ) {
            x.setUserEntity(null); // initially clear out. this is actually just the 'foundUserEntity' anyway. will use it below as a clever way to pull in 'title'.
            x.setCreated(null);
            x.setInviter(null);
            x.setVisibilityPermission(null);}

        // add title to each friend of friend using this 'clever' trick. TODO: use JPQL/SQL instead of this tecnique.
        for (FriendshipsEntity x : foundUser.getFriendsSet() ) {
            x.setUserEntity(userEntityRepository.findOneByUserName(x.getFriend()));
            x.getUserEntity().setFriendsSet(null);
            x.getUserEntity().setPublicProfile(null);
            x.getUserEntity().setPassword(null);
            x.getUserEntity().setUserName(null);
            x.getUserEntity().setBlurb(null);
            x.getUserEntity().setEducation(null);
            x.getUserEntity().setOccupation(null);
            x.getUserEntity().setRelationshipStatus(null);
            x.getUserEntity().setContactInfo(null);
            x.getUserEntity().setCreated(null);
            x.getUserEntity().setId(null);
        }

        return foundUser;
    }

    // POST a new user
    public UserEntityDto createUserEntity(final UserEntityDto userEntityDto) {
            userEntityDto.setPublicProfile("Network");
            UserEntity userEntity = userEntityRepository.saveAndFlush(userEntityDtoTransformer.generate(userEntityDto));
            return userEntityDtoTransformer.generate(userEntity);
    }

    // PATCH permissions update
    public UserEntityDto patchUserEntity(final UserEntityDto userEntityDto) {
        UserEntity userEntity = userEntityRepository.findOneByUserName(userEntityDto.getUserName());
        userEntity.setPublicProfile(userEntityDto.getPublicProfile());
        userEntityRepository.save(userEntity);
        return userEntityDtoTransformer.generate(userEntity);
    }

    // PATCH password update
    public UserEntityDto patchPasswordUserEntity(final String userName, final String password, final String newPassword) {
        UserEntity userEntity = userEntityRepository.findOneByUserNameAndPassword(userName, password);
        userEntity.setPassword(newPassword);
        userEntityRepository.save(userEntity);
        return userEntityDtoTransformer.generate(userEntity);
    }

    // POST/PATCH profile fields post/update
    public UserEntityDto patchProfileUserEntity(final String userName, final UserEntityDto userEntityDto) {
        UserEntity userEntity = userEntityRepository.findOneByUserName(userName);
        userEntity.setTitle(userEntityDto.getTitle());
        userEntity.setBlurb(userEntityDto.getBlurb());
        userEntity.setEducation(userEntityDto.getEducation());
        userEntity.setOccupation(userEntityDto.getOccupation());
        userEntity.setRelationshipStatus(userEntityDto.getRelationshipStatus());
        userEntity.setLocation(userEntityDto.getLocation());
        userEntity.setContactInfo(userEntityDto.getContactInfo());
        userEntityRepository.save(userEntity);
        return userEntityDtoTransformer.generate(userEntity);
    }
}
