package core.transformers;

import db.entity.ClubsEntity;
import model.ClubsEntityDto;
import org.springframework.stereotype.Component;

@Component
public class ClubsEntityDtoTransformer {

    public ClubsEntityDtoTransformer() {
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
        dto.setDescription(clubsEntity.getDescription());
        dto.setMaxSize(clubsEntity.getMaxSize());
        dto.setCurrentSize(clubsEntity.getCurrentSize());
        dto.setAlpha(clubsEntity.getAlpha());
        dto.setMembers(clubsEntity.getMembers());
        return dto;
    }

    // POST
    public ClubsEntity generate(final ClubsEntityDto dto) {
        return new ClubsEntity(
                dto.getId(),
                dto.getCreated(),
                dto.getClubName(),
                dto.getDescription(),
                dto.getMaxSize(),
                dto.getCurrentSize(),
                dto.getAlpha(),
                dto.getMembers()
        );
    }


}
