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

@RestController
@RequestMapping(value = "/api/c", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "ClubsEntity endpoints", tags = "ClubsEntity")
public class ClubsController extends AbstractRestController {

    private ClubsEntityService clubsEntityService;

    public ClubsController(ClubsEntityService clubsEntityService ) {
        this.clubsEntityService = clubsEntityService;
    }

    // GET
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

        ClubsEntityDto clubsEntityDto = clubsEntityService.getClubsEntity(clubsEntityId );
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

        clubsEntityService.userQuitClub(user, clubId);

        String quitClubMessage = "quit club command";
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

}
