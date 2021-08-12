package main.model.repository;

import main.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    String QUERY_USER_CHECK_EMAIL_PASSWORD = "select * from user where email = :eml and password = :pass";

    @Query(value = QUERY_USER_CHECK_EMAIL_PASSWORD,
            nativeQuery = true)
    List<User> findUser(@Param("eml") String email, @Param("pass") String password);

    Optional<User> findUserById(Integer id);

    String QUERY_CHECK_BLOCKED = "select is_blocked from user where email = :eml";

    @Query(value = QUERY_CHECK_BLOCKED,
            nativeQuery = true)
    int countIsBlocked(@Param("eml") String email);

    String QUERY_USER_CHECK_EMAIL = "select count(*) from user where email = :eml";

    @Query(value = QUERY_USER_CHECK_EMAIL,
            nativeQuery = true)
    int countFindUserEmail(@Param("eml") String email);

    String QUERY_USER_CHECK_CODE = "select count(*) from user where confirmation_code = :code";

    @Query(value = QUERY_USER_CHECK_CODE,
            nativeQuery = true)
    int countFindUserCode(@Param("code") String code);

    @Query("FROM User WHERE email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    Page<User> findAllById(int id, Pageable pageable);

    @Query(value = "" +
            "SELECT u " +
            "FROM Friendship as myFriens " +
            "JOIN User u " +
            "ON u = myFriens.srcUser where myFriens.dstUser = :currentUser AND u.firstName = :firstName AND myFriens.status = 'REQUEST'")
    Page<User> getFriendRequestsByName(@Param("currentUser") User user, @Param("firstName") String firstName,
                                       Pageable pageable);

    @Query(value = "" +
            "SELECT u " +
            "FROM Friendship as myFriens " +
            "JOIN User u " +
            "ON u = myFriens.srcUser where myFriens.dstUser = :currentUser AND myFriens.status = 'REQUEST'")
    Page<User> getFriendRequestsAll(@Param("currentUser") User user, Pageable pageable);

    @Query(value = "" +
            "SELECT DISTINCT u " +
            "FROM Friendship as myFriensd " +
            "INNER JOIN User u " +
            "ON (CASE WHEN myFriensd.srcUser = :currentUser " +
            "      THEN myFriensd.dstUser " +
            "    ELSE myFriensd.srcUser END) = u " +
            "WHERE (myFriensd.srcUser = :currentUser OR myFriensd.dstUser = :currentUser) AND u.firstName = :firstName " +
            "AND myFriensd.status = 'FRIEND'")
    List<User> getFriendsByName(@Param("currentUser") User user, @Param("firstName") String firstName,
                                Pageable pageable);

    @Query(value = "" +
            "SELECT DISTINCT friendsMyFriends " +
            "FROM Friendship AS myFriendship " +
            "INNER JOIN User as myFriends " +
            "    ON (CASE WHEN myFriendship.srcUser = :currentUser " +
            "           THEN myFriendship.dstUser " +
            "        ELSE myFriendship.srcUser " +
            "        END) = myFriends " +
            "LEFT JOIN Friendship AS friendshipMyFriends " +
            "ON (myFriends = friendshipMyFriends.srcUser  " +
            "    OR myFriends = friendshipMyFriends.dstUser) " +
            "   AND friendshipMyFriends.status = 'FRIEND' " +
            "INNER JOIN User as friendsMyFriends " +
            "    ON (CASE WHEN myFriends = friendshipMyFriends.srcUser " +
            "         THEN friendshipMyFriends.dstUser " +
            "         ELSE friendshipMyFriends.srcUser " +
            "        END) = friendsMyFriends " +
            "LEFT JOIN Friendship AS allMyFriendship " +
            "   ON (allMyFriendship.dstUser = :currentUser OR allMyFriendship.srcUser = :currentUser) " +
            "       AND (allMyFriendship.status = 'FRIEND' OR allMyFriendship.status = 'DECLINED' " +
            "               OR allMyFriendship.status = 'BLOCKED') " +
            "        AND (friendsMyFriends = allMyFriendship.dstUser " +
            "             OR friendsMyFriends.id = allMyFriendship.srcUser) " +
            "WHERE (myFriendship.srcUser = :currentUser OR myFriendship.dstUser = :currentUser) " +
            "        AND myFriendship.status = 'FRIEND' " +
            "        AND allMyFriendship  IS NULL " +
            "        AND friendsMyFriends != :currentUser ")
    Page<User> getFriendsRecommendations(User currentUser, Pageable pageable);

    @Query(value = "" +
            "SELECT DISTINCT u " +
            "FROM Friendship as myFriensd " +
            "INNER JOIN User u " +
            "ON (CASE WHEN myFriensd.srcUser = :currentUser " +
            "      THEN myFriensd.dstUser " +
            "    ELSE myFriensd.srcUser END) = u " +
            "WHERE (myFriensd.srcUser = :currentUser OR myFriensd.dstUser = :currentUser) " +
            "AND myFriensd.status = 'FRIEND'")
    List<User> getAllMyFriends(@Param("currentUser") User user, Pageable pageable);

    @Query(value = "select distinct u.* from user u " +
            "inner join town t ON u.town_id = t.id " +
            "where LOWER(u.first_name) like concat(LOWER(:firstName),'%') and " +
            "LOWER(u.last_name) LIKE concat(LOWER(:lastName),'%') and " +
            "(CASE WHEN :townId > 0 then u.town_id = :townId ELSE u.id != 0 end) and " +
            "(CASE WHEN :countryId > 0 then t.country_id = :countryId ELSE u.id != 0 end)" +
            "and birth_date between :birthFrom and :birthTo " +
            "and NOT (u.id = :id)",
            nativeQuery = true)
    Page<User> getUsersSearch(
            @Param("id") Integer id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("townId") int townId,
            @Param("countryId") int countryId,
            @Param("birthFrom") LocalDateTime birthFrom,
            @Param("birthTo") LocalDateTime birthTo, Pageable pageable);

    @Query("FROM User WHERE id IN (:userIds)")
    List <User> getUsersForDialog (@Param("userIds") List <Integer> userIds);

}
