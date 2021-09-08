package main.integration;

import main.model.entity.Dialog;
import main.model.entity.Message;
import main.model.entity.Notification;
import main.model.entity.User;
import main.model.entity.enums.ReadMessageStatus;
import main.model.repository.*;
import main.service.dialog.MessageService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Send message")
@ActiveProfiles("test_config")
public class MessageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageService messageService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DialogRepository dialogRepository;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private NotificationSettingRepository notificationSettingRepository;

    @BeforeEach
    public void setUp() {
        User user = Mockito.mock(User.class);
        Message message = new Message(1, LocalDateTime.now(), user,
                "Привет бро", ReadMessageStatus.READ, List.of(new Notification()), new Dialog());
        Dialog dialog = new Dialog(1, user, new User(), false,
                "sada12", List.of(message), message);
        Notification notification = Mockito.mock(Notification.class);
        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(Optional.of(dialog)).when(dialogRepository).findDialogById(anyInt());
        doReturn(List.of(notification)).when(notificationRepository).findByUserAndEntityId(anyInt(), anyInt(), any());
        doReturn(Optional.of((byte) 0)).when(notificationSettingRepository).getNotificationSetting(any(), any());
        doAnswer(i -> i.getArguments()[0]).when(dialogRepository).save(any(Dialog.class));
        doAnswer(i -> i.getArguments()[0]).when(messageRepository).save(any(Message.class));
        doAnswer(i -> i.getArguments()[0]).when(notificationRepository).save(any(Notification.class));

    }

    @Test
    @DisplayName("Send message")
    @WithMockUser
    public void postNewDialog() throws Exception {
        mockMvc.perform(post("/api/v1/dialogs/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message_text\" : \"Hello, its test message\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"String")));
    }
}
