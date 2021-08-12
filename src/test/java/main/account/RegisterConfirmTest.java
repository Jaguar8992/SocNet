package main.account;


import main.model.entity.TokenToUser;
import main.model.repository.TokenToUserRepository;
import main.service.account.RegisterConfirmService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Подтверждение регистрации")
public class RegisterConfirmTest {
    public RegisterConfirmTest() {
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RegisterConfirmService registerConfirmService;
    @Autowired
    private TokenToUserRepository tokenToUserRepository;

    @Test
    @DisplayName("Подтверждение")
    public void setPassword() throws Exception {
        String token = "token";
        TokenToUser testToken = new TokenToUser();
        testToken.setToken(token);
        testToken.setUserId(3);
        tokenToUserRepository.save(testToken);

        {
            mockMvc.perform(post("/api/v1/account/register/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(("{\"token\" : \"token\",\"userId\" : \"3\"}")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("message\":\"ok")));
        }
    }
}

