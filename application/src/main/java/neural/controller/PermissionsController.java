package neural.controller;

import core.services.PermissionsEntityService;
import db.entity.FriendshipsEntity;
import db.entity.PermissionsEntity;
import db.entity.QuestionSetVersionEntity;
import db.entity.UserEntity;
import db.repository.FriendshipsRepositoryDAO;
import db.repository.PermissionsRepositoryDAO;
import db.repository.UserAnswersRepositoryDAO;
import db.repository.UserRepositoryDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.PermissionsEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/prm", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "Permissions endpoints", tags = "PermissionsEntity")
public class PermissionsController extends AbstractRestController {

    private PermissionsEntityService permissionsEntityService;
    private PermissionsRepositoryDAO permissionsRepositoryDAO; // used for delete. shortcut to the repository.
    private FriendshipsRepositoryDAO friendshipsRepositoryDAO; // shortcut. TODO delete this and go through transformer
    private UserRepositoryDAO userRepositoryDAO; // used for checking Public Profile permission to show public page
    private UserAnswersRepositoryDAO userAnswersRepositoryDAO; // used for 'Delete' to also delete all related audit answers.

    public PermissionsController(PermissionsEntityService permissionsEntityService, PermissionsRepositoryDAO permissionsRepositoryDAO, FriendshipsRepositoryDAO friendshipsRepositoryDAO, UserRepositoryDAO userRepositoryDAO, UserAnswersRepositoryDAO userAnswersRepositoryDAO) {
        this.permissionsEntityService = permissionsEntityService;
        this.permissionsRepositoryDAO = permissionsRepositoryDAO;
        this.friendshipsRepositoryDAO = friendshipsRepositoryDAO;
        this.userRepositoryDAO = userRepositoryDAO;
        this.userAnswersRepositoryDAO = userAnswersRepositoryDAO;
    }

    // GET not used??
    //@ApiOperation(value = "permissionsEntity")
    //@RequestMapping(value = "/{au}/{qsId}", method = RequestMethod.GET)
    //public ResponseEntity<PermissionsEntityDto> getPermissionsEntity(
    //        @RequestHeader("Authorization") String token,
    //        @PathVariable("au") final String auditee,
    //        @PathVariable("qId") final Long questionSetVersionEntityId) {
    //    String base64Credentials = token.substring("Basic".length()).trim();
    //    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
    //    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
    //    final String[] values = credentials.split(":", 2);
    //    String user = values[0];

    //    PermissionsEntityDto permissionsEntityDto = permissionsEntityService.getPermissionsEntity(user, auditee, questionSetVersionEntityId);
    //    if (permissionsEntityDto == null) {
    //        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    //    }
    //    return ResponseEntity.ok(permissionsEntityDto);
    //}

    // POST/PATCH  SCORE permission. (Audit '16' permission below).
    @RequestMapping(value = "/sc/d{qsId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionsEntityDto> createPermissionsEntity(
            @Valid
            @RequestBody final PermissionsEntityDto permissionsEntityDto,
            @RequestHeader("Authorization") String token,
            @RequestParam("qsId") final Long questionSetVersionEntityId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        // userName from token
        permissionsEntityDto.setUserName(user);

        PermissionsEntityDto savedPermissionsEntityDto = permissionsEntityService.createPermissionsEntity(permissionsEntityDto, questionSetVersionEntityId, user);
        return ResponseEntity.ok(savedPermissionsEntityDto);
    }

    // POST/PATCH  Audit Score '16' .
    @RequestMapping(value = "/sc/q{qsId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionsEntityDto> createPermissionsEntityAuditScore(
            @Valid
            @RequestBody final PermissionsEntityDto permissionsEntityDto,
            @RequestHeader("Authorization") String token,
            @RequestParam("qsId") final Long questionSetVersionEntityId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        // userName from token
        permissionsEntityDto.setUserName(user);

        PermissionsEntityDto savedPermissionsEntityDto = permissionsEntityService.createPermissionsEntityAuditScore(permissionsEntityDto, questionSetVersionEntityId, user);
        return ResponseEntity.ok(savedPermissionsEntityDto);
    }

    // POST/PATCH  post to let a group of connections view new user Qset
    @RequestMapping(value = "/sc/n{qsId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createFriendsViewQsetsPermissionsEntities(
            @Valid
            @RequestBody final PermissionsEntityDto permissionsEntityDto,
            @RequestHeader("Authorization") String token,
            @RequestParam("qsId") final Long questionSetVersionEntityId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        String invitationMessage = permissionsEntityService.createQsetViewPermissionEntities(permissionsEntityDto.getTypeNumber(), questionSetVersionEntityId, user);
        invitationMessage = "{\"invitationMessage\":" + "\"" + invitationMessage + "\"" + "}";
        return ResponseEntity.ok(invitationMessage);
    }

    // POST/PATCH  post to let an individual connection view new user Qset
    @RequestMapping(value = "/sc/o{qsId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createIndividualViewQsetsPermissionsEntities(
            @Valid
            @RequestBody final PermissionsEntityDto permissionsEntityDto,
            @RequestHeader("Authorization") String token,
            @RequestParam("qsId") final Long questionSetVersionEntityId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        String invitationMessage = permissionsEntityService.createIndividualQsetViewPermissionEntity(questionSetVersionEntityId, user, permissionsEntityDto.getUserName());
        invitationMessage = "{\"invitationMessage\":" + "\"" + invitationMessage + "\"" + "}";
        return ResponseEntity.ok(invitationMessage);
    }

    // GET. QSets & user scores for Private Profile Page.
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/dr", method = RequestMethod.GET)
    public ResponseEntity<Set<PermissionsEntity>> getPermissionsEntityUserScorePrivateProfilePage(
            @RequestHeader("Authorization") String token) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        //PermissionsEntityDto permissionsEntityDto = permissionsEntityService.getPermissionsEntity(user);

        // TODO: Create a set of Dto's in the transformer and return them as a Set instead of direct to repository.
        Set<PermissionsEntity> permissionsEntities = permissionsRepositoryDAO.getPrivateProfilePageQsets(user);
        for (PermissionsEntity x : permissionsEntities) {
            x.getQuestionSetVersionEntity().setCreated(null); x.setViewGroup(null);
        }

        if (permissionsEntities.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(permissionsEntities);
    }

    // GET. QSets for Main/'Scores' page.
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/dw", method = RequestMethod.GET)
    public ResponseEntity<Set<PermissionsEntity>> getPermissionsEntityScoresPage(
            @RequestHeader("Authorization") String token) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        //PermissionsEntityDto permissionsEntityDto = permissionsEntityService.getPermissionsEntity(user);

        // TODO: Create a set of Dto's in the transformer and return them as a Set instead of direct to repository.
        Set<PermissionsEntity> permissionsEntities = permissionsRepositoryDAO.getScoresPageQsets();

        if (permissionsEntities.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(permissionsEntities);
    }

    // GET. QSets & user scores for Public Profile Page.
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/dc{id}", method = RequestMethod.GET)
    public ResponseEntity<Set<PermissionsEntity>> getPermissionsEntityUserScorePublicProfilePage(
            @RequestParam("id") final String userName) {
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        if (foundUserEntity == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }

        Set<PermissionsEntity> permissionsEntities = permissionsRepositoryDAO.getPublicProfilePageQsets(userName);
        if (foundUserEntity.getPublicProfile().equals("Public") && !permissionsEntities.isEmpty() ) {
            return ResponseEntity.ok(permissionsEntities);
        }
        else { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
    }

    // GET. scores for a Network Contact (checks UserEntity PublicProfile permission & FriendshipsEntity Privacy permission)
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/df{ctc}", method = RequestMethod.GET)
    public ResponseEntity<Set<PermissionsEntity>> getPermissionsEntityUserScoreNetworkContact(
            @RequestHeader("Authorization") String token,
            @RequestParam("ctc") final Long friendId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        // TODO: validate again that incoming friendID is indeed a friend of user.

        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendId);
        UserEntity friendsUserEntity = userRepositoryDAO.findOneByUserName(foundFriendshipsEntity.getFriend());

        if (friendsUserEntity.getPublicProfile() == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        else if (friendsUserEntity.getPublicProfile().equals("Public") || friendsUserEntity.getPublicProfile().equals("Network") ) {
            // get the visibilityPermission from the friend
            FriendshipsEntity friendsFriendshipsEntity = friendshipsRepositoryDAO.findOneByUserEntityIdAndFriend( friendsUserEntity.getId(),user);

            if (friendsFriendshipsEntity.getVisibilityPermission().equals("Yes")) {
            Set<PermissionsEntity> permissionsEntities = permissionsRepositoryDAO.getNetworkContactScores(foundFriendshipsEntity.getFriend());
              if (permissionsEntities.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
              else { return ResponseEntity.ok(permissionsEntities); }
            }
            else { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        }
        else { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
    }

    // GET. Private Profile page self-made Qsets. Also for 'Scores'/'My Sets'.
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/du", method = RequestMethod.GET)
    public ResponseEntity<Set<PermissionsEntity>> getPermissionsEntityPrivateProfilePage(
            @RequestHeader("Authorization") String token) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        // TODO: Create a set of Dto's in the transformer and return them as a Set instead of direct to repository.
        Set<PermissionsEntity> permissionsEntities = permissionsRepositoryDAO.getPrivateProfilePageSelfMadeQsets(user);

        for (PermissionsEntity x : permissionsEntities) {
            x.setViewGroup(null);
        }

        if (permissionsEntities.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(permissionsEntities);
    }

    // GET. For Answers - 'Network Sets'. (no 'removed' contact's qsets)
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/dv", method = RequestMethod.GET)
    public ResponseEntity<Set<PermissionsEntity>> getPermissionsEntityNetworkQsets(
            @RequestHeader("Authorization") String token) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        Set<PermissionsEntity> permissionsEntities = permissionsRepositoryDAO.getNetworkCreatedQsets(user);
        if (permissionsEntities.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);
        Set<FriendshipsEntity> foundFriendshipsEntities = foundUserEntity.getFriendsSet();
        foundFriendshipsEntities.removeIf(i -> i.getConnectionStatus().equals("Connected"));  // black listed friends 'removed' or 'pending'
        for (FriendshipsEntity x : foundFriendshipsEntities ) {
            permissionsEntities.removeIf(i -> i.getAuditee().equals(x.getFriend())); }

        if (permissionsEntities.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(permissionsEntities);
    }

    // GET. Contact NetworkPages Qsets of a friend.
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/dy{fid}", method = RequestMethod.GET)
    public ResponseEntity<Set<PermissionsEntity>> getPermissionsEntityNetworkProfileQsets(
            @RequestHeader("Authorization") String token,
            @RequestParam("fid") final Long friendId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        String friend = friendshipsRepositoryDAO.findOneById(friendId).getFriend();
        Set<PermissionsEntity> permissionsEntities = permissionsRepositoryDAO.getNetworkProfilePageQsets(user, friend);

        if (permissionsEntities.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(permissionsEntities);
    }

    // Delete? Not used?
    // GET a single permission for a Qset for 'manageAudit'
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/dg{id}", method = RequestMethod.GET)
    public ResponseEntity<PermissionsEntityDto> getPermissionsEntityManageAudit(
            @RequestHeader("Authorization") String token,
            @RequestParam("id") final Long id) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        PermissionsEntityDto permissionsEntityDto = permissionsEntityService.getPermissionsEntity(id, user);

        if (permissionsEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(permissionsEntityDto);
    }

    // GET. ViewAudits
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/de{qsId}", method = RequestMethod.GET)
    public ResponseEntity<Set<PermissionsEntity>> getPermissionsEntityViewAudits(
            @RequestHeader("Authorization") String token,
            @RequestParam("qsId") final Long questionSetVersionEntityId) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        Set<PermissionsEntity> permissionsEntities = permissionsRepositoryDAO.getAudits(user, questionSetVersionEntityId);

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);
        Set<FriendshipsEntity> foundFriendshipsEntities = foundUserEntity.getFriendsSet();
        foundFriendshipsEntities.removeIf(i -> i.getConnectionStatus().equals("Connected"));  // black listed friends 'removed' or 'pending'
        for (FriendshipsEntity x : foundFriendshipsEntities ) {
            permissionsEntities.removeIf(i -> i.getUserName().equals(x.getFriend())); }

        if (permissionsEntities.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(permissionsEntities);
    }

    // GET. Alerts - Newly posted '16' audits posted.
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/dh", method = RequestMethod.GET)
    public ResponseEntity<Set<PermissionsEntity>> getPermissionsEntityNewAuditsPosted(
            @RequestHeader("Authorization") String token) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        Set<PermissionsEntity> permissionsEntities = permissionsEntityService.getPermissionsEntityNewAuditsPosted(user);

        if (permissionsEntities.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(permissionsEntities);
    }

    // POST/DELETE. Delete a score completely from permissionsEntity (and related audits).
    @ApiOperation(value = "permissionsEntity")
    @RequestMapping(value = "/sc/dl", method = RequestMethod.POST)
    public ResponseEntity<Integer> deletePermissionsEntityUserScorePrivateProfilePage(
            @Valid
            @RequestBody final PermissionsEntityDto permissionsEntityDto,
            @RequestHeader("Authorization") String token) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        PermissionsEntity foundPermissionsEntity = permissionsRepositoryDAO.findOneById(permissionsEntityDto.getId());
        QuestionSetVersionEntity foundQuestionSetVersionEntity = foundPermissionsEntity.getQuestionSetVersionEntity();

        // delete the score posted Permission
        Integer deletedScore = permissionsRepositoryDAO.deleteOneById(permissionsEntityDto.getId());
        if (deletedScore == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }

        // delete the '16' auditor scores posted permissions
        permissionsRepositoryDAO.deleteAllByAuditeeAndQuestionSetVersionEntityAndTypeNumber(user, foundQuestionSetVersionEntity, new Long(16));

        // delete all the related audit userAnswers
        userAnswersRepositoryDAO.deleteAllByAuditeeAndQuestionSetVersionEntity(user, foundQuestionSetVersionEntity);

        return ResponseEntity.ok(deletedScore);
    }
}