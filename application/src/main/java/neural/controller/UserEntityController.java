package neural.controller;

// import Paths;     --use later if wish to have Paths restricted/opened via separate class--
import core.services.UserEntityService;
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

    public UserEntityController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
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
        userEntityDto.setPassword(null);
        if (userEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        if (userEntityDto.getFriendsSet().isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };
        return ResponseEntity.ok(userEntityDto);
    }

    // GET a user (without the friendships Set). so user knows publicProfile privacy setting.
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

    // POST to add a new user
    @RequestMapping(value = "/signup",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntityDto> createUserEntity(
            @Valid
            @RequestBody
            final UserEntityDto userEntityDto) {

        if (userEntityDto.getUserName().length() < 4 ) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };
        if (userEntityDto.getPassword().length() < 4 ) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };

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
}
