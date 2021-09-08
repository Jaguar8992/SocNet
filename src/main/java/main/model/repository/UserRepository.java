package main.model.repository;

import main.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserById(Integer id);

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
    List<User> getAllMyFriends(@Param("currentUser") User user);

    @Query(value = "select distinct u.* from user u " +
            "inner join town t ON u.town_id = t.id " +
            "inner join country c on t.country_id = c.id " +
            "where LOWER(u.first_name) like concat(LOWER(:firstName),'%') and " +
            "LOWER(u.last_name) LIKE concat(LOWER(:lastName),'%') and " +
            "(CASE WHEN length(:town) > 0 " +
            " then LOWER(t.name) like concat(lower(:town),'%') " +
            " ELSE u.id != 0 end) and " +
            "(CASE WHEN length(:country) > 0 " +
            "then LOWER(c.name) like concat(lower(:country),'%') " +
            "ELSE u.id != 0 end) " +
            "and birth_date between (current_date - interval :birthFrom year ) " +
            "and (current_date - interval :birthTo year ) " +
            "and NOT (u.id = :id)",
            nativeQuery = true)
    Page<User> getUsersSearch(
            @Param("id") Integer id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("town") String town,
            @Param("country") String country,
            @Param("birthFrom") int birthFrom,
            @Param("birthTo") int birthTo, Pageable pageable);

    @Query("FROM User WHERE id IN (:userIds)")
    List<User> getUsersForDialog(@Param("userIds") List<Integer> userIds);

    @Query(value = "SELECT DISTINCT u " +
            "FROM User u " +
            "WHERE DAYOFMONTH(u.birthDate) = DAYOFMONTH(:birthDay) AND " +
            "MONTH(u.birthDate) = MONTH(:birthDay)")
    List<User> getUsersByBirthDay(@Param("birthDay") LocalDateTime birthDay);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.photo = :photo WHERE u.id = :userId")
    void updateUserPhoto(@Param("photo") String photo, @Param("userId") Integer userId);

}
