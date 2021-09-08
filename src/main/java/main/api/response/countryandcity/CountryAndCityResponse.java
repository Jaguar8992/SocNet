package main.api.response.countryandcity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryAndCityResponse {

    private Integer countryId;
    private String country;
    private Integer cityId;
    private String city;
}