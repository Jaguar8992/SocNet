package main.model.repository;

import main.model.entity.Post;
import main.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Optional<Post> getPostById(Integer id);

    @Query(value = "SELECT new main.model.entity.Post(" +
            "p.id AS id, " +
            "UNIX_TIMESTAMP(p.time) AS timestamp, " +
            "MAX(p.author) AS author, " +
            "MAX(p.title) AS title, " +
            "MAX(p.postText) AS postText, " +
            "MAX(p.isBlocked) AS isBlocked, " +
            "MAX(p.isDeleted) AS isDeleted, " +
            "SUM(CASE WHEN pLike.id IS NULL THEN 0 ELSE 1 END) AS likes) " +
            "FROM Post AS p " +
            "LEFT JOIN PostLike AS pLike ON pLike.post = p " +
            "WHERE lower(p.postText) LIKE lower(:query) " +
            "AND p.author = :user and p.isDeleted = 0 " +
            "AND p.time >= :dateFrom AND p.time <= :dateTo " +
            "GROUP BY p.id, UNIX_TIMESTAMP(p.time)")
    Page<Post> search(Pageable page,
                      @Param("user") User user,
                      @Param("query") String query,
                      @Param("dateFrom") LocalDateTime dateFrom,
                      @Param("dateTo") LocalDateTime dateTo);

    @Query(value = "SELECT new main.model.entity.Post(" +
            "p.id AS id, " +
            "UNIX_TIMESTAMP(p.time) AS timestamp, " +
            "MAX(p.author) AS author, " +
            "MAX(p.title) AS title, " +
            "MAX(p.postText) AS postText, " +
            "MAX(p.isBlocked) AS isBlocked, " +
            "MAX(p.isDeleted) AS isDeleted, " +
            "SUM(CASE WHEN pLike.id IS NULL THEN 0 ELSE 1 END) AS likes) " +
            "FROM Post AS p " +
            "LEFT JOIN PostLike AS pLike ON pLike.post = p " +
            "WHERE p.author IN (:users) AND " +
            "(p.author = :currentUser OR (p.isDeleted = 0 and p.time <= NOW())) " +
            "GROUP BY p.id, UNIX_TIMESTAMP(p.time)")
    Page<Post> getAllPostByUsers(Pageable page,
                                 @Param("users") List<User> users,
                                 @Param("currentUser") User currentUser);
}
