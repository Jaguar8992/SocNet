package main.controller;

import main.service.platform.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/platform")
public class ApiPlatformController {

    private final PlatformService platformService;

    @Autowired
    public ApiPlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping("/cities")
    private ResponseEntity<?> getCities(
            @RequestParam Integer countryId,
            @RequestParam(defaultValue = "", required = false) String city,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return platformService.getCities(countryId, city, offset, itemPerPage);
    }

    @GetMapping("/languages")
    private ResponseEntity<?> getLanguages(
            @RequestParam(defaultValue = "", required = false) String language,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return platformService.getLanguages(language, offset, itemPerPage);
    }

    @GetMapping("/countries")
    private ResponseEntity<?> getCountries(
            @RequestParam(defaultValue = "", required = false) String country,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return platformService.getCountries(country, offset, itemPerPage);
    }

}
