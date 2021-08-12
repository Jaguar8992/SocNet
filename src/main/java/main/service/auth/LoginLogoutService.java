package main.service.auth;

import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.response.loginlogout.DataLoginResponse;
import main.api.response.loginlogout.DataLogoutResponse;
import main.api.response.loginlogout.UserLoginResponse;
import main.api.response.loginlogout.UserLogoutResponse;
import main.model.entity.User;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class LoginLogoutService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginLogoutService(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> loginUser(String email, String password) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new Error(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        User user = findUser.get();

        if (new BCryptPasswordEncoder(12).matches(password, user.getPassword())) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return new ResponseEntity<>(setUserInfo(user, token.toString()), HttpStatus.OK);
        } else return ResponseEntity.badRequest().body(new Error(
                DTOError.INVALID_REQUEST.get(),
                DTOErrorDescription.BAD_CREDENTIALS.get()));
    }

    private UserLoginResponse setUserInfo(User user, String token) {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        long timestamp = dateTimeNow.toEpochSecond(ZoneOffset.UTC);
        return new UserLoginResponse("String", timestamp, new DataLoginResponse(user, token));

    }

    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, auth);

        UserLogoutResponse userLogoutResponse = new UserLogoutResponse();
        DataLogoutResponse dataLogoutResponse = new DataLogoutResponse();
        dataLogoutResponse.setMessage(new DTOMessage().getMessage());
        userLogoutResponse.setData(dataLogoutResponse);
        LocalDateTime dateTimeNow = LocalDateTime.now();
        long timestamp = dateTimeNow.toEpochSecond(ZoneOffset.UTC);
        userLogoutResponse.setTimestamp(timestamp);
        return new ResponseEntity<>(userLogoutResponse, HttpStatus.OK);
    }
}
