package db.repository;

import db.entity.ClubsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Repository
public interface ClubsRepositoryDAO extends JpaRepository<ClubsEntity, Long> {

    ClubsEntity findOneById(Long id);

    ClubsEntity findOneByClubName(String clubName);

    @Transactional
    Integer deleteOneById(Long id);
}
