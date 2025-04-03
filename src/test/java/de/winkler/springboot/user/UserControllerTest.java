package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

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
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isForbidden());

        //
        // Login
        //
        String jwt = ControllerUtils.loginAndGetToken(mockMvc,"ADMIN", "secret-password");

        Optional<Nickname> validate = loginService.validate(jwt);
        assertThat(validate).isPresent().map(Nickname::value).contains("ADMIN");
        
        //
        // Get all users
        //
        this.mockMvc.perform(get("/user").header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Winkler")))
                .andExpect(jsonPath("$[1].name", is("NachnameA")))
                .andExpect(jsonPath("$[2].name", is("NachnameB")))
                .andExpect(content().string(containsString("Frosch")));
    }

    @Test
    @Tag("controller")
    @DisplayName("Controller Test: Only ADMIN can create a user.")
    void onlyAdminCanCreateUser() throws Exception {
        //
        // User login
        //
        String userJwt = ControllerUtils.loginAndGetToken(mockMvc, "Frosch", "PasswordFrosch");
        assertThat(loginService.validate(userJwt)).isPresent().map(Nickname::value).contains("Frosch");

        //
        // Create user without login and admin role.
        //
        UserEntity testC = UserEntity.UserBuilder.of(Nickname.of("TestC"), "PasswordTestC")
                .firstname("VornameC")
                .name("NachnameC")
                .build();

        this.mockMvc.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + userJwt)
                        .content(JsonUtils.toString(UserEntityMapper.to(testC))))
                .andExpect(status().isForbidden());

        //
        // Admin login
        //
        String adminJwt = ControllerUtils.loginAndGetToken(mockMvc, "ADMIN", "secret-password");
        assertThat(loginService.validate(adminJwt)).isPresent().map(Nickname::value).contains("ADMIN");

        //
        // Create user with admin credentials.
        //
        this.mockMvc.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + adminJwt)
                        .content(JsonUtils.toString(UserEntityMapper.to(testC))))
                .andExpect(status().isOk());

        String json = this.mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + adminJwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
//                .andExpect(jsonPath("$[0].name", is("Winkler")))
//                .andExpect(jsonPath("$[1].name", is("NachnameA")))
//                .andExpect(jsonPath("$[2].name", is("NachnameB")))
//                .andExpect(jsonPath("$[3].name", is("NachnameC")));

        List<UserEntity> allUsers = JsonUtils.toList(json);
        assertThat(allUsers).extracting("nickname", "name", "firstname")
                .contains(
                        tuple("Frosch", "Winkler", "Andre"),
                        tuple("TestA", "NachnameA", "VornameA"),
                        tuple("TestB", "NachnameB", "VornameB"),
                        tuple("TestC", "NachnameC", "VornameC"),
                        tuple("ADMIN", "admin", "admin"));

        UserEntity persistedUserC = userRepository.findByNickname(Nickname.of("TestC")).orElseThrow();
        assertThat(persistedUserC.nickname().value()).isEqualTo("TestC");
        UserEntity persistedFrosch = userRepository.findByNickname(Nickname.of("Frosch")).orElseThrow();
        assertThat(persistedFrosch.nickname().value()).isEqualTo("Frosch");
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
        String froschJwt = ControllerUtils.loginAndGetToken(mockMvc, "Frosch", "PasswordFrosch");

        Optional<Nickname> validate = loginService.validate(froschJwt);
        assertThat(validate).isPresent().map(Nickname::value).contains("Frosch");
        UserEntity persistedFrosch = userRepository.findByNickname(Nickname.of("Frosch")).orElseThrow();

        //
        // Update user
        //

        // Some user can´t change the user data of another user.
        testC.setName("NachnameC_Neu");
        this.mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt)
                        .content(JsonUtils.toString(UserEntityMapper.to(testC))))
                .andExpect(status().isForbidden());

        // Only the logged user can change his own user data.
        persistedFrosch.setFirstname("Erwin");
        persistedFrosch.setName("WinklerNeu");

        this.mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt)
                        .content(JsonUtils.toString(UserEntityMapper.to(persistedFrosch))))
                .andExpect(status().isOk());
        
        // The logged user wants to update his role.
        this.mockMvc.perform(
                put("/user/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt)
                        .param("nickname", "Frosch")
                        .param("role", "ROLE_USER")
                        .content(JsonUtils.toString(UserEntityMapper.to(persistedFrosch))))
                .andExpect(status().isForbidden());
        
        // Some random user wants to update ... but gets a forbidden response.
        UserUpdateJson fantasyUser = new UserUpdateJson();
        fantasyUser.setNickname("Fantasy");
        this.mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt)
                        .content(JsonUtils.toString(fantasyUser)))
                .andExpect(status().isForbidden());

//        this.mockMvc.perform(get("/user"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name", is("WinklerNeu")))
//                .andExpect(jsonPath("$[1].name", is("NachnameA")))
//                .andExpect(jsonPath("$[2].name", is("NachnameB")))
//                .andExpect(jsonPath("$[3].name", is("NachnameC")));

        //
        // Update without Jason Web Token
        //
        testC.setName("NachnameC_Neu");
        this.mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toString(testC)))
                .andExpect(status().isForbidden());
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
