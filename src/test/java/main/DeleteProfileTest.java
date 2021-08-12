package main;

import main.model.entity.User;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.UserType;
import main.model.repository.UserRepository;
import main.service.profiles.ProfileService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Удаление профиля")
public class DeleteProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileService profileService;

    private final UserRepository userRepository;

    @Autowired
    public DeleteProfileTest(UserRepository userRepository) {
        this.userRepository = userRepository;
        User user = new User();

        user.setEmail("test@mail.ru");
        user.setPassword(new BCryptPasswordEncoder().encode("Ab123456"));
        user.setFirstName("FirstName");
        user.setLastName("LastName");

        user.setRegDate(LocalDateTime.now());
        user.setLastOnlineTime(LocalDateTime.now());
        user.setIsApproved(false);
        user.setType(UserType.USER);
        user.setIsBlocked(false);
        user.setMessagesPermission(MessagesPermission.ALL);

        this.userRepository.save(user);

    }

    @Test
    @DisplayName("Удаление профиля")
    @WithUserDetails("test@mail.ru")
    public void deleteProfile () throws Exception {
        mockMvc.perform(delete("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("message\":\"ok")));
    }
}
