package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
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

        final UserEntity persistedUser = userService.findByName("Winkler");
        assertThat(persistedUser).isNotNull();
        assertThat(persistedUser.getFirstname()).isEqualTo("Andre");
        assertThat(persistedUser.getName()).isEqualTo("Winkler");
        assertThat(persistedUser.getPassword()).isEqualTo("Password");

        final UserEntity findByNickname = userService.findByNickname("Frosch");
        assertThat(findByNickname).isEqualTo(persistedUser);
    }

}
