package main.service.account;

import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.dto.DTOSuccessfully;
import main.model.entity.User;
import main.model.repository.UserRepository;
import main.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SetEmailService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final Logger log = Logger.getLogger(SetEmailService.class.getName());

    @Autowired
    public SetEmailService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public ResponseEntity<?> createResponse(String email) {
        User currentUser;

        //Проверка авторизирвоан ли пользователь
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new Error(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        //Занят ли данный email
        if (userRepository.findByEmail(email).isPresent()) {
            log.error(DTOErrorDescription.EXIST.get());
            return ResponseEntity.badRequest().body(new Error(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.EXIST.get()));
        }

        //Смена паролья
        currentUser.setEmail(email);
        userRepository.save(currentUser);

        log.info("Email was change");

        return ResponseEntity.ok(new DTOSuccessfully(null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }
}
