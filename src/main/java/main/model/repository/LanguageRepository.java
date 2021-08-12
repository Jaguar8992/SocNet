package main.model.repository;

import main.model.entity.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {

    @Query(value = "SELECT l FROM Language l WHERE l.name = :language ")
    Page<Language> findByLanguage(@Param("language") String language, Pageable pageable);

    @Query(value = "SELECT l FROM Language l")
    Page<Language> findAll(Pageable pageable);

}
