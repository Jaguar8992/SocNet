package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileChangingRequest {
    @JsonProperty
    private String about;
    @JsonProperty("birth_date")
    private String birthDate;
    @JsonProperty
    private String city;
    @JsonProperty
    private String country;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty
    private String phone;
    @JsonProperty
    private String photoId;
    /**
     * from Request Payload in changeparam page
     *
     * about: null
     * birth_date: "1992-09-02T00:00:00+04:00"
     * city: "Москва"
     * country: "Россия"
     * first_name: "Иван"
     * last_name: "Кириленко"
     * phone: "9843251514"
     * photo_id: "654316f4b60e4c0a8e07a07500586619"
     */
}
