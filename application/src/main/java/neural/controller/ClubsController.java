package neural.controller;

import core.services.ClubsEntityService;
import db.entity.ClubsEntity;
import db.repository.ClubsRepositoryDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.ClubsEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/c", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "ClubsEntity endpoints", tags = "ClubsEntity")
public class ClubsController extends AbstractRestController {

    private ClubsEntityService clubsEntityService;
    private final ClubsRepositoryDAO clubsRepositoryDAO;

    public ClubsController(ClubsEntityService clubsEntityService, final ClubsRepositoryDAO clubsRepositoryDAO ) {
        this.clubsEntityService = clubsEntityService; this.clubsRepositoryDAO = clubsRepositoryDAO;
    }

    // GET a club (get it's info/details and members list sans this user).
    @ApiOperation(value = "getClubsEntity")
    @RequestMapping(value = "/a{cId}", method = RequestMethod.GET)
    public ResponseEntity<ClubsEntityDto> getClubsEntity(
            @RequestHeader("Authorization") String token,
            //@PathVariable("cId") final Long clubsEntityId) {
            @RequestParam("cId") final Long clubsEntityId) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        ClubsEntityDto clubsEntityDto = clubsEntityService.getClubsEntity(clubsEntityId, user );

        // validation. ensure user is in club before providing details. see implementation in service.

        clubsEntityDto.getMembers().removeIf(i -> i.getUserName().equals(user));

        if (clubsEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }

        return ResponseEntity.ok(clubsEntityDto);
    }

    // GET Quit club (remove user from club)
    @RequestMapping(value = "/c{cId}", method = RequestMethod.GET)
    public ResponseEntity<String> quitClubsEntity(
            @RequestHeader("Authorization") String token,
            @RequestParam("cId") final Long clubId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        // validation. in service.

        String quitClubMessage = clubsEntityService.userQuitClub(user, clubId);

        //String quitClubMessage = "Club removed.";
        return ResponseEntity.ok(quitClubMessage);
    }

    // POST a NEW club
    @RequestMapping(value = "/b", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClubsEntityDto> createClubsEntity(
            @RequestHeader("Authorization") String token,
            @Valid
            @RequestBody
            final ClubsEntityDto clubsEntityDto) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        // validation. in service.

        ClubsEntityDto savedClubsEntityDto = clubsEntityService.createClubsEntity(clubsEntityDto, user);
        savedClubsEntityDto.setMembers(null);

        return ResponseEntity.ok(savedClubsEntityDto);
    }



    // POST edit/update a clubsEntity (alpha only).
    @RequestMapping(value = "/d", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClubsEntityDto> updateClubsEntity(
            @RequestHeader("Authorization") String token,
            @Valid
            @RequestBody
            final ClubsEntityDto clubsEntityDto) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        ClubsEntityDto savedClubsEntityDto = clubsEntityService.updateClubsEntity(clubsEntityDto, user);
        return ResponseEntity.ok(savedClubsEntityDto);
    }

    // GET alpha delete/remove a member and member's messages
    @RequestMapping(value = "/e", method = RequestMethod.GET)
    public ResponseEntity<ClubsEntityDto> removeMember(
            @RequestHeader("Authorization") String token,
            @RequestParam("cId") final Long clubId,
            @RequestParam("mId") final Long memberId,
            final ClubsEntityDto clubsEntityDto) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        ClubsEntityDto savedClubsEntityDto = clubsEntityService.removeMember(user, memberId, clubId);
        return ResponseEntity.ok(savedClubsEntityDto);
    }

    // GET alpha delete a member and member's messages
    @RequestMapping(value = "/f", method = RequestMethod.GET)
    public ResponseEntity<ClubsEntityDto> changeAlpha(
            @RequestHeader("Authorization") String token,
            @RequestParam("cId") final Long clubId,
            @RequestParam("mId") final Long memberId,
            final ClubsEntityDto clubsEntityDto) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        ClubsEntityDto savedClubsEntityDto = clubsEntityService.changeAlpha(user, memberId, clubId);
        return ResponseEntity.ok(savedClubsEntityDto);
    }

    // GET Set of public clubs.
    @ApiOperation(value = "getPublicClubs")
    @RequestMapping(value = "/g", method = RequestMethod.GET)
    public ResponseEntity<Set<ClubsEntity>> getPublicClubs(
            @RequestHeader("Authorization") String token)               {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        Set<ClubsEntity> foundPublicClubs = clubsRepositoryDAO.findAllByClubMode(2L);
        for (ClubsEntity x : foundPublicClubs) {
            x.setFounder(null);
            x.setHeadline1(null); x.setHeadline2(null); x.setHeadline3(null); x.setHeadline4(null); x.setHeadline5(null);
            x.setMembers(null);
            x.setVotes(null);
        }

        if (foundPublicClubs.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(foundPublicClubs);
    }

}
