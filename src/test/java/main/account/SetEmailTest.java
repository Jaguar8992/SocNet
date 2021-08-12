package main.account;


import main.model.entity.User;
import main.model.repository.UserRepository;
import main.service.account.SetEmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Смена эллектронной почты")
public class SetEmailTest {
    public SetEmailTest() {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SetEmailService setEmailService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Устанавливаем тестовые данные")
    @WithUserDetails("SergeevaAnna98@ya.ru")
    public void setTestEmail() throws Exception {
        mockMvc.perform(put("/api/v1/account/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"email\" : \"wwwemail@mail.com\"}")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("message\":\"ok")));
    }

    @AfterEach
    public void setBackEmail() throws Exception {
        Optional<User> userOptional = userRepository.findByEmail("wwwemail@mail.com");
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail("SergeevaAnna98@ya.ru");
            userRepository.save(user);
        }
    }
}

