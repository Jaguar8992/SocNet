package main.api.response.loginlogout;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorsLoginLogoutResponse {
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
}
