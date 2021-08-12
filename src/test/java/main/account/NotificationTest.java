package main.account;


import main.service.account.NotificationsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Тестирование настроек для оповещения")
public class NotificationTest {
    public NotificationTest() {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationsService notificationsService;

    @Test
    @DisplayName("Установка оповещения")
    @WithUserDetails("SergeevaAnna98@ya.ru")
    public void setNotification() throws Exception {
        mockMvc.perform(put("/api/v1/account/notifications?notification_type=POST&enable=true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"notification_type\" : \"POST\",\"enable\": true}")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("message\":\"ok")));
    }
}