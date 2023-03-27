package de.winkler.springboot.security;

import de.winkler.springboot.JsonUtils;
import de.winkler.springboot.user.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginLogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private LoginService loginService;

    @Test
    @Tag("controller")
    @Transactional
    void loginLogout() throws Exception {
        prepareDatabase();

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

        // Get the JWT from the response header ...
        String authorizationHeader = result.getResponse().getHeader(SecurityConstants.HEADER_STRING);
        String jwt = authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
        // ... and validate the token.
        Optional<Nickname> validate = loginService.validate(jwt);
        assertThat(validate).isPresent().map(Nickname::value).contains("Frosch");

        //
        // Get my own user data. Should work...
        //

        this.mockMvc.perform(
                get("/user/Frosch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt))
                .andDo(print()).andExpect(status().isOk());

        this.mockMvc.perform(
                get("/user/AnotherUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt))
                .andDo(print()).andExpect(status().isForbidden());

        //
        // Logout
        //

        this.mockMvc.perform(
                post("/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt)
                        .content(JsonUtils.toString(validate.get())))
                .andExpect(status().isOk());
    }

    private void prepareDatabase() {
        UserEntity frosch = UserEntity.UserBuilder
                .of(Nickname.of("Frosch"), "Password")
                .firstname("Andre")
                .name("Winkler")
                .build();
        frosch = userRepository.save(frosch);

        UserEntity anotherUser = UserEntity.UserBuilder
                .of(Nickname.of("AnotherUser"), "Password")
                .firstname("Another")
                .name("User")
                .build();
        anotherUser = userRepository.save(anotherUser);

        RoleEntity userRole = RoleEntity.RoleBuilder.of("ROLE_USER");
        frosch.addRole(userRole);
        anotherUser.addRole(userRole);

        roleRepository.save(userRole);
    }

}
