package main.service.platform;

import main.api.response.platform.PlatformResponse;
import main.api.response.platform.PlatformResponseList;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

        return new ResponseEntity<>(new PlatformResponseList(
                "string",
                Instant.now().getEpochSecond(),
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

        //Если города найденны формируем список
        if (cities.getTotalElements() > 0) {

            cities.getContent().forEach(town -> {
                cityResponse.add(new PlatformResponse(town.getId(), town.getName()));
            });
        }

        log.info("successfully");

        return new ResponseEntity<>(new PlatformResponseList(
                "string",
                Instant.now().getEpochSecond(),
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

        return new ResponseEntity<>(new PlatformResponseList(
                "string",
                Instant.now().getEpochSecond(),
                content.size(),
                offset,
                itemPerPage,
                platformResponse), HttpStatus.OK);
    }

}
