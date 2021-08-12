package main.model.repository;

import main.model.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    @Query(value = "SELECT c FROM Country c WHERE lower(c.name) LIKE lower(:country) ")
    Page<Country> findCountries(String country, Pageable pageable);
}
