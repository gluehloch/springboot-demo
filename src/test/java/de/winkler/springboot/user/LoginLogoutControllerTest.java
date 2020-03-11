package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import de.winkler.springboot.ObjectToJsonString;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginLogoutControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void loginLogout() throws Exception {
        UserEntity frosch = UserEntity.UserBuilder
                .of("Frosch", "Password")
                .firstname("Andre")
                .name("Winkler")
                .build();

        frosch = userRepository.save(frosch);

        //
        // Login
        //

        ResultActions loginAction = this.mockMvc.perform(
                post("/login")
                        .param("nickname", "Frosch")
                        .param("password", "Password"))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult result = loginAction.andReturn();

        // TODO
        String contentAsString = result.getResponse().getContentAsString();

        Token response = objectMapper.readValue(contentAsString, Token.class);
        assertThat(response.getContent()).isNotNull();

        //
        // Logout
        //

        ResultActions logoutAction = this.mockMvc.perform(
                post("/logout").contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectToJsonString.toString(response)))
                .andExpect(status().isNoContent());
    }

}
