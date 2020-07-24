package neural.controller;

// import .Paths;     --use later if wish to have Paths restricted/opened via separate class--
import db.entity.UserEntity;
import db.repository.UserRepositoryDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.FriendshipsEntityDto;
import core.services.FriendshipsEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping(value = "/api/f", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "FriendshipsEntity endpoints", tags = "FriendshipsEntity")
public class FriendshipsEntityController extends AbstractRestController {

    private FriendshipsEntityService friendshipsEntityService;
    private UserRepositoryDAO userRepositoryDAO;

    public FriendshipsEntityController(FriendshipsEntityService friendshipsEntityService, UserRepositoryDAO userRepositoryDAO) {
        this.friendshipsEntityService = friendshipsEntityService;
        this.userRepositoryDAO = userRepositoryDAO; };

    // POST a NEW friendship
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FriendshipsEntityDto> createFriendshipsEntity(
            @RequestHeader("Authorization") String token,
            @Valid
            @RequestBody
            final FriendshipsEntityDto friendshipsEntityDto) {

            String base64Credentials = token.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String user = values[0];

            if (userRepositoryDAO.findOneByUserName(friendshipsEntityDto.getFriend()) == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            FriendshipsEntityDto savedFriendshipsEntityDto = friendshipsEntityService.createFriendshipsEntity(friendshipsEntityDto, user);
            return ResponseEntity.ok(savedFriendshipsEntityDto);
    }

    // POST a friendship (amend an existing). Acceptance, ...
    @RequestMapping(value = "/a", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FriendshipsEntityDto> amendFriendshipsEntity(
            @RequestHeader("Authorization") String token,
            @Valid
            @RequestBody
            final FriendshipsEntityDto friendshipsEntityDto) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        FriendshipsEntityDto savedFriendshipsEntityDto = friendshipsEntityService.createFriendshipsEntity(friendshipsEntityDto, user);
        return ResponseEntity.ok(savedFriendshipsEntityDto);
    }


    // GET a single friendship
    @ApiOperation(value = "getQuestionsEntity")
    @RequestMapping(value = "/{ct}", method = RequestMethod.GET)
    public ResponseEntity<FriendshipsEntityDto> getFriendshipsEntity(
            @RequestHeader("Authorization") String token,
            @PathVariable("ct")
            final Long ct) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        FriendshipsEntityDto friendshipsEntityDto = friendshipsEntityService.getFriendshipsEntity(user, ct);

        if (friendshipsEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(friendshipsEntityDto);
    }

}
