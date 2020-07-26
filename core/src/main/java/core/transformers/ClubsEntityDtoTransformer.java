package core.transformers;

import db.entity.ClubsEntity;
import db.entity.UserEntity;
import db.entity.VotesEntity;
import db.repository.VotesRepositoryDAO;
import model.ClubsEntityDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
public class ClubsEntityDtoTransformer {

    private VotesRepositoryDAO votesRepositoryDAO;

    public ClubsEntityDtoTransformer(VotesRepositoryDAO votesRepositoryDAO) {
        this.votesRepositoryDAO = votesRepositoryDAO;
    }

    // GET from db
    public ClubsEntityDto generate(final ClubsEntity clubsEntity) {
        if (clubsEntity == null || clubsEntity.getId() == null) {
            return null;
        }

        ClubsEntityDto dto = new ClubsEntityDto();

        dto.setId(clubsEntity.getId());
        dto.setCreated(clubsEntity.getCreated());
        dto.setClubName(clubsEntity.getClubName());
        dto.setFounder(clubsEntity.getFounder());
        dto.setDescription(clubsEntity.getDescription());
        dto.setMaxSize(clubsEntity.getMaxSize());
        if ( clubsEntity.getMaxSize().equals(null) ) { dto.setCurrentSize(new Long(20)); }
        else { dto.setCurrentSize( clubsEntity.getCurrentSize()); };
        dto.setAlpha(clubsEntity.getAlpha());
        dto.setMembers(clubsEntity.getMembers());
        dto.setVotes(clubsEntity.getVotes());

        //dto.setBetaCount(

        //);


        //dto.setBetaMember();


        return dto;
    }

    // POST
    public ClubsEntity generate(final ClubsEntityDto dto) {
        return new ClubsEntity(
                dto.getId(),
                dto.getCreated(),
                dto.getClubName(),
                dto.getFounder(),
                dto.getDescription(),
                dto.getMaxSize(),
                dto.getCurrentSize(),
                dto.getAlpha(),
                dto.getMembers(),
                dto.getVotes()
        );
    }


}
