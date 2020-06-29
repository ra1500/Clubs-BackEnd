package neural.controller;

// import Paths;     --use later if wish to have Paths restricted/opened via separate class--
import core.services.UserEntityService;
import db.entity.UserEntity;
import db.repository.UserRepositoryDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.UserEntityDto;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping(value = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "UserEntity endpoints", tags = "UserEntity")
public class UserEntityController extends AbstractRestController {

    private UserEntityService userEntityService;
    private UserRepositoryDAO userEntityRepository;

    public UserEntityController(UserEntityService userEntityService, UserRepositoryDAO userEntityRepository) {
        this.userEntityService = userEntityService;
        this.userEntityRepository = userEntityRepository;
    }

    // GET a user (and user's friendships). Excludes removed friends.
    @ApiOperation(value = "getUserEntity")
    @RequestMapping(value = "/n", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getUserEntitySansRemovedFriends(
            @RequestHeader("Authorization") String token)               {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntityDto userEntityDto = userEntityService.getUserEntityWithoutRemovedFriends(user);

        userEntityDto.setClubsSet(null);
        userEntityDto.setPassword(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        if (userEntityDto.getFriendsSet().isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };
        return ResponseEntity.ok(userEntityDto);
    }

    // GET. friends of friend. a friend's userEntity (with friendships). Excludes removed & pending friends.
    @ApiOperation(value = "getUserEntity")
    @RequestMapping(value = "/q", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getFriendsUserEntitySansRemovedFriends(
            @RequestHeader("Authorization") String token,
            @RequestParam("fid") final Long friendId){

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntityDto userEntityDto = userEntityService.getFriendsUserEntityWithoutRemovedFriends(user, friendId);
        userEntityDto.setClubsSet(null);
        userEntityDto.setPassword(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        if (userEntityDto.getFriendsSet().isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };
        return ResponseEntity.ok(userEntityDto);
    }

    // GET. friends of friend. a friend's userEntity (with friendships). Excludes removed & pending friends.
    @ApiOperation(value = "getUserEntity")
    @RequestMapping(value = "/t", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getSetFriendsofFriend(
            @RequestHeader("Authorization") String token,
            @RequestParam("fid") final Long friendId){

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntityDto userEntityDto = userEntityService.getSetofFriendsofFriend(user, friendId);
        userEntityDto.setClubsSet(null);
        userEntityDto.setPassword(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        if (userEntityDto.getFriendsSet().isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };
        return ResponseEntity.ok(userEntityDto);
    }

    // GET a user (and user's friendships). Removed friends only.
    @ApiOperation(value = "getUserEntity")
    @RequestMapping(value = "/r", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getUserEntityRemovedFriends(
            @RequestHeader("Authorization") String token)               {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntityDto userEntityDto = userEntityService.getUserEntityRemovedFriendsOnly(user);
        userEntityDto.setClubsSet(null);
        userEntityDto.setPassword(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        if (userEntityDto.getFriendsSet().isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };
        return ResponseEntity.ok(userEntityDto);
    }

    // GET a user (without the friendships Set or clubs set).
    @ApiOperation(value = "getUserEntity")
    @RequestMapping(value = "/pr", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getUserEntity2(
            @RequestHeader("Authorization") String token)               {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntityDto userEntityDto = userEntityService.getUserEntity(user);
        userEntityDto.setPassword(null);
        userEntityDto.setFriendsSet(null);
        userEntityDto.setClubsSet(null);
        userEntityDto.setUserName(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(userEntityDto);
    }

    // GET a user via id (without the friendships Set or clubs set). (to get from clubMembers).
    @ApiOperation(value = "getUserEntity")
    @RequestMapping(value = "/pu", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getUserEntity3(
            @RequestHeader("Authorization") String token,
            @RequestParam("cid") final Long clubId,
            @RequestParam("mid") final Long memberId){

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntityDto userEntityDto = userEntityService.getClubMember(user, clubId, memberId);
        userEntityDto.setPassword(null);
        userEntityDto.setFriendsSet(null);
        userEntityDto.setClubsSet(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(userEntityDto);
    }

    // GET a friend's userEntity for profile text (without the friendships Set).
    @ApiOperation(value = "getUserEntity")
    @RequestMapping(value = "/ps", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getFriendUserEntityProfile(
            @RequestHeader("Authorization") String token,
            @RequestParam("fid") final Long friendId){

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntityDto userEntityDto = userEntityService.getFriendUserEntity(user, friendId);
        userEntityDto.setPassword(null);
        userEntityDto.setFriendsSet(null);
        userEntityDto.setClubsSet(null);
        userEntityDto.setUserName(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(userEntityDto);
    }

    // GET a user with new friendships for alerts.
    @ApiOperation(value = "getUserEntity")
    @RequestMapping(value = "/al", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getUserEntityAlerts(
            @RequestHeader("Authorization") String token)               {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntityDto userEntityDto = userEntityService.getUserEntityRecentFriends(user);
        userEntityDto.setPassword(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(userEntityDto);
    }

    // GET a user and their clubs set/list (without the friendships Set).
    @ApiOperation(value = "getUserEntity")
    @RequestMapping(value = "/pa", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getUserEntityClubs(
            @RequestHeader("Authorization") String token)               {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        UserEntityDto userEntityDto = userEntityService.getUserEntity(user);
        userEntityDto.setPassword(null);
        userEntityDto.setFriendsSet(null);
        userEntityDto.setUserName(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(userEntityDto);
    }

    // GET. Public Profile Text
    @ApiOperation(value = "publicProfileText")
    @RequestMapping(value = "/pp{id}", method = RequestMethod.GET)
    public ResponseEntity<UserEntityDto> getPermissionsEntityUserScorePublicProfilePage(
            @RequestParam("id") final String userName) {
        UserEntityDto foundUserEntity = userEntityService.getUserEntity(userName);
        if (foundUserEntity == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }

        if (foundUserEntity.getPublicProfile().equals("Public") ) {
            foundUserEntity.setFriendsSet(null);
            //foundUserEntity.setClubsSet(null);
            foundUserEntity.setCreated(null);
            foundUserEntity.setId(null);
            foundUserEntity.setPassword(null);
            foundUserEntity.setContactInfo(null);
            foundUserEntity.setRelationshipStatus(null);
            return ResponseEntity.ok(foundUserEntity);
        }
        else { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
    }

    // POST to add a new user
    @RequestMapping(value = "/signup",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntityDto> createUserEntity(
            @Valid
            @RequestBody
            final UserEntityDto userEntityDto) {

        if (userEntityDto.getUserName().length() < 4 ) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };
        if (userEntityDto.getPassword().length() < 4 ) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };
        if (userEntityRepository.findOneByUserName(userEntityDto.getUserName()) != null ) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
            userEntityDto.setOccupation("");
            userEntityDto.setEducation(new Long(5));
            userEntityDto.setRelationshipStatus(new Long(3));
            UserEntityDto savedUserEntityDto = userEntityService.createUserEntity(userEntityDto);
        return ResponseEntity.ok(savedUserEntityDto);
    }

    // POST change password
    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntityDto> updatePassword(
            @RequestHeader("Authorization") String token,
            @Valid
            @RequestBody
            final UserEntityDto userEntityDto) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        final String[] values = credentials.split(":", 2);
        String user = values[0];
        String password = values[1];

        if (userEntityDto.getUserName().equals(password)) { // this is a lame security check. front-end put 'old' password in as 'userName' as a security check, even though it is available in the sessionStorage.
            UserEntityDto patchedUserEntityDto = userEntityService.patchPasswordUserEntity(user, password, userEntityDto.getPassword());
            userEntityDto.setFriendsSet(null);
            return ResponseEntity.ok(patchedUserEntityDto);
        }
        else {
            userEntityDto.setUserName(null);
            userEntityDto.setPassword("error");
            return ResponseEntity.ok(userEntityDto);
        }
    }

    // POST from client Login form. Check if user exists. Return token.
    @RequestMapping(value = "/userId",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntityDto> verifyLoginUserEntity(
            @Valid
            @RequestBody
            final UserEntityDto userEntityDto) {

        UserEntityDto verifiedUserEntityDto = userEntityService.getUserEntity(userEntityDto.getUserName(),userEntityDto.getPassword());
        verifiedUserEntityDto.setFriendsSet(null);
        verifiedUserEntityDto.setPublicProfile(null);
        verifiedUserEntityDto.setId(null);
        verifiedUserEntityDto.setCreated(null);

        if (verifiedUserEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(verifiedUserEntityDto);
    }

    // PATCH a user's publicProfile privacy setting
    @RequestMapping(value = "/up", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntityDto> patchPublicProfile(
            @RequestHeader("Authorization") String token,
            @Valid
            @RequestBody
            final UserEntityDto userEntityDto) {

        // getting userName from Authorization token to secure endpoint.
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];
        String password = values[1];

        // setting/securing DTO userName as obtained from the Authorization token.
        userEntityDto.setUserName(user);
        userEntityDto.setPassword(password); // password cannot be null for a post/patch

        UserEntityDto patchedUserEntityDto = userEntityService.patchUserEntity(userEntityDto);
        patchedUserEntityDto.setPassword(null); //outgoing dto shouldnt have the password.
        patchedUserEntityDto.setFriendsSet(null);
        patchedUserEntityDto.setCreated(null);
        patchedUserEntityDto.setId(null);

        return ResponseEntity.ok(patchedUserEntityDto);
    }

    // POST a user's profile fields
    @RequestMapping(value = "/pf", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntityDto> patchProfileFields(
            @RequestHeader("Authorization") String token,
            @Valid
            @RequestBody
            final UserEntityDto userEntityDto) {

        // getting userName from Authorization token to secure endpoint.
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];
        String password = values[1];

        // setting/securing DTO userName as obtained from the Authorization token.
        userEntityDto.setUserName(user);
        userEntityDto.setPassword(password); // password cannot be null for a post/patch

        UserEntityDto patchedUserEntityDto = userEntityService.patchProfileUserEntity(user, userEntityDto);
        patchedUserEntityDto.setPassword(null); //outgoing dto shouldnt have the password.
        patchedUserEntityDto.setFriendsSet(null);
        patchedUserEntityDto.setCreated(null);
        patchedUserEntityDto.setId(null);

        return ResponseEntity.ok(patchedUserEntityDto);
    }



}
