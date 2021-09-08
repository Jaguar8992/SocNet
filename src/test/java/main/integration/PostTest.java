package main.integration;

import main.model.entity.*;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.UserType;
import main.model.repository.*;
import main.service.posts.PostService;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Testing posts")
@ActiveProfiles("test_config")
public class PostTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PostCommentRepository postCommentRepository;

    @MockBean
    private PostLikeRepository postLikeRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private TagRepository tagRepository;

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
        Tag tag = new Tag();
        tag.setName("test");
        tag.setId(1);
        tag.setPosts(List.of(post));
        post.setTags(List.of(tag));

        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(new PageImpl<>(List.of(post))).when(postRepository).search(any(), any(), anyString(), any(), any());
        doReturn(Optional.of(post)).when(postRepository).getPostById(2);
        doReturn(List.of(comment)).when(postCommentRepository).searchByPost(any());
        doReturn(Optional.of(postLike)).when(postLikeRepository).findMyLikeInComment(anyInt(), anyInt());
        doReturn(List.of()).when(notificationRepository).getNotificationsWithDelay(any());
        doReturn(List.of(tag.getName())).when(tagRepository).getNameByPost(anyInt());

    }

    @Test
    @DisplayName("Testing run Spring Boot")
    public void contextLoads() throws Exception {
    }

    @Test
    @DisplayName("Controller @NotNull")
    public void controllerNotNull() throws Exception {
        assertThat(postService).isNotNull();
    }

    @Test
    @DisplayName("Checking the error noAuthentication in the response")
    public void checkErrorsInResponse() throws Exception {
        doThrow(UsernameNotFoundException.class).when(userRepository).findByEmail(anyString());
        //noAuthentication
        this.mockMvc.perform(get(port + "/api/v1/post"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Checking the error authentication in the response")
    @WithMockUser
    public void givenUser_whenGetPost_thenOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/post")
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get post by id. (200 OK)")
    @WithMockUser
    public void shouldReturnPost() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(this.port + "/api/v1/post/2"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Post not found exception. (4O0)")
    @WithMockUser
    public void shouldReturnBadRequest() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(this.port + "/api/v1/post/9999"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


}


