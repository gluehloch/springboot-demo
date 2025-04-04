package de.winkler.springboot.security;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.Token;
import de.winkler.springboot.user.UserProfile;
import de.winkler.springboot.user.UserService;

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
        final UserProfile user = userService.create("Frosch", "Winkler", "Andre", "Password");

        boolean loggedIn = loginService.login(Nickname.of("Frosch"), "Password");
        assertThat(loggedIn).isTrue();
    }

    @DisplayName("Validate JWT.")
    @Test
    @Transactional
    void validateToken() {
        final UserProfile user = userService.create("Frosch", "Winkler", "Andre", "Password");
        final UserDetails userDetails = loginService.loadUserByUsername(user.nickname().value());

        Token token = loginService.token(userDetails);
        assertThat(token).isNotNull();
        assertThat(loginService.validate(token.getContent())).isPresent().map(Nickname::value).contains("Frosch");
    }

}
