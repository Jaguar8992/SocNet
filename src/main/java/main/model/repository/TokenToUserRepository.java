package main.model.repository;

import main.model.entity.TokenToUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenToUserRepository extends CrudRepository<TokenToUser, Integer> {
    //Удаление устаревших токенов
    @Query(value = "SELECT * FROM token2user WHERE time < DATE_SUB(:time, INTERVAL 1 HOUR)", nativeQuery = true)
    List<TokenToUser> selectExpiredToken(@Param("time") Date time);

    Optional<TokenToUser> findByToken(String token);
}
