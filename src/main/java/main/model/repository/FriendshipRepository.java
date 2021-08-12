package main.model.repository;

import main.model.entity.Friendship;
import main.model.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FriendshipRepository extends CrudRepository<Friendship, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM friendship " +
            "WHERE (src_user_id = :deleteUserId AND dst_user_id = :currentUser) " +
            "OR (src_user_id = :currentUser AND dst_user_id = :deleteUserId) " +
            "AND status = 'FRIEND' ", nativeQuery = true)
    void deleteFriend(@Param("currentUser") Integer userId, @Param("deleteUserId") Integer deleteUserId);

    @Query("FROM Friendship WHERE (srcUser =:src AND dstUser =:dst)  OR (srcUser = :dst AND dstUser = :src) ")
    Optional<Friendship> findFriendshipForUser(@Param("src") User srcUser, @Param("dst") User dstUser);

}
