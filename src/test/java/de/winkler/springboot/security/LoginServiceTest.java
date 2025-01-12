package de.winkler.springboot.security;

import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.Token;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoginServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @DisplayName("Login, validate and logout a user from a session.")
    @Tag("service")
    @Test
    @Transactional
    void loginLogout() {
        final UserEntity user = userService.create("Frosch", "Winkler", "Andre", "Password");
        assertThat(user.getId()).isNotNull();

        boolean loggedIn = loginService.login(Nickname.of("Frosch"), "Password");
        assertThat(loggedIn).isTrue();
    }

    @DisplayName("Validate JWT.")
    @Test
    @Transactional
    void validateToken() {
        final UserEntity user = userService.create("Frosch", "Winkler", "Andre", "Password");
        final UserDetails userDetails = loginService.loadUserByUsername(user.getNickname().value());

        Token token = loginService.token(userDetails);
        assertThat(token).isNotNull();
        assertThat(loginService.validate(token.getContent())).isPresent().map(Nickname::value).contains("Frosch");
    }

}
