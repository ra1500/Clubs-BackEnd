package neural.controller;

import core.services.MessagesEntityService;
import db.entity.MessagesEntity;
import db.repository.MessagesRepositoryDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.MessagesEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/m", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "MessagesEntity endpoints", tags = "MessagesEntity")
public class MessagesController extends AbstractRestController {

    private MessagesEntityService messagesEntityService;
    private MessagesRepositoryDAO messagesRepositoryDAO;

    public MessagesController(MessagesEntityService messagesEntityService, MessagesRepositoryDAO messagesRepositoryDAO ) {
        this.messagesEntityService = messagesEntityService;
        this.messagesRepositoryDAO = messagesRepositoryDAO;
    }

    // GET a single message *** Not used....
    @ApiOperation(value = "getMessagesEntity")
    @RequestMapping(value = "/a{mId}", method = RequestMethod.GET)
    public ResponseEntity<MessagesEntityDto> getMessagesEntity(
            @RequestHeader("Authorization") String token,
            //@PathVariable("cId") final Long clubsEntityId) {
            @RequestParam("mId") final Long messagesEntityId) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        MessagesEntityDto messagesEntityDto = messagesEntityService.getMessagesEntity(messagesEntityId );
        if (messagesEntityDto == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(messagesEntityDto);
    }

    // GET list of messages for a club
    @ApiOperation(value = "getMessagesEntity")
    @RequestMapping(value = "/c{cId}", method = RequestMethod.GET)
    public ResponseEntity<Set<MessagesEntity>> getClubMessages(
            @RequestHeader("Authorization") String token,
            //@PathVariable("cId") final Long clubsEntityId) {
            @RequestParam("cId") final Long clubsEntityId) {
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        Set<MessagesEntity> clubMessages = messagesRepositoryDAO.getClubMessages(clubsEntityId);

        // check that user is in club (validation)
        // for loop to clean Set data
        // if empty return empty set.

        return ResponseEntity.ok(clubMessages);
    }

    // POST a new message
    @RequestMapping(value = "/b", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessagesEntityDto> createMessagesEntity(
            @RequestHeader("Authorization") String token,
            @Valid
            @RequestBody
            final MessagesEntityDto messagesEntityDto) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        MessagesEntityDto savedMessagesEntityDto = messagesEntityService.createMessagesEntity(messagesEntityDto, user);

        return ResponseEntity.ok(savedMessagesEntityDto);
    }


}
