package main.service;

import main.model.entity.User;
import main.model.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() throws UsernameNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return getUser(currentPrincipalName);
    }

    private User getUser(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("user "
                        + username + " " + "not found"));
    }

    public User getUserById(Integer userId) throws UsernameNotFoundException {
        return userRepository
                .findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user "
                        + userId + " " + "not found"));
    }

}
