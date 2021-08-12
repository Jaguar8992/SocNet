package main;

import junit.framework.TestCase;
import main.model.repository.PostRepository;
import main.service.PostService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Testing posts")
public class PostTest extends TestCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @LocalServerPort
    private String port;

    @Test
    @DisplayName("Testing run Spring Boot")
    public void contextLoads() throws Exception {
    }

    @Test
    @DisplayName("Controller @NotNull")
    public void controllerNotNull() throws Exception {
        assertThat(postService).isNotNull();
    }

    @Test
    @DisplayName("Checking the error noAuthentication in the response")
    public void checkErrorsInResponse() throws Exception {
        //noAuthentication
        this.mockMvc.perform(get(port + "/api/v1/post"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Checking the error authentication in the response")
    @WithUserDetails("IvanovMaksim@ya.ru")
    public void givenUser_whenGetPost_thenOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/post")
                .accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

}


