package main.integration;

import main.model.entity.*;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.ReadMessageStatus;
import main.model.entity.enums.UserType;
import main.model.repository.*;
import main.service.dialog.DialogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Testing dialogs")
@ActiveProfiles("test_config")
public class DialogTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DialogService dialogService;

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

    @LocalServerPort
    private String port;

    @BeforeEach
    public void setUp() throws Exception {

        Country country = new Country();
        country.setId(1);
        country.setName("Russia");
        Town town = new Town(1, "Moscow", country, List.of());
        User user = new User(1, "Анна", "Чернов", LocalDateTime.now(), LocalDateTime.now().minusYears(23), "user@mail.ru",
                "", "password", "", "", town, "", (byte) 1, MessagesPermission.ALL,
                LocalDateTime.now(), (byte) 1, (byte) 1, UserType.USER, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        User user2 = new User(2, "Влад", "Иванов", LocalDateTime.now(), LocalDateTime.now().minusYears(23), "user@mail.ru",
                "", "password", "", "", town, "", (byte) 1, MessagesPermission.ALL,
                LocalDateTime.now(), (byte) 1, (byte) 1, UserType.USER, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        Message message = new Message(1, LocalDateTime.now(), user,
                "Привет бро", ReadMessageStatus.READ, List.of(new Notification()), new Dialog());
        Dialog dialog = new Dialog(1, user, user2, false,
                "sada12", List.of(message), message);
        message.setDialog(dialog);

        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(List.of(user2)).when(userRepository).getUsersForDialog(any());
        doReturn(new PageImpl<>(List.of(dialog))).when(dialogRepository).getAllDialog(anyInt(), anyString(), any());
        doReturn(new PageImpl<>(List.of(message))).when(messageRepository).findAllByDialogAndMessageTextContaining(any(), anyString(), any());
        doReturn(Optional.of(dialog)).when(dialogRepository).findDialogById(anyInt());
        doReturn(List.of(new Notification())).when(notificationRepository).getNotificationsWithDelay(any());
        doReturn(Optional.of((byte) 0)).when(notificationSettingRepository).getNotificationSetting(any(), any());
        doAnswer(i -> i.getArguments()[0]).when(messageRepository).save(any(Message.class));
        doAnswer(i -> i.getArguments()[0]).when(dialogRepository).save(any(Dialog.class));


    }

    @Test
    @DisplayName("Testing run Spring Boot")
    public void contextLoads() throws Exception {
    }

    @Test
    @DisplayName("Controller @NotNull")
    public void controllerNotNull() throws Exception {
        assertThat(dialogService).isNotNull();
    }

    @Test
    @DisplayName("Checking the error noAuthentication in the response")
    public void checkErrorsInResponse() throws Exception {
        doThrow(UsernameNotFoundException.class).when(userRepository).findByEmail(anyString());
        //noAuthentication
        this.mockMvc.perform(get(port + "/api/v1/dialogs"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Checking the error authentication in the response")
    @WithMockUser
    public void getDialogs_ThenUserAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/dialogs")
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/dialogs?query=прив")
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Checking the error authentication in the response")
    @WithMockUser
    public void getMessagesDialog() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/dialogs/1/messages")
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get unread message")
    @WithMockUser
    public void getUnreadMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/dialogs/unreaded"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));
    }

    @Test
    @DisplayName("Post new Dialog")
    @WithMockUser
    public void postNewDialog() throws Exception {
        mockMvc.perform(post("/api/v1/dialogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"user_ids\" : [2,3]}")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));
    }
}
