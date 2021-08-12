package main.api.response.loginlogout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.entity.User;

import java.time.ZoneOffset;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataLoginResponse {
    private int id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("reg_date")
    private long regDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("birth_date")
    private Long birthDate;
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String photo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String about;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String city;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String country;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("messages_permission")
    private String messagesPermission;
    @JsonProperty("last_online_time")
    private long lastOnlineTime;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    private String token;

    public DataLoginResponse(User user, String token) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.regDate = user.getRegDate().toEpochSecond(ZoneOffset.UTC);
        this.birthDate = user.getBirthDate() == null ? null : user.getBirthDate().toEpochSecond(ZoneOffset.UTC);
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.photo = user.getPhoto();
        this.about = user.getAbout();
        this.city = user.getTown() == null ? null : user.getTown().getName();
        this.country = user.getTown() == null ? null : user.getTown().getCountry().getName();
        this.messagesPermission = user.getMessagesPermission().name();
        this.lastOnlineTime = user.getLastOnlineTime().toEpochSecond(ZoneOffset.UTC);
        this.isBlocked = user.getIsBlocked();
        this.token = token;
    }
}
