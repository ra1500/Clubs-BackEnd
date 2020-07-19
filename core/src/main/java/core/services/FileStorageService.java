package core.services;

import core.config.FileStorageProperties;
import db.entity.ClubsEntity;
import db.entity.FriendshipsEntity;
import db.entity.UserEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.FriendshipsRepositoryDAO;
import db.repository.UserRepositoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private FriendshipsRepositoryDAO friendshipsRepositoryDAO;
    private UserRepositoryDAO userRepositoryDAO;
    private ClubsRepositoryDAO clubsRepositoryDAO;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties, FriendshipsRepositoryDAO friendshipsRepositoryDAO,
                              UserRepositoryDAO userRepositoryDAO, ClubsRepositoryDAO clubsRepositoryDAO) {
        this.friendshipsRepositoryDAO = friendshipsRepositoryDAO;
        this.userRepositoryDAO = userRepositoryDAO;
        this.clubsRepositoryDAO = clubsRepositoryDAO;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            //throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    // Get a file
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            String defaultImage = "profiledefault.jpg";
            Path defaultFilePath = this.fileStorageLocation.resolve(defaultImage).normalize();
            Resource defaultResource = new UrlResource(defaultFilePath.toUri());

            if(resource.exists()) {
                return resource;
            } else if(defaultResource.exists()) {
                return defaultResource; }
            else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }

        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    // Post/save a file
    public String storeFile(MultipartFile file, String user, Long imgNumber) {
        // Normalize file name
        String fileToSave = user + imgNumber + ".jpg";
        String fileName = StringUtils.cleanPath(fileToSave);

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    // Delete a file
    public String deleteFile(String user, Long imgNumber) {
        // Normalize file name
        String fileToDelete = user + imgNumber + ".jpg";
        String fileName = StringUtils.cleanPath(fileToDelete);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.deleteIfExists(targetLocation);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileName, ex);
            }
    }

    // Get a friend's profile image
    public Resource getFriendsProfileImage(String user, Long friendId) {

        // validate that contact is indeed a friend of the user
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);
        Set<FriendshipsEntity> foundFriendshipsEntities = foundUserEntity.getFriendsSet();
        foundFriendshipsEntities.removeIf(i -> !i.getId().equals(friendId));
        String defaultImage = "profiledefault.jpg";
        Path defaultFilePath = this.fileStorageLocation.resolve(defaultImage).normalize();
        try {
        Resource defaultResource = new UrlResource(defaultFilePath.toUri());
        if (foundFriendshipsEntities.isEmpty()) { return defaultResource; }

        // TODO: also check that friendship isn't 'removed' from other side of friendship (so that people can't still see the profile image of someonw who removed them).

        FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendId);
        String imageSelected = foundFriendshipsEntity.getFriend() + "1.jpg";

            Path filePath = this.fileStorageLocation.resolve(imageSelected).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            String defaultImage2 = "profiledefault.jpg";
            Path defaultFilePath2 = this.fileStorageLocation.resolve(defaultImage2).normalize();
            Resource defaultResource2 = new UrlResource(defaultFilePath2.toUri());

            if(resource.exists()) {return resource;
            } else if(defaultResource2.exists()) {return defaultResource2; }
            else {throw new MyFileNotFoundException("File not found ");
            }

        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found ", ex);
        }
    }

    // Get a friend of friend profile image
    public Resource getFriendOfFriendImage(String user, Long friendId) {

        // TODO: validate that contact is indeed a friend of a friend of the user
        //UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);
        //Set<FriendshipsEntity> foundFriendshipsEntities = foundUserEntity.getFriendsSet();
        //foundFriendshipsEntities.removeIf(i -> !i.getId().equals(friendId));
        String defaultImage = "profiledefault.jpg";
        Path defaultFilePath = this.fileStorageLocation.resolve(defaultImage).normalize();
        try {
            Resource defaultResource = new UrlResource(defaultFilePath.toUri());
            //if (foundFriendshipsEntities.isEmpty()) { return defaultResource; }

            // TODO: also check that friendship isn't 'removed' from other side of friendship (so that people can't still see the profile image of someonw who removed them).

            FriendshipsEntity foundFriendshipsEntity = friendshipsRepositoryDAO.findOneById(friendId);
            String imageSelected = foundFriendshipsEntity.getFriend() + "1.jpg";

            Path filePath = this.fileStorageLocation.resolve(imageSelected).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            String defaultImage2 = "profiledefault.jpg";
            Path defaultFilePath2 = this.fileStorageLocation.resolve(defaultImage2).normalize();
            Resource defaultResource2 = new UrlResource(defaultFilePath2.toUri());

            if(resource.exists()) {return resource;
            } else if(defaultResource2.exists()) {return defaultResource2; }
            else {throw new MyFileNotFoundException("File not found ");
            }

        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found ", ex);
        }
    }

    // Get a friend's profile image
    public Resource getClubMemberImage(String user, Long clubId ,Long memberId) {

        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(user);
        UserEntity foundMemberUserEntity = userRepositoryDAO.findOneById(memberId);
        ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(clubId);
        Set<UserEntity> clubMembers = foundClubsEntity.getMembers();

        String defaultImage = "profiledefault.jpg";
        Path defaultFilePath = this.fileStorageLocation.resolve(defaultImage).normalize();
        try {
            Resource defaultResource = new UrlResource(defaultFilePath.toUri());
            if ( !clubMembers.contains(foundUserEntity) ) { return defaultResource; } // validation: is user in club?
            if ( !clubMembers.contains(foundMemberUserEntity) ) { return defaultResource; } // validation: is member also in club?

            String imageSelected = foundMemberUserEntity.getUserName() + "1.jpg";

            Path filePath = this.fileStorageLocation.resolve(imageSelected).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            String defaultImage2 = "profiledefault.jpg";
            Path defaultFilePath2 = this.fileStorageLocation.resolve(defaultImage2).normalize();
            Resource defaultResource2 = new UrlResource(defaultFilePath2.toUri());

            if(resource.exists()) {return resource;
            } else if(defaultResource2.exists()) { return defaultResource2; }
            else {throw new MyFileNotFoundException("File not found ");
            }

        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found ", ex);
        }
    }


}
