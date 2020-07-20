package neural.controller;

import core.services.VotesEntityService;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VotesEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import db.repository.VotesRepositoryDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.MessagesEntityDto;
import model.VotesEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "VotesEntity endpoints", tags = "VotesEntity")
public class VotesEntityController extends AbstractRestController {

    private VotesEntityService votesEntityService;
    private VotesRepositoryDAO votesRepositoryDAO;
    private ClubsRepositoryDAO clubsRepositoryDAO;
    private UserRepositoryDAO userRepositoryDAO;

    public VotesEntityController(VotesEntityService votesEntityService, VotesRepositoryDAO votesRepositoryDAO, ClubsRepositoryDAO clubsRepositoryDAO, UserRepositoryDAO userRepositoryDAO) {
        this.votesEntityService = votesEntityService;
        this.votesRepositoryDAO = votesRepositoryDAO;
        this.clubsRepositoryDAO = clubsRepositoryDAO;
        this.userRepositoryDAO = userRepositoryDAO;
    }

    // GET a user's votes for a particular club
    @ApiOperation(value = "getMessagesEntity")
    @RequestMapping(value = "/a{cId}", method = RequestMethod.GET)
    public ResponseEntity<Set<VotesEntity>> getVotesEntity(
            @RequestHeader("Authorization") String token,
            //@PathVariable("cId") final Long clubsEntityId) {
            @RequestParam("cId") final Long clubsEntityId) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubsEntityId);
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);

        Set<VotesEntity> userClubVotesSet = votesRepositoryDAO.findAllByVoterAndClub(foundUserEntity, foundClubsEntity);

        if (userClubVotesSet.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        for (VotesEntity x : userClubVotesSet) {
            x.setVoter(null);
        }

        return ResponseEntity.ok(userClubVotesSet);
    }

    // POST or update a new/existing vote
    @RequestMapping(value = "/b{cId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VotesEntityDto> createVotesEntity(
            @Valid
            @RequestBody final VotesEntityDto votesEntityDto,
            @RequestHeader("Authorization") String token,
            @RequestParam("cId") final Long clubId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        VotesEntityDto savedVotesEntityDto = votesEntityService.createVotesEntity(votesEntityDto, user, clubId);

        savedVotesEntityDto.setVoter(null);

        return ResponseEntity.ok(savedVotesEntityDto);
    }

}
