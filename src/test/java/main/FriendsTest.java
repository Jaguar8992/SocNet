package main;

import main.model.repository.FriendshipRepository;
import main.model.repository.UserRepository;
import main.service.friends.FriendsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Testing friends")
public class FriendsTest {
    public FriendsTest() {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FriendsService friendsService;

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Testing run Spring Boot")
    void contextLoads() throws Exception {
    }

    @Test
    @DisplayName("Controller @NotNull")
    public void controllerNotNull() throws Exception {
        assertThat(friendsService).isNotNull();
    }

    @Test
    @DisplayName("Checking the error in the response")
    @WithUserDetails("SergeevaAnna98@ya.ru")
    public void checkErrorsInResponse() throws Exception {
        this.mockMvc.perform(get("/api/v1/friends/recommendations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));


        this.mockMvc.perform(get("/api/v1/friends/request?name=Анна"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));


        this.mockMvc.perform(get("/api/v1/friends?name=Анна"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));
    }

}