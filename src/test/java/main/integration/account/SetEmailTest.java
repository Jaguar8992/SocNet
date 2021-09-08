package main.integration.account;


import main.model.entity.User;
import main.model.repository.NotificationRepository;
import main.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
@DisplayName("Смена эллектронной почты")
@ActiveProfiles("test_config")
public class SetEmailTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() {
        User user = Mockito.mock(User.class);
        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(Optional.empty()).when(userRepository).findByEmail("wwwemail@mail.com");
        doAnswer(i -> i.getArguments()[0]).when(userRepository).save(any(User.class));
        doReturn(List.of()).when(notificationRepository).getNotificationsWithDelay(any());

    }

    @Test
    @DisplayName("Устанавливаем тестовые данные")
    @WithMockUser
    public void setTestEmail() throws Exception {
        mockMvc.perform(put("/api/v1/account/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"email\" : \"wwwemail@mail.com\"}")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("message\":\"ok")));
    }
}

