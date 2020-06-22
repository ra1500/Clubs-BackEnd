package neural.controller;

import core.services.ClubInvitationsEntityService;
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

@RestController
@RequestMapping(value = "/api/i", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "ClubInvitationsEntity endpoints", tags = "ClubInvitationsEntity")
public class ClubInvitationsController extends AbstractRestController {

    private ClubInvitationsEntityService clubInvitationsEntityService;

    public ClubInvitationsController(ClubInvitationsEntityService clubInvitationsEntityService ) {
        this.clubInvitationsEntityService = clubInvitationsEntityService;
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
