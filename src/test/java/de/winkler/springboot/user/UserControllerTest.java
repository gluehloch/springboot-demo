package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginService loginService;

    @Test
    @Transactional
    public void shouldReturnSomeUsers() throws Exception {
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

        //
        // Get all users
        //

        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Winkler")))
                .andExpect(jsonPath("$[1].name", is("NachnameA")))
                .andExpect(jsonPath("$[2].name", is("NachnameB")))
                .andExpect(content().string(containsString("Frosch")));

        UserEntity testC = UserEntity.UserBuilder.of("TestC", "PasswordTestC")
                .firstname("VornameC")
                .name("NachnameC")
                .build();

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

        String authorizationHeader = result.getResponse().getHeader(SecurityConstants.HEADER_STRING);
        String jwt = authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, " ");

        Optional<String> validate = loginService.validate(jwt);
        assertThat(validate).isPresent().get().isEqualTo("Frosch");

        //
        // Create user
        //

        this.mockMvc.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt)
                        .content(ObjectToJsonString.toString(testC)))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Winkler")))
                .andExpect(jsonPath("$[1].name", is("NachnameA")))
                .andExpect(jsonPath("$[2].name", is("NachnameB")))
                .andExpect(jsonPath("$[3].name", is("NachnameC")));

        UserEntity persistedUserC = userRepository.findByNickname("TestC");
        testC.setId(persistedUserC.getId());

        //
        // Update user
        //

        testC.setName("NachnameC_Neu");
        this.mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt)
                        .content(ObjectToJsonString.toString(testC)))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Winkler")))
                .andExpect(jsonPath("$[1].name", is("NachnameA")))
                .andExpect(jsonPath("$[2].name", is("NachnameB")))
                .andExpect(jsonPath("$[3].name", is("NachnameC_Neu")));
    }

}
