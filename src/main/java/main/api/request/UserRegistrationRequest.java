package main.api.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {
    private String email;
    private String passwd1;
    private String passwd2;
    private String firstName;
    private String lastName;
    private String token;
}
