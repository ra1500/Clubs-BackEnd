package core.services;

import core.transformers.MessagesEntityDtoTransformer;
import db.entity.MessagesEntity;
import db.entity.UserEntity;
import db.repository.MessagesRepositoryDAO;
import db.repository.UserRepositoryDAO;
import model.MessagesEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

@Service
public class MessagesEntityService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MessagesRepositoryDAO messagesRepositoryDAO;
    private final MessagesEntityDtoTransformer messagesEntityDtoTransformer;
    private final UserRepositoryDAO userRepositoryDAO;

    public MessagesEntityService(final MessagesRepositoryDAO messagesRepositoryDAO,
                              final MessagesEntityDtoTransformer messagesEntityDtoTransformer, final UserRepositoryDAO userRepositoryDAO) {
        this.messagesRepositoryDAO = messagesRepositoryDAO;
        this.messagesEntityDtoTransformer = messagesEntityDtoTransformer;
        this.userRepositoryDAO = userRepositoryDAO;
    }

        // GET
        public MessagesEntityDto getMessagesEntity(final Long messagesEntityId) {
            return messagesEntityDtoTransformer.generate(messagesRepositoryDAO.findOneById(messagesEntityId));
        }

    // POST a message
    public MessagesEntityDto createMessagesEntity(final MessagesEntityDto messagesEntityDto, String userName) {
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        messagesEntityDto.setSender(foundUserEntity);

        // currently limiting to posting 1,000 messages per day
        // this is not quite perfect since 'receiverId' can refer to clubId or userId and therefore may count up more than intended.
        Date yesterday = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
        Set<MessagesEntity> messagesOneDay = messagesRepositoryDAO.getAllWithinOneDay(foundUserEntity, messagesEntityDto.getReceiverId(), yesterday);
        if ( messagesOneDay.size() > 1000   ) { messagesEntityDto.setMessage(  "Sorry, over daily limit of messaging"   ); return messagesEntityDto; };

        messagesEntityDto.setRedFlag(new Long(0));
        MessagesEntity messagesEntity = messagesRepositoryDAO.saveAndFlush(messagesEntityDtoTransformer.generate(messagesEntityDto));
        return messagesEntityDtoTransformer.generate(messagesEntity);
    }

}
