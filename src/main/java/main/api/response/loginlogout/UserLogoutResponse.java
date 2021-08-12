package main.api.response.loginlogout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogoutResponse {
    private String error;
    private long timestamp;
    private DataLogoutResponse data;
}
