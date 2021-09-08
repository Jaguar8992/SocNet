package main.integration.account;


import main.model.entity.TokenToUser;
import main.model.entity.User;
import main.model.repository.NotificationRepository;
import main.model.repository.TokenToUserRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Смена пароля")
@ActiveProfiles("test_config")
public class SetPasswordTest {

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
        TokenToUser testToken = Mockito.mock(TokenToUser.class);
        User user = Mockito.mock(User.class);
        doReturn(List.of()).when(tokenToUserRepository).selectExpiredToken(any());
        doReturn(Optional.of(testToken)).when(tokenToUserRepository).findByToken(anyString());
        doReturn(Optional.of(user)).when(userRepository).findById(anyInt());
        doAnswer(i -> i.getArguments()[0]).when(userRepository).save(user);
        doAnswer(i -> i.getArguments()[0]).when(tokenToUserRepository).delete(testToken);
        doReturn(List.of()).when(notificationRepository).getNotificationsWithDelay(any());

    }

    @Test
    @DisplayName("Смена пароля")
    public void setPassword() throws Exception {
        {
            mockMvc.perform(put("/api/v1/account/password/set")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(("{\"token\" : \"testToken\" , \"password\" : \"Ab123456\"}")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("message\":\"ok")));
        }
    }
}

