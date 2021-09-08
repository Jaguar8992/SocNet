package main.api.response.user;

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
public class UserResponse {
    private int id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("reg_date")
    private long regDate;

    @JsonProperty("birth_date")
    private long birthDate;

    private String email;

    private String phone;

    private String photo;

    private String about;

    private String city;

    private String country;

    @JsonProperty("messages_permission")
    private String messagesPermission;

    @JsonProperty("last_online_time")
    private long lastOnlineTime;

    @JsonProperty("is_blocked")
    private boolean isBlocked;

    public UserResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.regDate = user.getRegDate().toEpochSecond(ZoneOffset.UTC);
        this.birthDate = user.getBirthDate() == null ? 0 : user.getBirthDate().toEpochSecond(ZoneOffset.UTC);
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.photo = user.getPhoto() == null ? "" : user.getPhoto();
        this.about = user.getAbout();
        this.city = user.getTown() == null ? "" : user.getTown().getName();
        this.country = user.getTown() == null ? "" : user.getTown().getCountry().getName();
        this.messagesPermission = user.getMessagesPermission().toString();
        this.lastOnlineTime = user.getLastOnlineTime().toEpochSecond(ZoneOffset.UTC);
        this.isBlocked = user.getIsBlocked();
    }
}
