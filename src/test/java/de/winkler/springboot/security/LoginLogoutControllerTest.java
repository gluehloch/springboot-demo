package de.winkler.springboot.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import de.winkler.springboot.JsonUtils;
import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.SecurityConstants;
import de.winkler.springboot.user.internal.PrivilegeRepository;
import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.RoleRepository;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class LoginLogoutControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

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

        final var resultLogin = this.mockMvcTester.perform(
                post("/login")
                        .param("nickname", "Frosch")
                        .param("password", "Password"));
        assertThat(resultLogin).hasStatus2xxSuccessful();

        // Get the JWT from the response header ...
        Optional<String> authorizationHeader = Optional.ofNullable(
                resultLogin.getResponse().getHeader(SecurityConstants.HEADER_STRING));
        assertThat(authorizationHeader).isPresent();

        Optional<String> jwt = authorizationHeader.map(s -> s.replace(SecurityConstants.TOKEN_PREFIX, ""));
        Optional<Nickname> validate = jwt.flatMap(token -> loginService.validate(token));
        assertThat(validate).isPresent().map(Nickname::value).contains("Frosch");

        // alternative code and more functional...
        Optional<Nickname> nickname = Optional
                .ofNullable(resultLogin.getResponse().getHeader(SecurityConstants.HEADER_STRING))
                .map(s -> s.replace(SecurityConstants.TOKEN_PREFIX, ""))
                .flatMap(token -> loginService.validate(token));
        assertThat(nickname).isPresent().map(Nickname::value).contains("Frosch");

        //
        // Get my own user data. Should work...
        //

       final var resultUserGetFrosch = this.mockMvcTester.perform(
                get("/user/Frosch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt.get()));
       assertThat(resultUserGetFrosch).hasStatus(HttpStatus.OK);

       final var resultUserGetAnotherUser = this.mockMvcTester.perform(
                get("/user/AnotherUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt.get()));
       assertThat(resultUserGetAnotherUser).hasStatus(HttpStatus.FORBIDDEN);

        //
        // Logout
        //

        final var resultPostLogout = this.mockMvcTester.perform(
                post("/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt.get())
                        .content(JsonUtils.toString(validate.get())));
        assertThat(resultPostLogout).hasStatus(HttpStatus.OK);
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
