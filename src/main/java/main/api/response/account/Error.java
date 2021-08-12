package main.api.response.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {

    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
}
