package main;

import main.service.dialog.MessageService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Send message")
public class MessageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageService messageService;

    @Test
    @DisplayName("Send message")
    @WithUserDetails("IvanovMaksim@ya.ru")
    public void postNewDialog() throws Exception {
        mockMvc.perform(post("/api/v1/dialogs/1/messages?message_text=Hello, its test message"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"String")));
    }
}
