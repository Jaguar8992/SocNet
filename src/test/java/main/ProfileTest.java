package main;

import main.model.repository.UserRepository;
import main.service.profiles.ProfileService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Testing profile")
public class ProfileTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ProfileService profileService;

    @Autowired
    UserRepository userRepository;

    @LocalServerPort
    private String port;

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
        //noAuthentication
        this.mockMvc.perform(get(port + "/api/v1/users/search"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Checking search users and success response")
    @WithUserDetails("NikSok1992@mail.ru")
    public void checkSearchUsersSuccessResponse() throws Exception {
        this.mockMvc.perform(get("/api/v1/users/search?first_name=Анн"))
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

        this.mockMvc.perform(get("/api/v1/users/search?town_id=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));

        this.mockMvc.perform(get("/api/v1/users/search?country_id=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));
    }

    @Test
    @DisplayName("Looking for not existing user with id. (404 NOT FOUND)")
    @WithUserDetails("IvanovMaksim@ya.ru")
    public void shouldReturnUserNotFoundException() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(this.port + "/api/v1/users/999999"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Get user profile. (200 OK)")
    @WithUserDetails("IvanovMaksim@ya.ru")
    public void shouldReturnUserProfile() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(this.port + "/api/v1/users/5"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get current logged user. (200 OK)")
    @WithUserDetails("IvanovMaksim@ya.ru")
    public void shouldReturnCurrentUser() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(this.port + "/api/v1/users/me"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
