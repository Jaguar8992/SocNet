package main.model.repository;

import main.model.entity.Dialog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {

    @Query(value = "select distinct d.* from dialog d " +
            "inner join message m on d.id = m.dialog_id " +
            "where d.owner_id = :ownerId and " +
            "LOWER(m.message_text) like LOWER(concat('%',(:query),'%'))",
            nativeQuery = true)
    Page<Dialog> getAllDialog(@Param("ownerId") int ownerId,
                              @Param("query") String query,
                              Pageable pageable);

    Optional<Dialog> findDialogById(Integer dialogId);

}
