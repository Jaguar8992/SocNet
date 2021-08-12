package main.service.account;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.request.UserRegistrationRequest;
import main.api.response.registration.DataRegistrationResponse;
import main.api.response.registration.UserRegistrationResponse;
import main.mailSender.MailSender;
import main.model.entity.TokenToUser;
import main.model.entity.User;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.UserType;
import main.model.repository.TokenToUserRepository;
import main.model.repository.UserRepository;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final TokenToUserRepository tokenToUserRepository;
    private final Logger log = Logger.getLogger(RegistrationService.class.getName());
    private String secretKey = "0x80C47b712f18D6e49DC3c33119FCfc876Ae24338";
    private HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).build();

    @Autowired
    public RegistrationService(UserRepository userRepository, TokenToUserRepository tokenToUserRepository) {
        this.userRepository = userRepository;
        this.tokenToUserRepository = tokenToUserRepository;
    }

    public ResponseEntity<?> registrationUser(UserRegistrationRequest userRegistrationRequest, String address) {

        try {
            if (!checkCaptcha(secretKey, userRegistrationRequest.getToken())){
                log.error(DTOErrorDescription.CAPTCHA_INCORRECT.get());
                return ResponseEntity.badRequest().body(new Error(
                        DTOError.INVALID_REQUEST.get(),
                        DTOErrorDescription.CAPTCHA_INCORRECT.get()));
            }
        } catch (Exception e) {
            log.error(e);
        }


        if (userRepository.findByEmail(userRegistrationRequest.getEmail()).isPresent()) {
            log.error(DTOErrorDescription.EXIST.get());
            return ResponseEntity.badRequest().body(new Error(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.EXIST.get()));
        } else return ResponseEntity.ok(setUserRegistrationInfo(userRegistrationRequest, address));
    }

    private UserRegistrationResponse setUserRegistrationInfo(UserRegistrationRequest userRegistrationRequest, String address) {
        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
        DataRegistrationResponse dataRegistrationResponse = new DataRegistrationResponse();

        dataRegistrationResponse.setMessage(new DTOMessage().getMessage());
        LocalDateTime dateTimeNow = LocalDateTime.now();
        long timestamp = dateTimeNow.toEpochSecond(ZoneOffset.UTC);

        setUser(userRegistrationRequest, dateTimeNow, address);
        userRegistrationResponse.setTimestamp(timestamp);
        userRegistrationResponse.setData(dataRegistrationResponse);

        log.info("DTOSuccessfully");

        return userRegistrationResponse;
    }

    private void setUser(UserRegistrationRequest userRegistrationRequest, LocalDateTime dateTimeNow, String address) {
        User user = new User();
        user.setRegDate(dateTimeNow);
        user.setFirstName(userRegistrationRequest.getFirstName());
        user.setLastName(userRegistrationRequest.getLastName());
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(new BCryptPasswordEncoder()
                .encode(userRegistrationRequest.getPasswd2()));
        //Not null
        user.setLastOnlineTime(dateTimeNow);
        user.setIsApproved(false);
        user.setType(UserType.USER);
        user.setIsBlocked(false);
        user.setMessagesPermission(MessagesPermission.ALL);
        //код не нужен! Он нужен только для фронта
        userRepository.save(user);

        sendEmail(user.getId(), userRegistrationRequest.getEmail(), address);
    }

    private void sendEmail(Integer id, String email, String address) {
        Cage cage = new GCage();
        String token = cage.getTokenGenerator().next();

        TokenToUser tokenToUser = new TokenToUser();
        tokenToUser.setToken(token);
        tokenToUser.setUserId(id);

        tokenToUserRepository.save(tokenToUser);

        MailSender.sendMessage(email, "Registration confirm",
                address + "/registration/complete?userId=" + id + "&token=" + token);

        log.info("Registration confirm link was send");
    }

    private boolean checkCaptcha (String secretKey, String token)
    {
        if (token.isEmpty()){
            return true;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("response=");
        builder.append(token);
        builder.append("&secret=");
        builder.append(secretKey);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://hcaptcha.com/siteverify"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(builder.toString())).build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonObject = new JSONObject(response.body());

            return (boolean) jsonObject.get("success");
        } catch (Exception e){
            log.error(e);
        }
        return false;
    }
}
