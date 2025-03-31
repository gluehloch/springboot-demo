package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

import de.winkler.springboot.user.internal.UserEntity;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @DisplayName("Show principal usage of JUnit with Spring-Boot")
    @Test
    @Tag("service")
    @Transactional
    void createAndFindUser() {
        final UserEntity user = userService.create("Frosch", "Winkler", "Andre", "Password");
        assertThat(user.getId()).isNotNull();

        final User persistedUser = userService.findByName("Winkler").orElseThrow();
        assertThat(persistedUser).isNotNull();
        assertThat(persistedUser.firstname()).isEqualTo("Andre");
        assertThat(persistedUser.name()).isEqualTo("Winkler");
        assertThat(persistedUser.password()).isEqualTo("Password");

        final User findByNickname = userService.findByNickname(Nickname.of("Frosch")).orElseThrow();
        assertThat(findByNickname).isEqualTo(persistedUser);
    }

}
