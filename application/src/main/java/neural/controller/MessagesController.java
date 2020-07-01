package neural.controller;

import core.services.MessagesEntityService;
import db.entity.FriendshipsEntity;
import db.entity.MessagesEntity;
import db.entity.UserEntity;
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
import java.util.Set;

@RestController
@RequestMapping(value = "/api/m", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "MessagesEntity endpoints", tags = "MessagesEntity")
public class MessagesController extends AbstractRestController {

    private MessagesEntityService messagesEntityService;
    private MessagesRepositoryDAO messagesRepositoryDAO;
    private final UserRepositoryDAO userRepositoryDAO;
    private final FriendshipsRepositoryDAO friendshipsRepositoryDAO;

    public MessagesController(MessagesEntityService messagesEntityService, MessagesRepositoryDAO messagesRepositoryDAO, final UserRepositoryDAO userRepositoryDAO, FriendshipsRepositoryDAO friendshipsRepositoryDAO ) {
        this.messagesEntityService = messagesEntityService;
        this.messagesRepositoryDAO = messagesRepositoryDAO;
        this.userRepositoryDAO = userRepositoryDAO;
        this.friendshipsRepositoryDAO = friendshipsRepositoryDAO;
    }

    // GET a single message *** Not used....
    @ApiOperation(value = "getMessagesEntity")
    @RequestMapping(value = "/a{mId}", method = RequestMethod.GET)
    public ResponseEntity<MessagesEntityDto> getMessagesEntity(
            @RequestHeader("Authorization") String token,
            //@PathVariable("cId") final Long clubsEntityId) {
            @RequestParam("mId") final Long messagesEntityId) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        MessagesEntityDto messagesEntityDto = messagesEntityService.getMessagesEntity(messagesEntityId );
        if (messagesEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(messagesEntityDto);
    }

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

        Set<MessagesEntity> clubMessages = messagesRepositoryDAO.getClubMessages(clubsEntityId);

        // TODO check that user is in club (validation)
        // for loop to clean Set data
        // if empty return empty set.

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

        // get messages sent by logged in user
        Set<MessagesEntity> twoUsersMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverType(senderUserEntity, individualEntityId, new Long(1));

        // add messages sent by other person/club member. Also, change flag to read/1.
        UserEntity receiverUserEntity = userRepositoryDAO.findOneById(individualEntityId);
        Set<MessagesEntity> newReceivedMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverType(receiverUserEntity, senderUserEntity.getId(), new Long(1));
        for (MessagesEntity x : newReceivedMessages) {
            if (x.getRedFlag() ==  null) {
            x.setRedFlag(new Long(1));
            messagesRepositoryDAO.save(x);}
        }

        twoUsersMessages.addAll(newReceivedMessages);

        // TODO check that user is in club (validation)
        // for loop to clean Set data
        // if empty return empty set.

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

        // get messages sent by logged in user
        Set<MessagesEntity> twoUsersMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverType(senderUserEntity, friendsUserEntity.getId(), new Long(1));

        // add messages sent by friend
        Set<MessagesEntity> newReceivedMessages = messagesRepositoryDAO.findAllBySenderAndReceiverIdAndReceiverType(friendsUserEntity, senderUserEntity.getId(), new Long(1));
        for (MessagesEntity x : newReceivedMessages) {
            if (x.getRedFlag() == null) {
                x.setRedFlag(new Long(1));
                messagesRepositoryDAO.save(x);}
        }

        twoUsersMessages.addAll(newReceivedMessages);


        // TODO check that user is in club (validation)
        // for loop to clean Set data
        // if empty return empty set.

        return ResponseEntity.ok(twoUsersMessages);
    }

    // GET list of ALL messages between ALL sets of two contacts.
    @ApiOperation(value = "getAlertsContactsMessagesEntity")
    @RequestMapping(value = "/f", method = RequestMethod.GET)
    public ResponseEntity<Set<MessagesEntity>> getAlertsContactMessages(
            @RequestHeader("Authorization") String token ) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntity receiverUserEntity = userRepositoryDAO.findOneByUserName(user);

        Set<MessagesEntity> alertsContactMessages = messagesRepositoryDAO.findAllByReceiverIdAndReceiverTypeAndRedFlag(receiverUserEntity.getId(), new Long(4), new Long(0));

        return ResponseEntity.ok(alertsContactMessages);
    }

    // GET list of ALL messages between ALL sets of two club members.
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

        Set<MessagesEntity> alertsContactMessages = messagesRepositoryDAO.findAllByReceiverIdAndReceiverTypeAndRedFlag(receiverUserEntity.getId(), new Long(1), new Long(0));

        return ResponseEntity.ok(alertsContactMessages);
    }

    // GET list of ALL messages between ALL sets of two guild members.
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

        Set<MessagesEntity> alertsContactMessages = messagesRepositoryDAO.findAllByReceiverIdAndReceiverTypeAndRedFlag(receiverUserEntity.getId(), new Long(5), new Long(0));

        return ResponseEntity.ok(alertsContactMessages);
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

        // TODO validations

        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendshipsEntityId);
        UserEntity friendsUserEntity = userRepositoryDAO.findOneByUserName(foundFriendshipsEntity.getFriend());
        messagesEntityDto.setReceiverId(friendsUserEntity.getId());
        MessagesEntityDto savedMessagesEntityDto = messagesEntityService.createMessagesEntity(messagesEntityDto, user);

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

        // TODO validations

        MessagesEntityDto savedMessagesEntityDto = messagesEntityService.createMessagesEntity(messagesEntityDto, user);

        return ResponseEntity.ok(savedMessagesEntityDto);
    }


}
