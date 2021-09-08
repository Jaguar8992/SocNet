package main.model.repository;

import main.model.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    @Query(value = "SELECT * FROM post_like WHERE user_id = :user_id AND post_id = :post_id", nativeQuery = true)
    Optional<PostLike> findByPostForUser(@Param("user_id") Integer userId, @Param("post_id") Integer postId);

    @Query(value = "SELECT * FROM post_like WHERE user_id = :user_id AND comment_id = :comment_id", nativeQuery = true)
    Optional<PostLike> findByCommentForUser(@Param("user_id") Integer userId, @Param("comment_id") Integer commentId);

    @Query(value = "SELECT pl.user.id FROM PostLike pl WHERE pl.post.id = :postId")
    List<Integer> findUsersByPost(@Param("postId") Integer postId);

    @Query(value = "SELECT pl.user.id FROM PostLike pl WHERE pl.postComment.id = :comment_id")
    List<Integer> findUsersByComment(@Param("comment_id") Integer commentId);

    @Query(value = "SELECT pl FROM PostLike pl WHERE pl.postComment.id = :postCommentId AND pl.user.id = :userId ")
    Optional<PostLike> findMyLikeInComment(@Param("userId") Integer userId, @Param("postCommentId") Integer postCommentId);

    @Query(value = "SELECT pl FROM PostLike pl WHERE pl.post.id = :postId AND pl.user.id = :userId ")
    Optional<PostLike> findMyLikeByPostId(@Param("userId") Integer userId, @Param("postId") Integer postId);
}
