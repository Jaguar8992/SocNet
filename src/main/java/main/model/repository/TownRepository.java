package main.model.repository;

import main.model.entity.Town;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TownRepository extends JpaRepository<Town, Integer> {

    @Query(value = "SELECT * FROM town WHERE country_id = :country_id AND name LIKE CONCAT(:query,'%')", nativeQuery = true)
    Page <Town> getCities (@Param("country_id") Integer countryId, @Param("query") String query, Pageable pageable);

}
