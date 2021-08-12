package main.service.account;

import com.github.cage.GCage;
import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.dto.DTOSuccessfully;
import main.mailSender.MailSender;
import main.model.entity.TokenToUser;
import main.model.entity.User;
import main.model.repository.TokenToUserRepository;
import main.model.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;


@Service
public class RecoveryService {

    private final UserRepository userRepository;
    private final TokenToUserRepository tokenToUserRepository;
    private final Logger log = Logger.getLogger(RecoveryService.class.getName());

    @Autowired
    public RecoveryService(UserRepository userRepository, TokenToUserRepository tokenToUserRepository) {
        this.userRepository = userRepository;
        this.tokenToUserRepository = tokenToUserRepository;
    }

    public ResponseEntity<?> createResponse(String email, String address) {
        Optional<User> user = userRepository.findByEmail(email);

        //Проверка на корректность email
        if (user.isEmpty()) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.badRequest().body(new Error(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        //Создание токена и привязка к пользователю
        String token = new GCage().getTokenGenerator().next();

        TokenToUser tokenToUser = new TokenToUser();
        tokenToUser.setToken(token);
        tokenToUser.setUserId(user.get().getId());

        tokenToUserRepository.save(tokenToUser);
        //Отправка сообщения
        MailSender.sendMessage(email, "Recovery link", address + "/change-password?token=" + token);

        log.info("Recovery link was send");

        return ResponseEntity.ok(new DTOSuccessfully(
                null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }
}
