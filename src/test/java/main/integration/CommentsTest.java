package main.integration;

import main.model.entity.*;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.UserType;
import main.model.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Testing comments")
@ActiveProfiles("test_config")
public class CommentsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostCommentRepository postCommentRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PostLikeRepository postLikeRepository;

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

        Post post = new Post(2, (long) 100, user, "title", "postText", (byte) 1, (byte) 0, 10);
        PostComment comment = new PostComment(1, LocalDateTime.now(), post, 0, null, user, "Коммент", (byte) 1, false, List.of());
        PostLike postLike = new PostLike(1, LocalDateTime.now(), user, post, comment);
        doThrow(EntityNotFoundException.class).when(postRepository).getPostById(999999999);
        doReturn(Optional.of(post)).when(postRepository).getPostById(3);
        doReturn(List.of(comment)).when(postCommentRepository).searchCommentsByPostId(anyInt(), any());
        doReturn(List.of()).when(notificationRepository).getNotificationsWithDelay(any());
        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(Optional.of(postLike)).when(postLikeRepository).findMyLikeInComment(anyInt(), anyInt());

    }

    @Test
    @DisplayName("Checking the error authentication in the response")
    @WithMockUser
    public void getCommentsThenUserAuthorized() throws Exception {
        this.mockMvc.perform(get(port + "/api/v1/post/3/comments"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Checking the error noAuthentication in the response")
    public void getCommentsWhenUserNotAuthorized() throws Exception {
        this.mockMvc.perform(get(port + "/api/v1/post/3/comments"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Check error response")
    @WithMockUser
    public void checkErrorResponse() throws Exception {
        this.mockMvc.perform(get(port + "/api/v1/post/3/comments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));
    }

    @Test
    @DisplayName("Checking checking a non-existent post")
    @WithMockUser
    public void getCommentsWhenPostNotExits() throws Exception {
        this.mockMvc.perform(get(port + "/api/v1/post/999999999/comments"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
