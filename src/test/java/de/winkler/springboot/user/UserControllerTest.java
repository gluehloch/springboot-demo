package de.winkler.springboot.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import de.winkler.springboot.ControllerUtils;
import de.winkler.springboot.JsonUtils;
import de.winkler.springboot.security.LoginService;
import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.RoleRepository;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;

/**
 * Login, create, update, logout, update. Check to control, that authentication and authorization is working.
 *
 * TODO It is hard to read this test.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LoginService loginService;

    @BeforeEach
    void before() {
        prepareDatabase();
    }

    @Test
    @Tag("controller")
    @DisplayName("Controller Test: Only ADMIN can see all users.")
    void onlyAdminCanSeeAllUsers() throws Exception {
        //
        // Try to get all users without ADMIN login.
        //
        final var usersResult = this.mockMvcTester.get().uri("/user").exchange();
        assertThat(usersResult).hasStatus(HttpStatus.FORBIDDEN);

        //
        // Login
        //
        String jwt = ControllerUtils.loginAndGetToken(mockMvcTester,"ADMIN", "secret-password").orElseThrow();

        Optional<Nickname> validate = loginService.validate(jwt);
        assertThat(validate).isPresent().map(Nickname::value).contains("ADMIN");
        
        //
        // Get all users with login
        //
        final var result = this.mockMvcTester.perform(get("/user").header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt));
        assertThat(result).hasStatus(HttpStatus.OK);

        final String json = result.getMvcResult().getResponse().getContentAsString();     
        assertThat(json).isEqualToIgnoringWhitespace("""
                 [
                     {"nickname":{"value":"Frosch"},"name":"Winkler","firstname":"Andre","age":0,"roles":[{"name":"ROLE_USER"}]},
                     {"nickname":{"value":"TestA"},"name":"NachnameA","firstname":"VornameA","age":0,"roles":[]},
                     {"nickname":{"value":"TestB"},"name":"NachnameB","firstname":"VornameB","age":0,"roles":[]},
                     {"nickname":{"value":"ADMIN"},"name":"admin","firstname":"admin","age":0,"roles":[{"name":"ROLE_ADMIN"}]}]
                """);
    }

    @Test
    @Tag("controller")
    @DisplayName("Controller Test: Only ADMIN can create a user.")
    void onlyAdminCanCreateUser() throws Exception {
        //
        // User login
        //
        String userJwt = ControllerUtils.loginAndGetToken(mockMvcTester, "Frosch", "PasswordFrosch").orElseThrow();
        assertThat(loginService.validate(userJwt)).isPresent().map(Nickname::value).contains("Frosch");

        //
        // Create user without login and admin role.
        //
        UserEntity testC = UserEntity.UserBuilder.of(Nickname.of("TestC"), "PasswordTestC")
                .firstname("VornameC")
                .name("NachnameC")
                .build();

        final var result = this.mockMvcTester.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + userJwt)
                        .content(JsonUtils.toString(UserEntityMapper.to(testC))));
        assertThat(result).hasStatus(HttpStatus.FORBIDDEN);

        //
        // Admin login
        //
        String adminJwt = ControllerUtils.loginAndGetToken(mockMvcTester, "ADMIN", "secret-password").orElseThrow();
        assertThat(loginService.validate(adminJwt)).isPresent().map(Nickname::value).contains("ADMIN");

        //
        // Create user with admin credentials.
        //
        final var userJson = UserEntityMapper.toUserCredentials(testC);
        final var resultPostUser = this.mockMvcTester.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + adminJwt)
                        .content(JsonUtils.toString(userJson)));

        assertThat(resultPostUser).hasStatus(HttpStatus.CREATED);
        // assertThat(resultPostUser).hasRedirectedUrl("http://localhost/user/TestC");
        assertThat(resultPostUser.getMvcResult().getResponse().getHeader("Location")).isEqualTo("http://localhost/user/TestC");
        

        final var resultGetUsers = this.mockMvcTester.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + adminJwt));

        assertThat(resultGetUsers).hasStatus(HttpStatus.OK);
        final String json = resultGetUsers.getMvcResult().getResponse().getContentAsString();
        
        assertThat(json).isEqualToIgnoringWhitespace("""
                [
                    {"nickname":{"value":"Frosch"},"name":"Winkler","firstname":"Andre","age":0,"roles":[{"name":"ROLE_USER"}]},
                    {"nickname":{"value":"TestA"},"name":"NachnameA","firstname":"VornameA","age":0,"roles":[]},
                    {"nickname":{"value":"TestB"},"name":"NachnameB","firstname":"VornameB","age":0,"roles":[]},
                    {"nickname":{"value":"ADMIN"},"name":"admin","firstname":"admin","age":0,"roles":[{"name":"ROLE_ADMIN"}]},
                    {"nickname":{"value":"TestC"},"name":"NachnameC","firstname":"VornameC","age":0,"roles":[]}    
                ]
               """);

        List<UserEntity> allUsers = JsonUtils.toList(json);
        assertThat(allUsers).extracting("nickname.value", "name", "firstname")
                .contains(
                        tuple("Frosch", "Winkler", "Andre"),
                        tuple("TestA", "NachnameA", "VornameA"),
                        tuple("TestB", "NachnameB", "VornameB"),
                        tuple("TestC", "NachnameC", "VornameC"),
                        tuple("ADMIN", "admin", "admin"));

        UserEntity persistedUserC = userRepository.findByNickname(Nickname.of("TestC")).orElseThrow();
        assertThat(persistedUserC.getNickname().value()).isEqualTo("TestC");
        UserEntity persistedFrosch = userRepository.findByNickname(Nickname.of("Frosch")).orElseThrow();
        assertThat(persistedFrosch.getNickname().value()).isEqualTo("Frosch");
    }

    @Test
    @Tag("controller")
    @DisplayName("Controller Test: Find some users, login, update user with and without credentials.")
    void shouldReturnSomeUsers() throws Exception {
        UserEntity testC = UserEntity.UserBuilder.of(Nickname.of("TestC"), "PasswordTestC")
                .firstname("VornameC")
                .name("NachnameC")
                .build();

        //
        // Login
        //
        String froschJwt = ControllerUtils.loginAndGetToken(mockMvcTester, "Frosch", "PasswordFrosch").orElseThrow();

        Optional<Nickname> validate = loginService.validate(froschJwt);
        assertThat(validate).isPresent().map(Nickname::value).contains("Frosch");
        UserEntity persistedFrosch = userRepository.findByNickname(Nickname.of("Frosch")).orElseThrow();

        //
        // Update user
        //

        // Some user can´t change the user data of another user.
        testC.setName("NachnameC_Neu");
        final var updateUserC = JsonUtils.toString(testC);
        final var resultPutUser = this.mockMvcTester.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt)
                        .content(updateUserC));

        assertThat(resultPutUser).hasStatus(HttpStatus.FORBIDDEN);

        // Only the logged user can change his own user data.
        persistedFrosch.setFirstname("Erwin");
        persistedFrosch.setName("WinklerNeu");

        final var updateUserJson = JsonUtils.toString(UserEntityMapper.toUserCredentials(persistedFrosch));
        final var resultPutUserWithAccessToken = this.mockMvcTester.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt)
                        .content(updateUserJson));
        assertThat(resultPutUserWithAccessToken).hasStatus(HttpStatus.OK);

        // The logged user wants to update his role.
        final var resultPutUserRole = this.mockMvcTester.perform(
                put("/user/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt)
                        .param("nickname", "Frosch")
                        .param("role", "ROLE_USER")
                        .content(JsonUtils.toString(persistedFrosch)));

        assertThat(resultPutUserRole).hasStatus(HttpStatus.FORBIDDEN);


        // Some random user wants to update ... but gets a forbidden response.
        UserUpdateJson fantasyUser = new UserUpdateJson();
        fantasyUser.setNickname(Nickname.of("Fantasy"));
        final var putUserChangeAnotherUser = this.mockMvcTester.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt)
                        .content(JsonUtils.toString(fantasyUser)));
        
        assertThat(putUserChangeAnotherUser).hasStatus(HttpStatus.FORBIDDEN);

        //
        // Update without Jason Web Token
        //
        testC.setName("NachnameC_Neu");
        final var resultPuUserWithoutAccessToken = this.mockMvcTester.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toString(testC)));
        
        assertThat(resultPuUserWithoutAccessToken).hasStatus(HttpStatus.FORBIDDEN);
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
