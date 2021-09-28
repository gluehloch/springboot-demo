package de.winkler.springboot.security;

import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.Token;
import de.winkler.springboot.user.UserEntity;
import de.winkler.springboot.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
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

        Optional<String> validate = loginService.validate(token.getContent());
        assertThat(validate).isPresent().get().isEqualTo("Frosch");
    }

}
