package core.transformers;

import db.entity.VotesEntity;
import model.ClubsEntityDto;
import model.VotesEntityDto;
import org.springframework.stereotype.Component;

@Component
public class VotesEntityDtoTransformer {

    public VotesEntityDtoTransformer() {
    }

    // GET from db
    public VotesEntityDto generate(final VotesEntity votesEntity) {
        if (votesEntity == null || votesEntity.getId() == null) {
            return null;
        }
        VotesEntityDto dto = new VotesEntityDto();
        dto.setId(votesEntity.getId());
        dto.setCreated(votesEntity.getCreated());
        dto.setClub(votesEntity.getClub());
        dto.setVoter(votesEntity.getVoter());
        dto.setVoteType(votesEntity.getVoteType());
        dto.setVoteCast(votesEntity.getVoteCast());
        return dto;
    }

    // POST
    public VotesEntity generate(final VotesEntityDto dto) {
        return new VotesEntity(
                dto.getId(),
                dto.getCreated(),
                dto.getClub(),
                dto.getVoter(),
                dto.getVoteType(),
                dto.getVoteCast()
        );
    }


}
