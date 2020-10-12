package core.services;

import core.transformers.FriendshipsEntityDtoTransformer;
import db.entity.FriendshipsEntity;
import db.entity.UserEntity;
import db.repository.FriendshipsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import model.FriendshipsEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FriendshipsEntityService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final FriendshipsRepositoryDAO friendshipsRepositoryDAO;
    private final UserRepositoryDAO userRepositoryDAO;
    private final FriendshipsEntityDtoTransformer friendshipsEntityDtoTransformer;

    public FriendshipsEntityService(final FriendshipsRepositoryDAO friendshipsRepositoryDAO, final FriendshipsEntityDtoTransformer friendshipsEntityDtoTransformer,
                                    UserRepositoryDAO userRepositoryDAO) {
        this.friendshipsRepositoryDAO = friendshipsRepositoryDAO;
        this.friendshipsEntityDtoTransformer = friendshipsEntityDtoTransformer;
        this.userRepositoryDAO = userRepositoryDAO;
    }

    // GET.
    public FriendshipsEntityDto getFriendshipsEntity(final String user, final Long friendId) {
        UserEntity userEntity = userRepositoryDAO.findOneByUserName(user);
        Long userEntityId = userEntity.getId();

        return friendshipsEntityDtoTransformer.generate(friendshipsRepositoryDAO.findOneByIdAndUserEntityId(friendId, userEntityId));
    }

    // POST a new friendship (double entry of friendships(qty 2)
    public FriendshipsEntityDto createFriendshipsEntity(final FriendshipsEntityDto friendshipsEntityDto, final String userName) {

        // validation. cannot invite self.
        if (friendshipsEntityDto.getFriend().equals(userName)) { return friendshipsEntityDto; }

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);

        // validation. invitation is new and friendship does not already exist.
        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneByUserEntityIdAndId(foundUserEntity.getId(), friendshipsEntityDto.getId());
        if (foundFriendshipsEntity != null) { return friendshipsEntityDto; }

        // get friend userEntity if it exists. if does not exist, break with error.
        UserEntity friendExistsUserEntity = userRepositoryDAO.findOneByUserName(friendshipsEntityDto.getFriend());
        if (friendExistsUserEntity == null) { return friendshipsEntityDto; }

        // limit quantity of friendships
        if (foundUserEntity.getFriendsSet().size() > 300) {
            friendshipsEntityDto.setConnectionStatus("OVER LIMIT");
            return friendshipsEntityDto; }

        // create a new 'raw' friendshipsEntity (1 of 2 entries) (ManyToOne twice, instead of ManyToMany)
        FriendshipsEntity newFriendshipsEntity1 = friendshipsEntityDtoTransformer.generate(friendshipsEntityDto);

        // add userEntity
        newFriendshipsEntity1.setUserEntity(foundUserEntity);

        // save completed friendshipsEntity1
        friendshipsRepositoryDAO.saveAndFlush(newFriendshipsEntity1);

        // create a new 'raw' friendshipsEntity (2 of 2 entries) (ManyToOne twice, instead of ManyToMany)
        FriendshipsEntityDto friendshipsEntityDto2 = friendshipsEntityDto;
        friendshipsEntityDto2.setFriend(userName);
        FriendshipsEntity newFriendshipsEntity2 = friendshipsEntityDtoTransformer.generate(friendshipsEntityDto2);

        // add userEntity (of friend)
        newFriendshipsEntity2.setUserEntity(friendExistsUserEntity);

        // save completed friendshipsEntity2
        friendshipsRepositoryDAO.saveAndFlush(newFriendshipsEntity2);

        // return only the 'main' 1st entry friendhsipsEntity
        return friendshipsEntityDtoTransformer.generate(newFriendshipsEntity1);
    }

    // POST unremove a friendship (from 'removed' to either 'Connected' or 'pending').
    public FriendshipsEntityDto unRemoveFriendshipsEntity(final FriendshipsEntityDto friendshipsEntityDto, final String user) {

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);

        // validation. friendship exists.
        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendshipsEntityDto.getId());
        if (foundFriendshipsEntity == null) { return friendshipsEntityDto; }

        // validation. friendshipsEntity belongs to user
        if ( !foundFriendshipsEntity.getUserEntity().equals(foundUserEntity) ) { FriendshipsEntityDto errFriendshiipsEntity = new FriendshipsEntityDto(); errFriendshiipsEntity.setConnectionStatus("Error. Invalid update."); return errFriendshiipsEntity; };

        // modify single-side. From 'removed' to 'pending' or 'Connected' (depending on friend's connection status with user).
        if ( foundFriendshipsEntity.getConnectionStatus().equals("removed") ) {
            String secondUser = foundFriendshipsEntity.getFriend();
            Long friendId = userRepositoryDAO.findOneByUserName(secondUser).getId();
            String friendsStatus = friendshipsRepositoryDAO.findOneByUserEntityIdAndFriend(friendId, user).getConnectionStatus();

            if (friendsStatus.equals("pending")) {
                foundFriendshipsEntity.setConnectionStatus("pending");
                foundFriendshipsEntity.setVisibilityPermission("Yes");
                friendshipsRepositoryDAO.save(foundFriendshipsEntity);
                return friendshipsEntityDtoTransformer.generate(foundFriendshipsEntity);
            } else if (friendsStatus.equals("Connected")) {
                foundFriendshipsEntity.setConnectionStatus("Connected");
                foundFriendshipsEntity.setVisibilityPermission("Yes");
                friendshipsRepositoryDAO.save(foundFriendshipsEntity);
                return friendshipsEntityDtoTransformer.generate(foundFriendshipsEntity);
            } else {  // if  friend is 'removed' then the only possibility is to make it 'pending' here.
                foundFriendshipsEntity.setConnectionStatus("pending");
                foundFriendshipsEntity.setVisibilityPermission("Yes");
                friendshipsRepositoryDAO.save(foundFriendshipsEntity);
                return friendshipsEntityDtoTransformer.generate(foundFriendshipsEntity);
            }
        }
        else { return friendshipsEntityDto; }
    }

    // POST remove a 'Connected' friendship.
    public FriendshipsEntityDto removeFriendshipsEntity(final FriendshipsEntityDto friendshipsEntityDto, final String user) {

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);

        // validation. friendship exists.
        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendshipsEntityDto.getId());
        if (foundFriendshipsEntity == null) { return friendshipsEntityDto; }

        // validation. friendshipsEntity belongs to user
        if ( !foundFriendshipsEntity.getUserEntity().equals(foundUserEntity) ) { FriendshipsEntityDto errFriendshiipsEntity = new FriendshipsEntityDto(); errFriendshiipsEntity.setConnectionStatus("Error. Invalid update."); return errFriendshiipsEntity; };

        // modify single-side. From 'pending' or 'Connected' to 'remove'.
        foundFriendshipsEntity.setConnectionStatus("removed");
        foundFriendshipsEntity.setVisibilityPermission("No");
        friendshipsRepositoryDAO.save(foundFriendshipsEntity);
        return friendshipsEntityDtoTransformer.generate(foundFriendshipsEntity);
    }


    // POST accept an invitation
    public FriendshipsEntityDto acceptFriendshipsEntity(final FriendshipsEntityDto friendshipsEntityDto, final String user) {

        // get friendshipsEntity from db, if it exists.
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);
        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendshipsEntityDto.getId());

        // validation. does friendship actually exist
        if (foundFriendshipsEntity == null) { FriendshipsEntityDto errFriendshiipsEntity = new FriendshipsEntityDto(); errFriendshiipsEntity.setConnectionStatus("Error. Not found."); return errFriendshiipsEntity; };
        // validation. invitee/friend is indeed user
        if ( !foundFriendshipsEntity.getUserEntity().equals(foundUserEntity) ) { FriendshipsEntityDto errFriendshiipsEntity = new FriendshipsEntityDto(); errFriendshiipsEntity.setConnectionStatus("Error. Invalid update."); return errFriendshiipsEntity; };

        // update user's FriendshipsEntity to accept (limit qty of friendships).
        if (foundUserEntity.getFriendsSet().size() > 300) {
            friendshipsEntityDto.setConnectionStatus("OVER LIMIT");
            return friendshipsEntityDto;
        };
        foundFriendshipsEntity.setConnectionStatus("Connected");
        friendshipsRepositoryDAO.save(foundFriendshipsEntity);

        // second entry (two-sided). If invitation still pending, then connect, but if inviter rescinded/removed, then leave as connectionStatus of 'removed'.
        String secondUser = foundFriendshipsEntity.getFriend();
        UserEntity secondUserEntity = userRepositoryDAO.findOneByUserName(secondUser);
        FriendshipsEntity secondFriendshipsEntity = friendshipsRepositoryDAO.findOneByUserEntityIdAndFriend(secondUserEntity.getId(), user);

        if ( secondFriendshipsEntity.getConnectionStatus().equals("pending") ) {
            secondFriendshipsEntity.setConnectionStatus("Connected");
            friendshipsRepositoryDAO.save(secondFriendshipsEntity);
            return friendshipsEntityDtoTransformer.generate(foundFriendshipsEntity);
        }
        else {
            return friendshipsEntityDtoTransformer.generate(foundFriendshipsEntity);
        }
    }

    // POST decline an invitation
    public FriendshipsEntityDto declineFriendshipsEntity(final FriendshipsEntityDto friendshipsEntityDto, final String user) {

        // get friendshipsEntity from db, if it exists.
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);
        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendshipsEntityDto.getId());

        // validation. does friendship actually exist
        if (foundFriendshipsEntity == null) { FriendshipsEntityDto errFriendshiipsEntity = new FriendshipsEntityDto(); errFriendshiipsEntity.setConnectionStatus("Error. Not found."); return errFriendshiipsEntity; };
        // validation. invitee/friend is indeed user
        if ( !foundFriendshipsEntity.getUserEntity().equals(foundUserEntity) ) { FriendshipsEntityDto errFriendshiipsEntity = new FriendshipsEntityDto(); errFriendshiipsEntity.setConnectionStatus("Error. Invalid update."); return errFriendshiipsEntity; };

        // update user's FriendshipsEntity to decline
        foundFriendshipsEntity.setConnectionStatus("removed");
        friendshipsRepositoryDAO.save(foundFriendshipsEntity);

        return friendshipsEntityDtoTransformer.generate(foundFriendshipsEntity);

    }


    // POST update friendships connectionType
    public FriendshipsEntityDto updateConnectionType(final FriendshipsEntityDto friendshipsEntityDto, final String userName) {

        // get friendshipsEntity from db, if it exists.
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        Long userId = foundUserEntity.getId();
        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneByUserEntityIdAndId(userId, friendshipsEntityDto.getId());

        // validation. friendships entity must exist properly.
        if ( foundFriendshipsEntity == null ) { FriendshipsEntityDto errFriendshipsEntity = new FriendshipsEntityDto(); errFriendshipsEntity.setConnectionType("error"); return errFriendshipsEntity; };

        foundFriendshipsEntity.setConnectionType(friendshipsEntityDto.getConnectionType());
        friendshipsRepositoryDAO.save(foundFriendshipsEntity);
        return friendshipsEntityDtoTransformer.generate(foundFriendshipsEntity);
     }

    // for DELETE.
    public Integer deleteFriendshipsEntity(final Long id) {
        Integer deletedFriendshipsEntity = friendshipsRepositoryDAO.deleteOneById(id);
        return deletedFriendshipsEntity;
    }
}
