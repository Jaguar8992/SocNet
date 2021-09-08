package main.model.repository;

import main.model.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query(value = "SELECT t FROM Tag t WHERE t.name LIKE CONCAT('%', :tag, '%')")
    Page<Tag> getTags(@Param("tag") String tag, Pageable pageable);

    Optional<Tag> findTagByName(String name);

    @Query(value = "SELECT t.name FROM Tag t LEFT JOIN Post2Tag tp ON tp.tag.id = t.id " +
            "LEFT JOIN Post p ON p.id = tp.post.id " +
            "WHERE tp.post.id = :post_id GROUP BY t.id")
    List<String> getNameByPost(@Param("post_id") Integer postId);

    @Query(value = "SELECT t FROM Tag t LEFT JOIN Post2Tag tp ON tp.tag.id = t.id WHERE tp.post.id IS NULL")
    List<Tag> excessTags();
}