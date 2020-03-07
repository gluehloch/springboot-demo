package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserTest {

    @Autowired
    UserService userService;

    @Test
    public void createAndFindUser() {
        UserEntity user = userService.createUser("Winkler", "Andre");
        assertThat(user.getId()).isNotNull();

        UserEntity persistedUser = userService.findUser("test");
        assertThat(persistedUser).isNotNull();
    }

}