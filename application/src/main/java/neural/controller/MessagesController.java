package neural.controller;

import core.services.MessagesEntityService;
import db.entity.ClubsEntity;
import db.entity.FriendshipsEntity;
import db.entity.MessagesEntity;
import db.entity.UserEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.FriendshipsRepositoryDAO;
import db.repository.MessagesRepositoryDAO;
import db.repository.UserRepositoryDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.MessagesEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/m", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "MessagesEntity endpoints", tags = "MessagesEntity")
public class MessagesController extends AbstractRestController {

    private MessagesEntityService messagesEntityService;
    private MessagesRepositoryDAO messagesRepositoryDAO;
    private ClubsRepositoryDAO clubsRepositoryDAO;
    private final UserRepositoryDAO userRepositoryDAO;
    private final FriendshipsRepositoryDAO friendshipsRepositoryDAO;

    public MessagesController(MessagesEntityService messagesEntityService, MessagesRepositoryDAO messagesRepositoryDAO, final UserRepositoryDAO userRepositoryDAO, FriendshipsRepositoryDAO friendshipsRepositoryDAO, ClubsRepositoryDAO clubsRepositoryDAO ) {
        this.messagesEntityService = messagesEntityService;
        this.messagesRepositoryDAO = messagesRepositoryDAO;
        this.userRepositoryDAO = userRepositoryDAO;
        this.friendshipsRepositoryDAO = friendshipsRepositoryDAO;
        this.clubsRepositoryDAO = clubsRepositoryDAO;
    }

    // GET a single message *** Not used....
    //@ApiOperation(value = "getMessagesEntity")
    //@RequestMapping(value = "/a{mId}", method = RequestMethod.GET)
    //public ResponseEntity<MessagesEntityDto> getMessagesEntity(
    //        @RequestHeader("Authorization") String token,
    //        //@PathVariable("cId") final Long clubsEntityId) {
    //        @RequestParam("mId") final Long messagesEntityId) {
    //    String base64Credentials = token.substring("Basic".length()).trim();
    //    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
    //    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
    //    // credentials = username:password
    //    final String[] values = credentials.split(":", 2);
    //    String user = values[0];

    //    MessagesEntityDto messagesEntityDto = messagesEntityService.getMessagesEntity(messagesEntityId );
    //    if (messagesEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
    //    return ResponseEntity.ok(messagesEntityDto);
    //}

    // GET list of messages for a club
    @ApiOperation(value = "getMessagesEntity")
    @RequestMapping(value = "/c{cId}", method = RequestMethod.GET)
    public ResponseEntity<Set<MessagesEntity>> getClubMessages(
            @RequestHeader("Authorization") String token,
            //@PathVariable("cId") final Long clubsEntityId) {
            @RequestParam("cId") final Long clubsEntityId) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        // validation. User is indeed in club.
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);
        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubsEntityId);
        Set<UserEntity> foundUserSet = foundClubsEntity.getMembers();
        if ( !foundUserSet.contains(foundUserEntity) ) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };

        Set<MessagesEntity> clubMessages = messagesRepositoryDAO.getClubMessages(clubsEntityId);

        // reduce data and don't share passowrds etc.
        for (MessagesEntity y : clubMessages) {
            y.getSender().setPassword(null); y.getSender().setContactInfo(null); y.getSender().setPublicProfile(null);
            y.getSender().setCreated(null); y.getSender().setRelationshipStatus(null); y.getSender().setOccupation(null);
            y.getSender().setBlurb(null); y.getSender().setLocation(null); y.getSender().setTitle(null);
            y.getSender().setEducation(null);
        }

        return ResponseEntity.ok(clubMessages);
    }

    // GET list of messages between two club/guild members.
    @ApiOperation(value = "getIndividualMessagesEntity")
    @RequestMapping(value = "/i{iId}", method = RequestMethod.GET)
    public ResponseEntity<Set<MessagesEntity>> getIndividualMessages(
            @RequestHeader("Authorization") String token,
            //@PathVariable("cId") final Long clubsEntityId) {
            @RequestParam("iId") final Long individualEntityId) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        //TODO should this go through service and transformer instead of direct to messagesRepository?

        UserEntity senderUserEntity = userRepositoryDAO.findOneByUserName(user);

        // validation. none needed here since posting message does validation. therefore db ok, and user must be in club.

        // get messages sent by logged in user
        Set<MessagesEntity> twoUsersMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverType(senderUserEntity, individualEntityId, new Long(1));

        // add messages sent by other person/club member. Also, change flag to read/1.
        UserEntity receiverUserEntity = userRepositoryDAO.findOneById(individualEntityId);
        Set<MessagesEntity> newReceivedMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverType(receiverUserEntity, senderUserEntity.getId(), new Long(1));
        for (MessagesEntity x : newReceivedMessages) {
            if (x.getRedFlag().equals(new Long(0))) {
            x.setRedFlag(new Long(1));
            messagesRepositoryDAO.save(x);}
        }

        twoUsersMessages.addAll(newReceivedMessages);

        return ResponseEntity.ok(twoUsersMessages);
    }

    // GET list of messages between two friends/contacts.
    @ApiOperation(value = "getIndividualMessagesEntity")
    @RequestMapping(value = "/d{fId}", method = RequestMethod.GET)
    public ResponseEntity<Set<MessagesEntity>> getFriendMessages(
            @RequestHeader("Authorization") String token,
            //@PathVariable("cId") final Long clubsEntityId) {
            @RequestParam("fId") final Long friendshipsEntityId) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        //TODO should this go through service and transformer instead of direct to messagesRepository?

        UserEntity senderUserEntity = userRepositoryDAO.findOneByUserName(user);
        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendshipsEntityId);
        UserEntity friendsUserEntity = userRepositoryDAO.findOneByUserName(foundFriendshipsEntity.getFriend()); // TODO this is inefficent string search instead of id# search

        // validation. validate that friend(via friendshipsEntity) is indeed in user's friend's list.
        if ( !senderUserEntity.getFriendsSet().contains(foundFriendshipsEntity) ) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };

        // get messages sent by logged in user
        Set<MessagesEntity> twoUsersMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverType(senderUserEntity, friendsUserEntity.getId(), new Long(4));

        // add messages sent by friend
        Set<MessagesEntity> newReceivedMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverType(friendsUserEntity, senderUserEntity.getId(), new Long(4));
        for (MessagesEntity x : newReceivedMessages) {
            if (x.getRedFlag().equals(new Long(0))) {
                x.setRedFlag(new Long(1));
                messagesRepositoryDAO.save(x);}
        }

        twoUsersMessages.addAll(newReceivedMessages);

        // reduce data and don't share passowrds etc.
        for (MessagesEntity y : twoUsersMessages) {
            y.getSender().setPassword(null); y.getSender().setContactInfo(null); y.getSender().setPublicProfile(null);
            y.getSender().setCreated(null); y.getSender().setRelationshipStatus(null); y.getSender().setOccupation(null);
            y.getSender().setBlurb(null); y.getSender().setLocation(null); y.getSender().setTitle(null);
            y.getSender().setEducation(null);
        }

        return ResponseEntity.ok(twoUsersMessages);
    }

    // GET Alerts. list of contacts with messages not yet read.
    @ApiOperation(value = "getAlertsContactsMessagesEntity")
    @RequestMapping(value = "/f", method = RequestMethod.GET)
    public ResponseEntity<Set<String>> getAlertsContactMessages(
            @RequestHeader("Authorization") String token ) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntity receiverUserEntity = userRepositoryDAO.findOneByUserName(user);

        // validation. none needed here. based on 'user'.

        Set<MessagesEntity> alertsContactMessages = messagesRepositoryDAO.findAllByReceiverIdAndReceiverTypeAndRedFlag(receiverUserEntity.getId(), new Long(4), new Long(0));

        // reduce the set to unique usernames.
        Set<String> userNewMessagesSet = new HashSet<>();
        for (MessagesEntity x : alertsContactMessages) {
            userNewMessagesSet.add(x.getSender().getUserName());
        }

        return ResponseEntity.ok(userNewMessagesSet);
    }

    // GET Alerts. list of club members with messages not yet read.
    @ApiOperation(value = "getAlertsClubsMessagesEntity")
    @RequestMapping(value = "/g", method = RequestMethod.GET)
    public ResponseEntity<Set<MessagesEntity>> getAlertsClubsMessages(
            @RequestHeader("Authorization") String token ) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntity receiverUserEntity = userRepositoryDAO.findOneByUserName(user);

        // validation. none needed here. based on 'user'.

        Set<MessagesEntity> alertsClubMessages = messagesRepositoryDAO.getAlertsNewCLubMessages(receiverUserEntity.getId());

        return ResponseEntity.ok(alertsClubMessages);
    }

    // GET Alerts. list of guild members with messages not yet read.
    @ApiOperation(value = "getAlertsGuildMessagesEntity")
    @RequestMapping(value = "/h", method = RequestMethod.GET)
    public ResponseEntity<Set<MessagesEntity>> getAlertsGuildMessages(
            @RequestHeader("Authorization") String token ) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntity receiverUserEntity = userRepositoryDAO.findOneByUserName(user);

        // validation. none needed here. posted messages would ensure user in club. here, it is just receiver messages, validated through token.

        Set<MessagesEntity> alertsClubMessages = messagesRepositoryDAO.getAlertsNewGuildMessages(receiverUserEntity.getId());

        return ResponseEntity.ok(alertsClubMessages);
    }

    // POST a new message. Two friends/contacts.
    @RequestMapping(value = "/e", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessagesEntityDto> createFriendMessagesEntity(
            @RequestHeader("Authorization") String token,
            @RequestParam("fId") final Long friendshipsEntityId,
            @Valid
            @RequestBody
            final MessagesEntityDto messagesEntityDto) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);
        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendshipsEntityId);
        UserEntity friendsUserEntity = userRepositoryDAO.findOneByUserName(foundFriendshipsEntity.getFriend());

        // validation.  user is indeed in the specified friendshipsEntity.
        if ( !foundFriendshipsEntity.getUserEntity().equals(foundUserEntity) ) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };

        messagesEntityDto.setReceiverId(friendsUserEntity.getId());
        MessagesEntityDto savedMessagesEntityDto = messagesEntityService.createMessagesEntity(messagesEntityDto, user);

        savedMessagesEntityDto.setSender(null);

        return ResponseEntity.ok(savedMessagesEntityDto);
    }

    // TODO break into two methods. one for club messages. one for two individual club members.
    // POST a new message in clubs/guilds. club message board or two users from club/guild.
    @RequestMapping(value = "/b", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessagesEntityDto> createMessagesEntity(
            @RequestHeader("Authorization") String token,
            @Valid
            @RequestBody
            final MessagesEntityDto messagesEntityDto) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        // validations. see the service method for the validations.

        MessagesEntityDto savedMessagesEntityDto = messagesEntityService.createMessagesEntity(messagesEntityDto, user);

        // reduce data and don't share passowrds etc.
        savedMessagesEntityDto.getSender().setPassword(null); savedMessagesEntityDto.getSender().setContactInfo(null); savedMessagesEntityDto.getSender().setPublicProfile(null);
        savedMessagesEntityDto.getSender().setCreated(null); savedMessagesEntityDto.getSender().setRelationshipStatus(null); savedMessagesEntityDto.getSender().setOccupation(null);
        savedMessagesEntityDto.getSender().setBlurb(null); savedMessagesEntityDto.getSender().setLocation(null); savedMessagesEntityDto.getSender().setTitle(null);
        savedMessagesEntityDto.getSender().setEducation(null);


        return ResponseEntity.ok(savedMessagesEntityDto);
    }

    // GET clears unread messages flag from network contacts.
    @ApiOperation(value = "getIndividualMessagesEntity")
    @RequestMapping(value = "/j{uId}", method = RequestMethod.GET)
    public ResponseEntity<String> clearUnreadMsgs(
            @RequestHeader("Authorization") String token,
            @RequestParam("uId") final String friendsName) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntity userEntity = userRepositoryDAO.findOneByUserName(user);
        UserEntity friendsUserEntity = userRepositoryDAO.findOneByUserName(friendsName);

        // validation. none needed as 'user' is authenticated and is the receiver
        //FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneByUserEntityIdAndFriend(userEntity.getId(), friendsName);
        //if ( foundFriendshipsEntity == null ) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };

        Set<MessagesEntity> newReceivedMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverType(friendsUserEntity, userEntity.getId(), new Long(4));
        for (MessagesEntity x : newReceivedMessages) {
            if (x.getRedFlag().equals(new Long(0))) {
                x.setRedFlag(new Long(1));
                messagesRepositoryDAO.save(x);}
        }

        String success = "alert cleared";
        return ResponseEntity.ok(success);
    }

    // GET clears unread messages flag from club members and guild members.
    @ApiOperation(value = "getIndividualMessagesEntity")
    @RequestMapping(value = "/k{uId}{type}", method = RequestMethod.GET)
    public ResponseEntity<String> clearUnreadMsgsClubs(
            @RequestHeader("Authorization") String token,
            @RequestParam("uId") final String friendsName,
            @RequestParam("cN") final String clubName,
            @RequestParam("type") final Long type) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntity userEntity = userRepositoryDAO.findOneByUserName(user);
        UserEntity friendsUserEntity = userRepositoryDAO.findOneByUserName(friendsName);

        // validation. none needed as 'user' is authenticated and is the receiver

        Set<MessagesEntity> newReceivedMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverTypeAndClubName(friendsUserEntity, userEntity.getId(), type, clubName);
        for (MessagesEntity x : newReceivedMessages) {
            if (x.getRedFlag().equals(new Long(0))) {
                x.setRedFlag(new Long(1));
                messagesRepositoryDAO.save(x);}
        }

        String success = "alert cleared";
        return ResponseEntity.ok(success);
    }

}
