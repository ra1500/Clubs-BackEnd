package core.services;

import core.transformers.PermissionsEntityDtoTransformer;
import db.entity.PermissionsEntity;
import db.repository.PermissionsRepositoryDAO;
import model.PermissionsEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PermissionsEntityService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PermissionsRepositoryDAO permissionsRepositoryDAO;

    private final PermissionsEntityDtoTransformer permissionsEntityDtoTransformer;

    public PermissionsEntityService(final PermissionsRepositoryDAO permissionsRepositoryDAO, final PermissionsEntityDtoTransformer permissionsEntityDtoTransformer) {
        this.permissionsRepositoryDAO = permissionsRepositoryDAO;
        this.permissionsEntityDtoTransformer = permissionsEntityDtoTransformer;
    }

    // GET
    public PermissionsEntityDto getPermissionsEntity(final String userName, final String auditee, Long questinSetVersion) {
        return permissionsEntityDtoTransformer.generate(permissionsRepositoryDAO.findOneByUserNameAndAuditeeAndQuestionSetVersion(userName, auditee, questinSetVersion));
    }

    // GET  (not currently used since network level profile permission currently sits in FriendshipsEntity
    //public PermissionsEntityDto getPermissionsEntity(final String userName, String auditee) {
    //    return permissionsEntityDtoTransformer.generate(permissionsRepositoryDAO.findOneByUserNameAndAuditee(userName, auditee));
    //}

    // POST
    public PermissionsEntityDto createPermissionsEntity(final PermissionsEntityDto permissionsEntityDto) {
        PermissionsEntity permissionsEntity = permissionsRepositoryDAO.saveAndFlush(permissionsEntityDtoTransformer.generate(permissionsEntityDto));
        return permissionsEntityDtoTransformer.generate(permissionsEntity);
    }

    // PATCH (not currently used, updates InNetwork Permission
    public PermissionsEntityDto patchPermissionsEntity(final PermissionsEntityDto permissionsEntityDto) {
        PermissionsEntity permissionsEntity = permissionsRepositoryDAO.findOneByUserNameAndAuditeeAndQuestionSetVersion(permissionsEntityDto.getUserName(),
                permissionsEntityDto.getAuditee(), permissionsEntityDto.getQuestionSetVersion());
        permissionsEntity.setNetworkProfilePagePermission(permissionsEntityDto.getNetworkProfilePagePermission());
        permissionsRepositoryDAO.save(permissionsEntity);
        return permissionsEntityDtoTransformer.generate(permissionsEntity);
    }
}
