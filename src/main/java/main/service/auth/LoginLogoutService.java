package main.service.auth;

import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.response.CommonResponseList;
import main.api.response.error.ErrorResponse;
import main.api.response.loginlogout.DataLoginResponse;
import main.api.response.loginlogout.DataLogoutResponse;
import main.model.entity.User;
import main.model.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LoginLogoutService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private Logger log = Logger.getLogger(LoginLogoutService.class.getName());

    @Autowired
    public LoginLogoutService(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> loginUser(String email, String password) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        User user = findUser.get();

        if (!user.getIsApproved()) {
            log.error(DTOErrorDescription.NOT_APPROVED.get());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.NOT_APPROVED.get()));
        }

        if (new BCryptPasswordEncoder(12).matches(password, user.getPassword())) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            user.setIsOnline((byte) 1);
            userRepository.save(user);
            return new ResponseEntity<>(setUserInfo(user, token.toString()), HttpStatus.OK);
        } else {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }
    }

    private CommonResponseList<?> setUserInfo(User user, String token) {
        return new CommonResponseList<>("String", new DataLoginResponse(user, token));

    }

    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        setLastOnlineTime(auth.getName());
        new SecurityContextLogoutHandler().logout(request, response, auth);

        return new ResponseEntity<>(new CommonResponseList<>("string",
                new DataLogoutResponse(
                        new DTOMessage().getMessage())), HttpStatus.OK);
    }

    private void setLastOnlineTime(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user "
                + email + " " + "not found"));
        user.setLastOnlineTime(LocalDateTime.now());
        user.setIsOnline((byte) 0);
        userRepository.save(user);
    }
}
