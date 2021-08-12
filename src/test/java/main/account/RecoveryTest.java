package main.account;

import main.service.account.RecoveryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Тестирование отправки ссылки для восстановления")
public class RecoveryTest {
    public RecoveryTest() {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecoveryService service;

    @Test
    @DisplayName("Сообщение отправленно на почту")
    public void sendEmail() throws Exception {
        mockMvc.perform(put("/api/v1/account/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"email\": \"NikSok1992@mail.ru\"}")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("message\":\"ok")));
    }
}

