package neural.controller;

import core.services.ClubInvitationsEntityService;
import db.entity.ClubInvitationsEntity;
import db.entity.ClubsEntity;
import db.repository.ClubsRepositoryDAO;
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
    private final ClubsRepositoryDAO clubsRepositoryDAO;

    public ClubInvitationsController(ClubInvitationsEntityService clubInvitationsEntityService, final ClubsRepositoryDAO clubsRepositoryDAO ) {
        this.clubInvitationsEntityService = clubInvitationsEntityService;
        this.clubsRepositoryDAO = clubsRepositoryDAO;
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

        // validation. none needed here or in service. authenticated by 'user'.

        if (foundNewClubInvitations.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(foundNewClubInvitations);
    }

    // GET a club invitation and provide the club details.
    @ApiOperation(value = "getClubInvitationsEntity")
    @RequestMapping(value = "/d", method = RequestMethod.GET)
    public ResponseEntity<ClubInvitationsEntityDto> getSingleClubInvitation(
            @RequestHeader("Authorization") String token,
            @RequestParam("cId") final Long clubInvitationEntityId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        ClubInvitationsEntityDto foundNewClubInvitations = clubInvitationsEntityService.getClubInvitationsEntity(clubInvitationEntityId, user);

        // validation. ensure club invitation belongs to user since this is based just on clubInviationEntityId. see service for implementation.

        foundNewClubInvitations.getClub().setMembers(null);
        foundNewClubInvitations.getSender().setPassword(null);
        foundNewClubInvitations.getSender().setContactInfo(null);
        foundNewClubInvitations.getSender().setPublicProfile(null);
        foundNewClubInvitations.getSender().setContactInfo(null);

        return ResponseEntity.ok(foundNewClubInvitations);
    }

    // GET join a public club.
    @ApiOperation(value = "joinPublicClub")
    @RequestMapping(value = "/e", method = RequestMethod.GET)
    public ResponseEntity<ClubsEntity> joinPublicClub(
            @RequestHeader("Authorization") String token,
            @RequestParam("cId") final Long clubId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        return ResponseEntity.ok(clubInvitationsEntityService.joinPublicClub(user, clubId));
    }

    // POST a new club invitation
    @RequestMapping(value = "/b", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClubInvitationsEntityDto> createClubInvitationsEntity(
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

        // validation. see implementation in service.

        ClubInvitationsEntityDto savedClubInvitationsEntityDto = clubInvitationsEntityService.createClubInvitationsEntity(clubInvitationsEntityDto, user, clubId);

        savedClubInvitationsEntityDto.setSender(null);
        savedClubInvitationsEntityDto.setClub(null);

        return ResponseEntity.ok(savedClubInvitationsEntityDto);
    }

    // POST accept or decline a club invitation
    @RequestMapping(value = "/c", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClubInvitationsEntityDto> updateClubInvitationEntity(
            @Valid
            @RequestBody final ClubInvitationsEntityDto clubInvitationsEntityDto,
            @RequestHeader("Authorization") String token) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        ClubInvitationsEntityDto savedClubInvitationsEntityDto = clubInvitationsEntityService.updateClubInvitationsEntity(clubInvitationsEntityDto, user);
        return ResponseEntity.ok(savedClubInvitationsEntityDto);
    }

}
