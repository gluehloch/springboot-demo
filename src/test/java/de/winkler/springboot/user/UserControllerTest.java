package de.winkler.springboot.user;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import de.winkler.springboot.ObjectToJsonString;
import de.winkler.springboot.security.LoginService;

/**
 * Login, create, update, logout, update. Check to control, that authentication and authorization is working.
 *
 * TODO It is hard to read this test.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginService loginService;

    @Test
    @Tag("controller")
    @DisplayName("Controller Test: Find some users, login, update user with and without credentials.")
    void shouldReturnSomeUsers() throws Exception {
        prepareDatabase();

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

        UserEntity persistedUserC = userRepository.findByNickname("TestC").orElseThrow();
        testC.setId(persistedUserC.getId());

        UserEntity persistedFrosch = userRepository.findByNickname("Frosch").orElseThrow();

        //
        // Update user
        //

        // Some user can´t change the user data of another user.
        testC.setName("NachnameC_Neu");
        this.mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt)
                        .content(ObjectToJsonString.toString(testC)))
                .andExpect(status().isForbidden());

        // Only the logged user can change his own user data.
        persistedFrosch.setFirstname("Erwin");
        persistedFrosch.setName("WinklerNeu");
        this.mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt)
                        .content(ObjectToJsonString.toString(persistedFrosch)))
                .andExpect(status().isOk());

        // Some random user wants to update ... but gets a forbidden response.
        UserJson fantasyUser = new UserJson();
        fantasyUser.setNickname("Fantasy");
        this.mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt)
                        .content(ObjectToJsonString.toString(fantasyUser)))
                .andExpect(status().isForbidden());

        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("WinklerNeu")))
                .andExpect(jsonPath("$[1].name", is("NachnameA")))
                .andExpect(jsonPath("$[2].name", is("NachnameB")))
                .andExpect(jsonPath("$[3].name", is("NachnameC")));

        //
        // Update without Jason Web Token
        //
        testC.setName("NachnameC_Neu");
        this.mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectToJsonString.toString(testC)))
                .andExpect(status().isForbidden());
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
