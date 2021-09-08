package main.integration.account;


import main.model.entity.Country;
import main.model.entity.TokenToUser;
import main.model.entity.Town;
import main.model.entity.User;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.UserType;
import main.model.repository.NotificationRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Подтверждение регистрации")
@ActiveProfiles("test_config")
public class RegisterConfirmTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenToUserRepository tokenToUserRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() {
        Country country = new Country();
        country.setId(1);
        country.setName("Russia");
        Town town = new Town(1, "Moscow", country, List.of());
        User user = new User(1, "Анна", "Чернов", LocalDateTime.now(), LocalDateTime.now().minusYears(23), "user@mail.ru",
                "", "password", "", "", town, "", (byte) 1, MessagesPermission.ALL,
                LocalDateTime.now(), (byte) 1, (byte) 1, UserType.USER, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        TokenToUser testToken = new TokenToUser();
        testToken.setToken("token");
        testToken.setUserId(3);
        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(Optional.of(user)).when(userRepository).findById(3);
        doReturn(List.of()).when(tokenToUserRepository).selectExpiredToken(any());
        doReturn(Optional.of(testToken)).when(tokenToUserRepository).findByToken("token");
        doReturn(List.of()).when(notificationRepository).getNotificationsWithDelay(any());

    }

    @Test
    @DisplayName("Подтверждение")
    public void setPassword() throws Exception {

        {
            mockMvc.perform(post("/api/v1/account/register/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(("{\"token\" : \"token\",\"userId\" : \"3\"}")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("message\":\"ok")));
        }
    }
}

