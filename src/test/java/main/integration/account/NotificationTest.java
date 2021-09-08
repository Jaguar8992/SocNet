package main.integration.account;


import main.model.entity.*;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.NotificationType;
import main.model.entity.enums.UserType;
import main.model.repository.NotificationRepository;
import main.model.repository.NotificationSettingRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Тестирование настроек для оповещения")
@ActiveProfiles("test_config")
public class NotificationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationSettingRepository settingRepository;

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

        NotificationSetting notificationSetting = new NotificationSetting(1, NotificationType.POST, user, (byte) 1);

        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(List.of(new Notification())).when(notificationRepository).getNotificationsWithDelay(any());
        doReturn(Optional.of(notificationSetting)).when(settingRepository).findByTypeAndPersonId(any(), any());
    }

    @Test
    @DisplayName("Установка оповещения")
    @WithMockUser
    public void setNotification() throws Exception {
        mockMvc.perform(put("/api/v1/account/notifications?notification_type=POST&enable=true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"notification_type\" : \"POST\",\"enable\": true}")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("message\":\"ok")));
    }
}