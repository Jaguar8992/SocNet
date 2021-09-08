package main.service.platform;

import main.api.response.PageCommonResponseList;
import main.api.response.countryandcity.CountryAndCityListResponse;
import main.api.response.countryandcity.CountryAndCityResponse;
import main.api.response.platform.PlatformResponse;
import main.model.entity.Country;
import main.model.entity.Language;
import main.model.entity.Town;
import main.model.repository.CountryRepository;
import main.model.repository.LanguageRepository;
import main.model.repository.TownRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {

    private final TownRepository townRepository;
    private final LanguageRepository languageRepository;
    private final CountryRepository countryRepository;
    private final Logger log = Logger.getLogger(PlatformService.class.getName());

    @Autowired
    public PlatformService(TownRepository townRepository, LanguageRepository languageRepository, CountryRepository countryRepository) {
        this.townRepository = townRepository;
        this.languageRepository = languageRepository;
        this.countryRepository = countryRepository;
    }

    public ResponseEntity<?> getAll() {

        List<CountryAndCityResponse> countryAndCityResponseList = new ArrayList<>();
        List<Town> townsList = townRepository.getAll();

        for (int i = 0; i < townsList.size(); i++) {

            countryAndCityResponseList.add(
                    new CountryAndCityResponse(
                            townsList.get(i).getId(),
                            townsList.get(i).getName(),
                            townsList.get(i).getCountry().getId(),
                            townsList.get(i).getCountry().getName()));
        }

        return new ResponseEntity<>(
                new CountryAndCityListResponse(countryAndCityResponseList), HttpStatus.OK);
    }

    public ResponseEntity<?> getLanguages(String language, Integer offset, Integer itemPerPage) {
        Pageable pageable = PageRequest.of(offset, itemPerPage);

        Page<Language> languagePage;

        if (!language.equals("")) {
            languagePage = languageRepository.findByLanguage(language, pageable);
        } else {
            languagePage = languageRepository.findAll(pageable);
        }

        List<PlatformResponse> platformResponse = new ArrayList<>();

        for (int i = 0; i < languagePage.getContent().size(); i++) {
            platformResponse.add(new PlatformResponse(
                    languagePage.getContent().get(i).getId(),
                    languagePage.getContent().get(i).getName()));
        }

        return new ResponseEntity<>(new PageCommonResponseList<>(
                "string",
                languagePage.getContent().size(),
                offset,
                itemPerPage,
                platformResponse), HttpStatus.OK);
    }

    public ResponseEntity<?> getCities(Integer countryId, String query, Integer offset, Integer itemPerPage) {
        Pageable pageable = PageRequest.of(offset, itemPerPage);

        //Получение городов по текущим условиям
        Page<Town> cities = townRepository.getCities(countryId, query, pageable);

        List<PlatformResponse> cityResponse = new ArrayList<>();

        //Если города найдены формируем список
        if (cities.getTotalElements() > 0) {

            cities.getContent().forEach(town -> {
                cityResponse.add(new PlatformResponse(town.getId(), town.getName()));
            });
        }

        log.info("successfully");

        return new ResponseEntity<>(new PageCommonResponseList<>(
                "string",
                cities.getContent().size(),
                offset,
                itemPerPage,
                cityResponse), HttpStatus.OK);
    }

    public ResponseEntity<?> getCountries(String country, Integer offset, Integer itemPerPage) {
        Pageable pageable = PageRequest.of(offset, itemPerPage);

        Page<Country> countryPage = countryRepository.findCountries("%" + country + "%", pageable);
        List<Country> content = countryPage.getContent();

        List<PlatformResponse> platformResponse = new ArrayList<>();

        for (int i = 0; i < content.size(); i++) {
            platformResponse.add(new PlatformResponse(
                    content.get(i).getId(),
                    content.get(i).getName()));
        }

        return new ResponseEntity<>(new PageCommonResponseList<>(
                "string",
                content.size(),
                offset,
                itemPerPage,
                platformResponse), HttpStatus.OK);
    }

    /**
     * @param nameCity    - which we want to save in the repository
     * @param nameCountry - which we want to save in the repository
     * @return if there has a town then we return an old object, else we will create new
     */
    public Town createCityAndCountry(String nameCity, String nameCountry) {

        Optional<Town> city = townRepository.findByNameContains(nameCity);

        if (city.isPresent()) {
            return city.get();
        } else {
            Town newTown = new Town();
            newTown.setName(nameCity);
            newTown.setCountry(createCountry(nameCountry));
            townRepository.save(newTown);
            return townRepository.findByNameContains(nameCity).get();
        }

    }

    /**
     * @param name - country name for saving in the repository
     * @return old country, if we find the country in our repository, else
     * we will create new country
     */

    private Country createCountry(String name) {
        Optional<Country> countryOptional = countryRepository.findByNameContains(name);
        if (countryOptional.isPresent()) {
            return countryOptional.get();
        } else {
            Country country = new Country();
            country.setName(name);
            countryRepository.save(country);
            return countryRepository.findByNameContains(name).get();
        }
    }


}
