package main.integration;

import main.model.entity.*;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.UserType;
import main.model.repository.*;
import main.service.profiles.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
@DisplayName("Testing profile")
@ActiveProfiles("test_config")
public class ProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileService profileService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private NotificationSettingRepository notificationSettingRepository;

    @MockBean
    private PostCommentRepository postCommentRepository;

    @MockBean
    private PostLikeRepository postLikeRepository;

    @MockBean
    private TagRepository tagRepository;

    @LocalServerPort
    private String port;

    @BeforeEach
    public void setUp() throws Exception {
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
        PostLike postLike = new PostLike(1, LocalDateTime.now(), user, post, postComment);
        Tag tag = new Tag();
        tag.setName("test");
        tag.setId(1);
        tag.setPosts(List.of(post));
        post.setTags(List.of(tag));

        doReturn(new PageImpl<>(List.of(user))).when(userRepository).getUsersSearch(anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyInt(), any());
        doThrow(UsernameNotFoundException.class).when(userRepository).findUserById(999999);
        doReturn(Optional.of(user)).when(userRepository).findUserById(5);
        doReturn(Optional.of(user)).when(userRepository).findUserById(1);
        doReturn(new PageImpl<>(List.of(post))).when(postRepository).getAllPostByUsers(any(), any(), any());
        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doAnswer(i -> i.getArguments()[0]).when(postRepository).save(any(Post.class));
        doReturn(List.of(userFriend)).when(userRepository).getAllMyFriends(any());
        doReturn(List.of(notification)).when(notificationRepository).findByUserAndEntityId(anyInt(), anyInt(), any());
        doReturn(Optional.of((byte) 0)).when(notificationSettingRepository).getNotificationSetting(any(), any());
        doReturn(List.of(postComment)).when(postCommentRepository).searchByPost(any());
        doReturn(Optional.of(postLike)).when(postLikeRepository).findMyLikeInComment(anyInt(), anyInt());
        doReturn(List.of(tag.getName())).when(tagRepository).getNameByPost(anyInt());

    }

    @Test
    @DisplayName("Testing run Spring Boot")
    public void contextLoads() throws Exception {
    }

    @Test
    @DisplayName("Controller @NotNull")
    public void controllerNotNull() throws Exception {
        assertThat(profileService).isNotNull();
    }

    @Test
    @DisplayName("Checking the error noAuthentication in the response")
    public void checkErrorsInResponse() throws Exception {
        doThrow(UsernameNotFoundException.class).when(userRepository).findByEmail(anyString());
        //noAuthentication
        this.mockMvc.perform(get(port + "/api/v1/users/search"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Checking search users and success response")
    @WithMockUser(username = "user@mail.ru")
    public void checkSearchUsersSuccessResponse() throws Exception {


        this.mockMvc.perform(get(port + "/api/v1/users/search?first_name=Анн"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));

        this.mockMvc.perform(get("/api/v1/users/search?last_name=Черн"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));

        this.mockMvc.perform(get("/api/v1/users/search?age_from=20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));

        this.mockMvc.perform(get("/api/v1/users/search?age_to=35"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));

        this.mockMvc.perform(get("/api/v1/users/search?city=Moscow"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));

        this.mockMvc.perform(get("/api/v1/users/search?country=Russia"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));
    }

    @Test
    @DisplayName("Looking for not existing user with id. (400 BAD REQUEST)")
    @WithMockUser
    public void shouldReturnUserNotFoundException() throws Exception {
        this.mockMvc
                .perform(get(port + "/api/v1/users/999999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get user profile. (200 OK)")
    @WithMockUser
    public void shouldReturnUserProfile() throws Exception {
        this.mockMvc
                .perform(get(this.port + "/api/v1/users/5"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get current logged user. (200 OK)")
    @WithMockUser
    public void shouldReturnCurrentUser() throws Exception {
        mockMvc
                .perform(get(this.port + "/api/v1/users/me"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get wall")
    @WithMockUser
    public void getWall() throws Exception {
        mockMvc
                .perform(get(this.port + "/api/v1/users/1/wall"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Looking wall for not existing user with id. (400 BAD REQUEST)")
    @WithMockUser
    public void wallShouldReturnUserNotFoundException() throws Exception {
        mockMvc
                .perform(get(this.port + "/api/v1/users/9999999/wall"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Checking the error noAuthentication in the wall response")
    public void wallShouldReturnErrorsInResponse() throws Exception {
        doThrow(UsernameNotFoundException.class).when(userRepository).findByEmail(anyString());
        //noAuthentication
        this.mockMvc.perform(get(port + "/api/v1/users/3/wall"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Post wall")
    @WithMockUser(username = "user@mail.ru")
    public void postWall() throws Exception {
        mockMvc
                .perform(post("/api/v1/users/1/wall")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"title\" : \"title\",\"post_text\": \"post_text\"}")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Checking the error noAuthentication in the wall post response")
    public void wallPostShouldReturnErrorsInResponse() throws Exception {
        doThrow(UsernameNotFoundException.class).when(userRepository).findByEmail(anyString());
        //noAuthentication
        this.mockMvc.perform(post("/api/v1/users/3/wall")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"title\" : \"title\",\"post_text\": \"post_text\"}")))
                .andExpect(status().isUnauthorized());
    }


}
