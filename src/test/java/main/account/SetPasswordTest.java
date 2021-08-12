package main.account;


import main.model.entity.TokenToUser;
import main.model.repository.TokenToUserRepository;
import main.service.account.SetPasswordService;
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
@DisplayName("Смена пароля")
public class SetPasswordTest {
    public SetPasswordTest() {
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SetPasswordService setPasswordService;
    @Autowired
    private TokenToUserRepository tokenToUserRepository;

    @Test
    @DisplayName("Смена пароля")
    public void setPassword() throws Exception {
        String token = "testToken";
        TokenToUser testToken = new TokenToUser();
        testToken.setToken(token);
        testToken.setUserId(2);
        tokenToUserRepository.save(testToken);

        {
            mockMvc.perform(put("/api/v1/account/password/set")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(("{\"token\" : \"testToken\" , \"password\" : \"Ab123456\"}")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("message\":\"ok")));
        }
    }
}

