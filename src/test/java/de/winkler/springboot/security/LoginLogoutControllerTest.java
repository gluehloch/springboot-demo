package de.winkler.springboot.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

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

import de.winkler.springboot.JsonUtils;
import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.RoleRepository;
import de.winkler.springboot.user.SecurityConstants;
import de.winkler.springboot.user.internal.PrivilegeRepository;
import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;

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
        assertThat(this.privilegeRepository.count()).isZero();
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
        Optional<String> authorizationHeader = Optional.ofNullable(
                result.getResponse().getHeader(SecurityConstants.HEADER_STRING));
        assertThat(authorizationHeader).isPresent();

        Optional<String> jwt = authorizationHeader.map(s -> s.replace(SecurityConstants.TOKEN_PREFIX, ""));
        Optional<Nickname> validate = jwt.flatMap(token -> loginService.validate(token));
        assertThat(validate).isPresent().map(Nickname::value).contains("Frosch");

        // alternative code and more functional...
        Optional<Nickname> nickname = Optional
                .ofNullable(result.getResponse().getHeader(SecurityConstants.HEADER_STRING))
                .map(s -> s.replace(SecurityConstants.TOKEN_PREFIX, ""))
                .flatMap(token -> loginService.validate(token));
        assertThat(nickname).isPresent().map(Nickname::value).contains("Frosch");

        //
        // Get my own user data. Should work...
        //

        this.mockMvc.perform(
                get("/user/Frosch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt.get()))
                .andDo(print()).andExpect(status().isOk());

        this.mockMvc.perform(
                get("/user/AnotherUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt.get()))
                .andDo(print()).andExpect(status().isForbidden());

        //
        // Logout
        //

        this.mockMvc.perform(
                post("/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt.get())
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
