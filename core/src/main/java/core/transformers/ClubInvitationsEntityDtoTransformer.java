package core.transformers;

import db.entity.ClubInvitationsEntity;
import model.ClubInvitationsEntityDto;
import org.springframework.stereotype.Component;

@Component
public class ClubInvitationsEntityDtoTransformer {

    public ClubInvitationsEntityDtoTransformer() {
    }

    // GET from db
    public ClubInvitationsEntityDto generate(final ClubInvitationsEntity clubsEntity) {
        if (clubsEntity == null || clubsEntity.getId() == null) {
            return null;
        }
        ClubInvitationsEntityDto dto = new ClubInvitationsEntityDto();
        dto.setId(clubsEntity.getId());
        dto.setCreated(clubsEntity.getCreated());
        dto.setSender(clubsEntity.getSender());
        dto.setReceiver(clubsEntity.getReceiver());
        dto.setStatus(clubsEntity.getStatus());
        dto.setClub(clubsEntity.getClub());
        return dto;
    }

    // POST
    public ClubInvitationsEntity generate(final ClubInvitationsEntityDto dto) {
        return new ClubInvitationsEntity(
                dto.getId(),
                dto.getCreated(),
                dto.getSender(),
                dto.getReceiver(),
                dto.getStatus(),
                dto.getClub()
        );
    }


}
