package neural.controller;

import core.services.VotesEntityService;
import db.entity.VoteCountsClubAlpha;
import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VotesEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import db.repository.VotesRepositoryDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.VotesEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Integer.valueOf;

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

        // escape if club not found
        if ( foundClubsEntity ==  null ) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };

        // validation. user is indeed in club
        if ( !foundClubsEntity.getMembers().contains(foundUserEntity) )  { return new ResponseEntity<>(HttpStatus.NO_CONTENT); };

        Set<VotesEntity> userClubVotesSet = votesRepositoryDAO.findAllByVoterAndClub(foundUserEntity, foundClubsEntity);

        if (userClubVotesSet.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        for (VotesEntity x : userClubVotesSet) {
            x.setVoter(null);
        }

        return ResponseEntity.ok(userClubVotesSet);
    }

    // GET the count of votes for club beta
    @ApiOperation(value = "getVoteCountsClubAlpha")
    @RequestMapping(value = "/c{cId}", method = RequestMethod.GET)
    public ResponseEntity<VoteCountsClubAlpha> getVoteCountsClubAlpha(
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

        // validation. user must be in club to get beta.
        if ( !foundUserEntity.getClubs().contains(foundClubsEntity) ) {
            VoteCountsClubAlpha oneMemberVoteCountsClubAlpha = new VoteCountsClubAlpha("error", new Integer(0) );
            return ResponseEntity.ok(oneMemberVoteCountsClubAlpha);
        }

        // TODO change the @Query to have a JOIN with VoteCountsclubAlpha and not have to do this mapping manually here.
        String[] voteCountsClubAlphas = votesRepositoryDAO.getAlphaVoteCounts(clubsEntityId);
        Set<VoteCountsClubAlpha>  setOfVotes = new HashSet<>();

        for (String x : voteCountsClubAlphas) {
            String[] y = x.split(",");
            setOfVotes.add(new VoteCountsClubAlpha( y[0], new Integer(y[1]) ));
        }

        // if only one member has votes, it is the alpha, so just break and return message 'none' for no beta existing.
        if ( setOfVotes.size() == 1 || setOfVotes.isEmpty()) {
            VoteCountsClubAlpha oneMemberVoteCountsClubAlpha = new VoteCountsClubAlpha("none", new Integer(0) );
            return ResponseEntity.ok(oneMemberVoteCountsClubAlpha);
        }

        // remove the alpha before getting the beta.
        setOfVotes.removeIf(i -> i.getVoteCast().equals(foundClubsEntity.getAlpha()));

        // get the beta. note if there is a tie in beta counts, this will just grab one from 'random' order in the Set.
        // maxCount must be > than 1 in order overcome the alpha
        int maxCount = 0;
        int currentCount;
        String beta = "";
        for (VoteCountsClubAlpha x : setOfVotes ) {
            currentCount = x.getCountVotesCast();
            if (currentCount > maxCount) {
                maxCount = currentCount;
                beta = x.getVoteCast();
            }
        }

        VoteCountsClubAlpha betaVoteCountsClubAlpha = new VoteCountsClubAlpha(beta, maxCount);

        return ResponseEntity.ok(betaVoteCountsClubAlpha);
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
