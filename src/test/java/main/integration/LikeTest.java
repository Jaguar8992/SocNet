package main.integration;

import main.model.entity.*;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.UserType;
import main.model.repository.NotificationRepository;
import main.model.repository.PostLikeRepository;
import main.model.repository.PostRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Like test")
@ActiveProfiles("test_config")
public class LikeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostLikeRepository postLikeRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        Country country = new Country();
        country.setId(1);
        country.setName("Russia");
        Town town = new Town(1, "Moscow", country, List.of());
        User user = new User(2, "Анна", "Чернов", LocalDateTime.now(), LocalDateTime.now().minusYears(23), "user@mail.ru",
                "", "password", "", "", town, "", (byte) 1, MessagesPermission.ALL,
                LocalDateTime.now(), (byte) 1, (byte) 1, UserType.USER, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        Post post = new Post(2, (long) 100, user, "title", "postText", (byte) 1, (byte) 0, 2);
        PostLike postLike = new PostLike(1, LocalDateTime.now(), user, post, new PostComment());
        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(Optional.of(user)).when(userRepository).findUserById(anyInt());
        doReturn(Optional.of(post)).when(postRepository).getPostById(anyInt());
        doReturn(Optional.of(postLike)).when(postLikeRepository).findByPostForUser(anyInt(), anyInt());
        doReturn(List.of(2, 3)).when(postLikeRepository).findUsersByPost(anyInt());
        doReturn(List.of(new Notification())).when(notificationRepository).getNotificationsWithDelay(any());
        doAnswer(i -> i.getArguments()[0]).when(postLikeRepository).save(any(PostLike.class));

    }

    @Test
    @DisplayName("Has Like")
    @WithMockUser
    public void hasLike() throws Exception {
        this.mockMvc.perform(get("/api/v1/liked?user_id=2&type=Post&item_id=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"data\":{\"likes\":true}")));
    }

    @Test
    @DisplayName("Get likes")
    @WithMockUser
    public void getLikes() throws Exception {
        this.mockMvc.perform(get("/api/v1/likes?type=Post&item_id=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"data\":{\"likes\":2,\"users\":[2,3]}")));
    }

    @Test
    @DisplayName("Put Like")
    @WithMockUser
    public void putLike() throws Exception {
        this.mockMvc.perform(put("/api/v1/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\" : \"Post\",\"item_id\": \"2\"}"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete Like")
    @WithMockUser
    public void deleteLike() throws Exception {
        this.mockMvc.perform(delete("/api/v1/likes?type=Post&item_id=2"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
