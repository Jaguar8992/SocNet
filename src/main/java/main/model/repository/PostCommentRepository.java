package main.model.repository;

import main.model.entity.Post;
import main.model.entity.PostComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    @Query(value = "SELECT pc FROM PostComment pc WHERE pc.post = :post")
    List<PostComment> searchByPost(@Param("post") Post post);

    @Query(value = "SELECT pc FROM PostComment pc WHERE pc.post.id = :postId")
    List<PostComment> searchCommentsByPostId(@Param("postId") int postId, Pageable pageable);

    @Query(value = "SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.id = :commentId")
    PostComment getPostCommentById(@Param("postId") Integer postId, @Param("commentId") Integer commentId);

    @Query(value = "SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.author.id = :authorId AND pc.parent.id = :parentId ")
    List<PostComment> getPostCommentWithParentId(@Param("postId") Integer postId,
                                                 @Param("authorId") Integer authorId,
                                                 @Param("parentId") Integer parentId);

}
