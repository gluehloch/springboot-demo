package de.winkler.springboot.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import de.winkler.springboot.ControllerUtils;
import de.winkler.springboot.security.LoginService;
import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.SecurityConstants;
import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.RoleRepository;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Tag("controller")
    @Transactional
    @Rollback
    void order() throws Exception {
        prepareDatabase();

        //
        // Order without login
        //

        assertThat(this.mockMvcTester
                .perform(put("/order")
                        .contentType(MediaType.APPLICATION_JSON)))
                                .hasStatus(HttpStatus.FORBIDDEN);

        //
        // Login
        //

        final String froschJwt = ControllerUtils.loginAndGetToken(mockMvcTester, "Frosch", "PasswordFrosch");
        Optional<Nickname> validate = loginService.validate(froschJwt);
        assertThat(validate).isPresent().map(Nickname::value).contains("Frosch");

        //
        // Order: Security definition expects a logged user with role 'USER'.
        //

        final var result = this.mockMvcTester.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON).queryParam("wkn", "101-isin")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt));

        assertThat(result).hasStatus(HttpStatus.CREATED);
        
        final String contentAsString = result.getMvcResult().getResponse().getContentAsString();
        
        assertThat(contentAsString).isEqualToIgnoringWhitespace("""
                {
                    "nickname":"Frosch",
                    "orderItems":[{"isin":"101-isin","quantity":100}],
                    "uuid":null
                }
                """);
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

        userRepository.saveAll(List.of(frosch, testA, testB));

        RoleEntity userRole = RoleEntity.RoleBuilder.of("ROLE_USER");
        roleRepository.save(userRole);
        frosch.addRole(userRole);
        userRepository.save(frosch);
    }

}
