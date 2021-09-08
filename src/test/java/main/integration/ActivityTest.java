package main.integration;

import main.model.entity.*;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.ReadMessageStatus;
import main.model.entity.enums.UserStatus;
import main.model.entity.enums.UserType;
import main.model.repository.DialogRepository;
import main.model.repository.NotificationRepository;
import main.model.repository.UserPrintStatusRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Активность пользователя")
@ActiveProfiles("test_config")
public class ActivityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private DialogRepository dialogRepository;

    @MockBean
    private UserPrintStatusRepository printStatusRepository;

    @BeforeEach
    void setUp() {
        User userFriend = Mockito.mock(User.class);
        Notification notification = Mockito.mock(Notification.class);
        Country country = new Country();
        country.setId(1);
        country.setName("Russia");
        Town town = new Town(1, "Moscow", country, List.of());
        User user = new User(1, "Анна", "Чернов", LocalDateTime.now(), LocalDateTime.now().minusYears(23), "user@mail.ru",
                "", "password", "", "", town, "", (byte) 1, MessagesPermission.ALL,
                LocalDateTime.now(), (byte) 1, (byte) 1, UserType.USER, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        Post post = new Post(2, (long) 100, user, "title", "postText", (byte) 1, (byte) 0, 10);
        PostComment postComment = new PostComment(1, LocalDateTime.now(), post, 0, null, user, "Провал", (byte) 0, false, List.of());
        Message message = new Message(1, LocalDateTime.now(), user,
                "Привет бро", ReadMessageStatus.READ, List.of(new Notification()), new Dialog());
        Dialog dialog = new Dialog(1, user, new User(), false,
                "sada12", List.of(message), message);
        UserPrintStatus userPrintStatus = new UserPrintStatus();
        userPrintStatus.setDialogId(dialog.getId());
        userPrintStatus.setUserId(user.getId());
        userPrintStatus.setStatus(UserStatus.PRINTS);
        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(Optional.of(user)).when(userRepository).findUserById(anyInt());
        doReturn(Optional.of(dialog)).when(dialogRepository).findDialogById(anyInt());
        doReturn(List.of()).when(notificationRepository).getNotificationsWithDelay(any());
        doReturn(Optional.of(userPrintStatus)).when(printStatusRepository).findByDialogIdAndUserId(any(), anyInt());
        doAnswer(i -> i.getArguments()[0]).when(printStatusRepository).save(any(UserPrintStatus.class));
    }

    @Test
    @DisplayName("Пользователь онлайн")
    @WithMockUser
    public void userOnline() throws Exception {
        this.mockMvc.perform(get("/api/v1/dialogs/1/activity/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Пользователь печатает")
    @WithMockUser
    public void userPrints() throws Exception {
        this.mockMvc.perform(post("/api/v1/dialogs/1/activity/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
