package main.service.profiles;

import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.dto.DTOSuccessfully;
import main.api.response.user.UserResponseList;
import main.api.response.user.UserResponse;
import main.model.entity.User;
import main.model.repository.UserRepository;
import main.service.auth.LoginLogoutService;
import org.apache.log4j.Logger;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final Logger log = Logger.getLogger(ProfileService.class.getName());
    private final LoginLogoutService loginLogoutService;

    @Autowired
    public ProfileService(UserRepository userRepository, UserService userService, LoginLogoutService loginLogoutService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.loginLogoutService = loginLogoutService;
    }

    public ResponseEntity<?> createUsersSearchResponse(String firstName, String lastName, int ageFrom, int ageTo,
                                                       int townId, int countryId, int offset, int itemPerPage) {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(401).body(
                    new Error(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }
        final PageRequest page = PageRequest.of(offset, itemPerPage);

        LocalDateTime startDate = LocalDateTime.now().minusYears(ageTo + 1);
        LocalDateTime endDate = LocalDateTime.now().minusYears(ageFrom);

        Page<User> findUsers = userRepository.getUsersSearch(currentUser.getId(), firstName, lastName, townId, countryId, startDate, endDate,
                page);
        List<UserResponse> data = new ArrayList<>();
        findUsers.forEach(user -> data.add(new UserResponse(user)));

        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();
        return ResponseEntity.ok(
                new UserResponseList("string", timestamp, findUsers.getTotalElements(), offset, itemPerPage,
                        data));
    }

    public ResponseEntity<?> getUserById(int id)  {
        User user;
        try {
            user = userService.getUserById(id);
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new Error(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }
        return ResponseEntity.ok(new UserResponse(user));
    }

    public ResponseEntity<?> getCurrentUser() throws UsernameNotFoundException {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(401).body(
                    new Error(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }
        return ResponseEntity.ok(new UserResponse(currentUser));
    }

    public ResponseEntity <?> deleteUser (HttpServletRequest request, HttpServletResponse response){

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

        //Logout
        loginLogoutService.logoutUser(request,response);
        //Delete user
        userRepository.delete(currentUser);

        return ResponseEntity.ok(new DTOSuccessfully(
                null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }
}
