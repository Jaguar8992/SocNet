package main.service.account;

import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.dto.DTOSuccessfully;
import main.model.entity.TokenToUser;
import main.model.entity.User;
import main.model.repository.TokenToUserRepository;
import main.model.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SetPasswordService {

    private final TokenToUserRepository tokenToUserRepository;
    private final UserRepository userRepository;
    private final Logger log = Logger.getLogger(SetPasswordService.class.getName());

    @Autowired
    public SetPasswordService(TokenToUserRepository tokenToUserRepository, UserRepository userRepository) {
        this.tokenToUserRepository = tokenToUserRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createResponse(String token, String password) {

        List<TokenToUser> expired = tokenToUserRepository.selectExpiredToken(new Date());
        if (!expired.isEmpty()) {
            log.info("Expired token was delete");
            expired.forEach(tokenToUserRepository::delete);
        }

        Optional<TokenToUser> byToken = tokenToUserRepository.findByToken(token);

        //Проверяем устарела ли ссылка
        if (byToken.isEmpty()) {
            log.error(DTOErrorDescription.EXPIRED.get());
            return ResponseEntity.badRequest().body(new Error(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.EXPIRED.get()));
        }

        //Устанавливаем новый пароль и удаляем токен
        User person = userRepository.findById(byToken.get().getUserId()).get();
        person.setPassword(new BCryptPasswordEncoder(12).encode(password));
        userRepository.save(person);
        tokenToUserRepository.delete(byToken.get());

        log.info("Password was change");

        return ResponseEntity.ok().body(new DTOSuccessfully(null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }
}
