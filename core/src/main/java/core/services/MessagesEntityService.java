package core.services;

import core.transformers.MessagesEntityDtoTransformer;
import db.entity.ClubsEntity;
import db.entity.MessagesEntity;
import db.entity.UserEntity;
import db.repository.ClubsRepositoryDAO;
import db.repository.MessagesRepositoryDAO;
import db.repository.UserRepositoryDAO;
import model.MessagesEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class MessagesEntityService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MessagesRepositoryDAO messagesRepositoryDAO;
    private final MessagesEntityDtoTransformer messagesEntityDtoTransformer;
    private final UserRepositoryDAO userRepositoryDAO;
    private final ClubsRepositoryDAO clubsRepositoryDAO;

    public MessagesEntityService(final MessagesRepositoryDAO messagesRepositoryDAO,
                              final MessagesEntityDtoTransformer messagesEntityDtoTransformer, final UserRepositoryDAO userRepositoryDAO, ClubsRepositoryDAO clubsRepositoryDAO) {
        this.messagesRepositoryDAO = messagesRepositoryDAO;
        this.messagesEntityDtoTransformer = messagesEntityDtoTransformer;
        this.userRepositoryDAO = userRepositoryDAO;
        this.clubsRepositoryDAO = clubsRepositoryDAO;
    }

        // GET
        public MessagesEntityDto getMessagesEntity(final Long messagesEntityId) {
            return messagesEntityDtoTransformer.generate(messagesRepositoryDAO.findOneById(messagesEntityId));
        }

    // GET club messages
    public List<MessagesEntity> getClubMessages(final Long clubsEntityId, Integer pageNo) {

        // max# of pages allowed to pull
        if ( pageNo > 10 ) { pageNo = new Integer(10); };

        // max# of messages per ajax call is second parameter
        Pageable paging = PageRequest.of(pageNo, 200, Sort.by("id").descending()); // descending order, so that default page of '0' is last page.

        Slice<MessagesEntity> slicedResult = messagesRepositoryDAO.getClubMessages(clubsEntityId, paging); // using 'Slice' instead of 'Page' since no need for count

        List<MessagesEntity> clubMessagesList = slicedResult.getContent();
        //Comparator<MessagesEntity> byId = Comparator.comparingLong(MessagesEntity::getId); // TODO this does not work. Slice not sortable or something. figure out way to sort this ascending by id. for now, front-end sorts list.
        //clubMessagesList.sort(byId);  // also sorted redundantly on front-end

        return clubMessagesList;
    }

    // POST a message
    public MessagesEntityDto createMessagesEntity(final MessagesEntityDto messagesEntityDto, String userName) {
        UserEntity foundUserEntity = userRepositoryDAO.findOneByUserName(userName);
        messagesEntityDto.setSender(foundUserEntity);

        // validation. if a club message, is user in club.
        if ( messagesEntityDto.getReceiverType().equals(new Long(2)) ) {
            ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneById(messagesEntityDto.getReceiverId());
            if ( !foundUserEntity.getClubs().contains(foundClubsEntity) ) { messagesEntityDto.setMessage("error"); return messagesEntityDto; };
        }

        // validation. if a club member to club member message, are both members in the club.
        if ( messagesEntityDto.getReceiverType().equals(new Long(1)) ) {
            ClubsEntity foundClubsEntity = clubsRepositoryDAO.findOneByClubName(messagesEntityDto.getClubName());
            Set<UserEntity> members = foundClubsEntity.getMembers();
            UserEntity receiver = userRepositoryDAO.findOneById(messagesEntityDto.getReceiverId());
            if ( !(members.contains(foundUserEntity) && members.contains(receiver)) ) { messagesEntityDto.setMessage("error"); return messagesEntityDto; };
        }

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
