package main.model.repository;

import main.model.entity.Post;
import main.model.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    @Query(value = "SELECT pc FROM PostComment pc WHERE pc.post = :post")
    List<PostComment> searchByPost(@Param("post") Post post);

}
