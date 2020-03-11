package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class LoginServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @DisplayName("Login, validate and logout a user from a session.")
    @Test
    @Transactional
    public void loginLogout() {
        final UserEntity user = userService
                .create("Frosch", "Winkler", "Andre", "Password");
        assertThat(user.getId()).isNotNull();

        Token token = loginService.login("Frosch", "Password");
        assertThat(token).isNotNull();
        assertThat(token.getContent()).isNotEmpty();
        assertThat(loginService.validate(token)).isTrue();
    }

}
