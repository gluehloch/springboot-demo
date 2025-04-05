package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @DisplayName("Show principal usage of JUnit with Spring-Boot")
    @Test
    @Tag("service")
    @Transactional
    void createAndFindUser() {
        final UserProfile user = userService.create("Frosch", "Winkler", "Andre", "Password");

        final UserCredentials persistedUser = userService.findByNickname(Nickname.of("Frosch")).orElseThrow();
        assertThat(persistedUser).isNotNull();
        assertThat(persistedUser.firstname()).isEqualTo("Andre");
        assertThat(persistedUser.name()).isEqualTo("Winkler");
        assertThat(persistedUser.password()).isEqualTo("Password");

        final UserCredentials findByNickname = userService.findByNickname(Nickname.of("Frosch")).orElseThrow();
        assertThat(findByNickname).isEqualTo(persistedUser);
    }

}
