package core.transformers;


import db.entity.MessagesEntity;
import model.MessagesEntityDto;
import org.springframework.stereotype.Component;

@Component
public class MessagesEntityDtoTransformer {

    public MessagesEntityDtoTransformer() {
    }

    // GET from db
    public MessagesEntityDto generate(final MessagesEntity messagesEntity) {
        if (messagesEntity == null || messagesEntity.getId() == null) {
            return null;
        }
        MessagesEntityDto dto = new MessagesEntityDto();
        dto.setId(messagesEntity.getId());
        dto.setCreated(messagesEntity.getCreated());
        dto.setMessage(messagesEntity.getMessage());
        dto.setSender(messagesEntity.getSender());
        dto.setReceiverType(messagesEntity.getReceiverType());
        dto.setReceiverId(messagesEntity.getReceiverId());
        return dto;
    }

    // POST
    public MessagesEntity generate(final MessagesEntityDto dto) {
        return new MessagesEntity(
                dto.getId(),
                dto.getCreated(),
                dto.getMessage(),
                dto.getSender(),
                dto.getReceiverType(),
                dto.getReceiverId()
        );
    }

}
