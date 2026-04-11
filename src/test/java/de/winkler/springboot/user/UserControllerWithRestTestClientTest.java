package de.winkler.springboot.user;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient.BodySpec;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.RoleRepository;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;

@SpringBootTest
@Transactional
// @WebMvcTest(UserController.class)
class UserControllerWithRestTestClientTest {

    @Autowired
    private WebApplicationContext applicationContext;

    //    @Autowired
    //    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void before() {
        prepareDatabase();
    }

    @Test
    @Tag("controller")
    @DisplayName("Only users with the role ADMIN should be able to see all users.")
    void onlyAdminCanSeeAllUsers() {
        final var client = RestTestClient.bindToApplicationContext(applicationContext).build();
        // final var client = RestTestClient.bindTo(mockMvc).build();

        // expcted:
        // client.get().uri("/user").exchange().expectStatus().isUnauthorized();
        ParameterizedTypeReference<List<UserProfileImpl>> typeRef = new ParameterizedTypeReference<List<UserProfileImpl>>() {};
        
        BodySpec<List<UserProfileImpl>,?> expectBody = client.get().uri("/user")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<List<UserProfileImpl>>() {});
    }

    private void prepareDatabase() {
        UserEntity frosch = UserEntity.UserBuilder
                .of(Nickname.of("Frosch"), "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .build();

        UserEntity testA = UserEntity.UserBuilder.of(Nickname.of("TestA"), "PasswordTestA")
                .firstname("VornameA")
                .name("NachnameA")
                .build();

        UserEntity testB = UserEntity.UserBuilder.of(Nickname.of("TestB"), "PasswordTestB")
                .firstname("VornameB")
                .name("NachnameB")
                .build();

        UserEntity admin = UserEntity.UserBuilder.of(Nickname.of("ADMIN"), "secret-password")
                .firstname("admin")
                .name("admin")
                .build();

        userRepository.saveAll(List.of(frosch, testA, testB, admin));

        RoleEntity adminRole = RoleEntity.RoleBuilder.of("ROLE_ADMIN");
        RoleEntity userRole = RoleEntity.RoleBuilder.of("ROLE_USER");
        roleRepository.saveAll(List.of(adminRole, userRole));

        admin.addRole(adminRole);
        frosch.addRole(userRole);
        userRepository.saveAll(List.of(admin, frosch));
    }

}
