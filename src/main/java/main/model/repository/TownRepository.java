package main.model.repository;

import main.model.entity.Town;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TownRepository extends JpaRepository<Town, Integer> {

    @Query(value = "SELECT t FROM Town t WHERE t.country.id = :country_id AND t.name LIKE CONCAT('%', :query, '%')")
    Page<Town> getCities(@Param("country_id") Integer countryId, @Param("query") String query, Pageable pageable);

    @Query(value = "SELECT DISTINCT t FROM Town t JOIN Country c ON c.id = t.country.id")
    List<Town> getAll();

    @Query(value = "SELECT t FROM Town t WHERE t.name = :name")
    Optional<Town> findByNameContains(@Param ("name") String name);

}
