package main.integration.account;

import main.model.entity.*;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.UserType;
import main.model.repository.NotificationRepository;
import main.model.repository.NotificationSettingRepository;
import main.model.repository.TokenToUserRepository;
import main.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Регистрация")
@ActiveProfiles("test_config")
public class RegistrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenToUserRepository tokenToUserRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private NotificationSettingRepository notificationSettingRepository;

    @BeforeEach
    public void setUp() {
        Country country = new Country();
        country.setId(1);
        country.setName("Russia");
        Town town = new Town(1, "Moscow", country, List.of());
        User user = new User(1, "Анна", "Чернов", LocalDateTime.now(), LocalDateTime.now().minusYears(23), "user@mail.ru",
                "", "password", "", "", town, "", (byte) 1, MessagesPermission.ALL,
                LocalDateTime.now(), (byte) 1, (byte) 1, UserType.USER, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        doAnswer(i -> i.getArguments()[0]).when(userRepository).save(any(User.class));
        doAnswer(i -> i.getArguments()[0]).when(tokenToUserRepository).save(any(TokenToUser.class));
        doReturn(List.of(new Notification())).when(notificationRepository).getNotificationsWithDelay(any());
        doReturn(Optional.of((byte) 0)).when(notificationSettingRepository).getNotificationSetting(any(), any());
    }

    @Test
    @DisplayName("Регистрация")
    public void registration() throws Exception {
        mockMvc.perform(post("/api/v1/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"email\" : \"registration@gmail.com\"," +
                        "\"passwd1\" : \"Ab123456\"," +
                        "\"passwd2\" : \"Ab123456\"," +
                        "\"lastName\" : \"TestName\"," +
                        "\"firstName\" : \"TestName\"," +
                        "\"token\" : \"\"}")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("message\":\"ok")));
    }
}
