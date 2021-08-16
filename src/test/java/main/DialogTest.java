package main;

import main.service.dialog.DialogService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Testing feeds")
public class DialogTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DialogService dialogService;

    @LocalServerPort
    private String port;

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
        //noAuthentication
        this.mockMvc.perform(get(port + "/api/v1/dialogs"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Checking the error authentication in the response")
    @WithUserDetails("IvanovMaksim@ya.ru")
    public void getDialogs_ThenUserAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/dialogs")
                .accept(MediaType.ALL))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/dialogs?query=прив")
                .accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Checking the error authentication in the response")
    @WithUserDetails("IvanovMaksim@ya.ru")
    public void getMessagesDialog() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/dialogs/{1}/messages")
                .accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get unread message")
    @WithUserDetails("IvanovMaksim@ya.ru")
    public void getUnreadMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/dialogs/unreaded"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));
    }

    @Test
    @DisplayName("Post new Dialog")
    @WithUserDetails("IvanovMaksim@ya.ru")
    public void postNewDialog () throws Exception {
        mockMvc.perform(post("/api/v1/dialogs/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"user_ids\" : [2,3]}")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));
    }
}
