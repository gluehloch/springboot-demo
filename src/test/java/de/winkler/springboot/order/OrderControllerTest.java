package de.winkler.springboot.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import de.winkler.springboot.security.LoginService;
import de.winkler.springboot.user.SecurityConstants;
import de.winkler.springboot.user.UserEntity;
import de.winkler.springboot.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Tag("controller")
    @Transactional
    public void order() throws Exception {
        prepareDatabase();

        //
        // Login
        //

        ResultActions loginAction = this.mockMvc.perform(
                post("/login")
                        .param("nickname", "Frosch")
                        .param("password", "PasswordFrosch"))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult result = loginAction.andReturn();

        // Get the JWT from the response header ...
        String authorizationHeader = result.getResponse().getHeader(SecurityConstants.HEADER_STRING);
        String jwt = authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "");

        Optional<String> validate = loginService.validate(jwt);
        assertThat(validate).isPresent().get().isEqualTo("Frosch");

        //
        // Order
        //

        this.mockMvc.perform(put("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", is("Frosch")));

    }

    private void prepareDatabase() {
        UserEntity frosch = UserEntity.UserBuilder
                .of("Frosch", "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .build();

        UserEntity testA = UserEntity.UserBuilder.of("TestA", "PasswordTestA")
                .firstname("VornameA")
                .name("NachnameA")
                .build();

        UserEntity testB = UserEntity.UserBuilder.of("TestB", "PasswordTestB")
                .firstname("VornameB")
                .name("NachnameB")
                .build();

        userRepository.saveAll(List.of(frosch, testA, testB));
    }

}
