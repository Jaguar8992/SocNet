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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Тестирование отправки ссылки для восстановления")
@ActiveProfiles("test_config")
public class RecoveryTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenToUserRepository tokenToUserRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() {
        Country country = new Country();
        country.setId(1);
        country.setName("Russia");
        Town town = new Town(1, "Moscow", country, List.of());
        User user = new User(1, "Анна", "Чернов", LocalDateTime.now(), LocalDateTime.now().minusYears(23), "NikSok1992@mail.ru",
                "", "password", "", "", town, "", (byte) 1, MessagesPermission.ALL,
                LocalDateTime.now(), (byte) 1, (byte) 1, UserType.USER, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doAnswer(i -> i.getArguments()[0]).when(tokenToUserRepository).save(any(TokenToUser.class));
        doReturn(List.of()).when(notificationRepository).getNotificationsWithDelay(any());


    }

    @Test
    @DisplayName("Сообщение отправленно на почту")
    public void sendEmail() throws Exception {
        mockMvc.perform(put("/api/v1/account/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"email\": \"NikSok1992@gmail.com\"}")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("message\":\"ok")));
    }
}

