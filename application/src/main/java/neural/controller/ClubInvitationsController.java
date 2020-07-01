package neural.controller;

import core.services.ClubInvitationsEntityService;
import db.entity.ClubInvitationsEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.ClubInvitationsEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/i", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "ClubInvitationsEntity endpoints", tags = "ClubInvitationsEntity")
public class ClubInvitationsController extends AbstractRestController {

    private ClubInvitationsEntityService clubInvitationsEntityService;

    public ClubInvitationsController(ClubInvitationsEntityService clubInvitationsEntityService ) {
        this.clubInvitationsEntityService = clubInvitationsEntityService;
    }

    // GET set of new club invitations.
    @ApiOperation(value = "getClubInvitationsEntity")
    @RequestMapping(value = "/a", method = RequestMethod.GET)
    public ResponseEntity<Set<ClubInvitationsEntity>> getClubInvitations(
            @RequestHeader("Authorization") String token)               {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        Set<ClubInvitationsEntity> foundNewClubInvitations = clubInvitationsEntityService.getNewClubInvitations(user);

        if (foundNewClubInvitations.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(foundNewClubInvitations);
    }



    // POST a new club invitation
    @RequestMapping(value = "/b", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClubInvitationsEntityDto> createClubsEntity(
            @Valid
            @RequestBody final ClubInvitationsEntityDto clubInvitationsEntityDto,
            @RequestHeader("Authorization") String token,
            @RequestParam("cId") final Long clubId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        ClubInvitationsEntityDto savedClubInvitationsEntityDto = clubInvitationsEntityService.createClubInvitationsEntity(clubInvitationsEntityDto, user, clubId);
        return ResponseEntity.ok(savedClubInvitationsEntityDto);
    }


}
