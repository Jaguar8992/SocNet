package main.account;

import main.model.entity.User;
import main.model.repository.TokenToUserRepository;
import main.model.repository.UserRepository;
import main.service.account.RegistrationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Регистрация")
public class RegistrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private TokenToUserRepository tokenToUserRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Регистрация")
    public void registration() throws Exception {
        mockMvc.perform(post("/api/v1/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"email\" : \"registration@mail.com\"," +
                        "\"passwd1\" : \"Ab123456\"," +
                        "\"passwd2\" : \"Ab123456\"," +
                        "\"lastName\" : \"TestName\"," +
                        "\"firstName\" : \"TestName\"," +
                        "\"token\" : \"\"}")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("message\":\"ok")));
    }

    @AfterEach
    public void setBackEmail() throws Exception {
        Optional<User> userOptional = userRepository.findByEmail("registration@mail.com");
        userOptional.ifPresent(user -> userRepository.delete(user));
    }
}
