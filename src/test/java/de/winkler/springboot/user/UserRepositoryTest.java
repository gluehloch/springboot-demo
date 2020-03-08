package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @DisplayName("Repository test: Find all users")
    @Test
    @Transactional
    public void findUser() {
        UserEntity user = new UserEntity();
        user.setNickname("Frosch");
        user.setFirstname("Andre");
        user.setName("Winkler");
        user.setPassword("Password");
        user = userRepository.save(user);
        assertThat(user.getId()).isNotNull();

        UserEntity persistedUser = userRepository.findByName("Winkler");
        assertThat(persistedUser).isNotNull();
        assertThat(persistedUser.getFirstname()).isEqualTo("Andre");
        assertThat(persistedUser.getName()).isEqualTo("Winkler");
        assertThat(persistedUser.getNickname()).isEqualTo("Frosch");

        assertThat(persistedUser).isEqualTo(user);
    }

}
