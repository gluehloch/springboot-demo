package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @DisplayName("Repository test: Find all users")
    @Test
    public void findUsers() {
        UserEntity user = new UserEntity();
        user.setFirstname("Andre");
        user.setName("Winkler");
        user = userRepository.save(user);

        user = userRepository.findByName("Winkler");
        assertThat(user).isNotNull();
        assertThat(user.getFirstname()).isEqualTo("Andre");
        assertThat(user.getName()).isEqualTo("Winkler");
    }

}
