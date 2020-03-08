package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {

    @Autowired
    UserService userService;

    @DisplayName("Show principal usage of JUnit with Spring-Boot")
    @Test
    public void createAndFindUser() {
        UserEntity user = userService.createUser("Winkler", "Andre");
        assertThat(user.getId()).isNotNull();

        UserEntity persistedUser = userService.findUser("test");
        assertThat(persistedUser).isNotNull();
    }

}