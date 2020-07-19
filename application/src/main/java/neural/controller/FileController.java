package neural.controller;


import core.config.FileStorageProperties;
import core.services.FileStorageService;
import db.entity.FriendshipsEntity;
import db.entity.UserEntity;
import db.repository.FriendshipsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/files")
@EnableConfigurationProperties({FileStorageProperties.class})
public class FileController {

    private FileStorageService fileStorageService;
    private UserRepositoryDAO userEntityRepository;

    public FileController(FileStorageService fileStorageService, UserRepositoryDAO userEntityRepository) {
        this.fileStorageService = fileStorageService;
        this.userEntityRepository = userEntityRepository;
    }

    // GET image -
    @RequestMapping(value = "/f", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("fnm") final Long imageSelector,
            HttpServletRequest request) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        String imageSelected;

        if (imageSelector == 1) { imageSelected = user + "1.jpg";}
        else { imageSelected = null;}

        Resource resource = fileStorageService.loadFileAsResource(imageSelected);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {contentType = "application/octet-stream";}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }

    // POST image
    @RequestMapping(value = "/g", method = RequestMethod.POST)
    public ResponseEntity<String> postImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("fnm")  Long imgNumber,
            @RequestParam("image")  MultipartFile file){

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        String imageType = file.getContentType();
        if (imageType.equals("image/jpg") || imageType.equals("image/jpeg") ) {

            String fileName = fileStorageService.storeFile(file, user, imgNumber);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/images/")
                    .path(fileName)
                    .toUriString();

            String uploadedMessage = "uploaded";
            return ResponseEntity.ok(uploadedMessage);
        }
        else { return ResponseEntity.ok(file.getContentType()); }
    }

    // DELETE image
    @RequestMapping(value = "/h", method = RequestMethod.POST)
    public ResponseEntity<String> postImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("fnm")  Long imageSelector) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        String deletedMessage = fileStorageService.deleteFile( user, imageSelector);
        return ResponseEntity.ok(deletedMessage);
    }

    // GET image of single contact
    @RequestMapping(value = "/i", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadContactImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("fid") final Long friendId,
            HttpServletRequest request) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        Resource resource = fileStorageService.getFriendsProfileImage(user, friendId);

        // Try to determine file's content type
        String contentType = null;
        try {contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {contentType = "application/octet-stream";}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    // GET image of a contact's contact.
    @RequestMapping(value = "/j", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFriendOfFriendImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("fid") final Long friendId,
            HttpServletRequest request) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        Resource resource = fileStorageService.getFriendOfFriendImage(user, friendId);

        // Try to determine file's content type
        String contentType = null;
        try {contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {contentType = "application/octet-stream";}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }

    // GET image of single club member
    @RequestMapping(value = "/k", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadClubMemberImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("mId") final Long memberId,
            @RequestParam("cId") final Long clubId,
            HttpServletRequest request) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String user = values[0];

        Resource resource = fileStorageService.getClubMemberImage(user, clubId, memberId);

        // Try to determine file's content type
        String contentType = null;
        try {contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {contentType = "application/octet-stream";}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    // GET profile image Public Profile.
    @RequestMapping(value = "/pp{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadPublicProfileImage(
            @RequestParam("id") final String userName,
            HttpServletRequest request) {

            String imageSelected;
            UserEntity foundUserEntity = userEntityRepository.findOneByUserName(userName);
             if (foundUserEntity == null) { imageSelected = null;}
             else if (foundUserEntity.getPublicProfile().equals("Public") ) {imageSelected = userName + "1.jpg"; }
             else { imageSelected = null;}

            Resource resource = fileStorageService.loadFileAsResource(imageSelected);

            // Try to determine file's content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
            }

            // Fallback to the default content type if type could not be determined
            if(contentType == null) {contentType = "application/octet-stream";}

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        }

}
