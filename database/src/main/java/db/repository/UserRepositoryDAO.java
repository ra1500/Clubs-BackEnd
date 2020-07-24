package db.repository;

import db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryDAO extends JpaRepository<UserEntity, Long> {

    UserEntity findOneByUserNameAndPassword(String userName, String password);
    UserEntity findOneByUserName(String userName);
    UserEntity findOneById(Long id);

}
