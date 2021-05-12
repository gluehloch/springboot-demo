package de.winkler.springboot.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import de.winkler.springboot.ControllerUtils;
import de.winkler.springboot.security.LoginService;
import de.winkler.springboot.user.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    public void order() throws Exception {
        prepareDatabase();

        //
        // Order without login
        //

        this.mockMvc.perform(put("/order")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //
        // Login
        //

        final String froschJwt = ControllerUtils.loginAndGetToken(mockMvc, "Frosch", "PasswordFrosch");

        Optional<String> validate = loginService.validate(froschJwt);
        assertThat(validate).isPresent().get().isEqualTo("Frosch");

        //
        // Order: Security definition expects a logged user with role 'USER'.
        //

        this.mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON).queryParam("orderNr", "101")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + froschJwt)/*.contentType("Order TODO"))*/)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("orderNr", is(4711)));

    }

    private void prepareDatabase() {
        UserEntity frosch = UserEntity.UserBuilder
                .of("Frosch", "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .build();

        UserEntity testA = UserEntity.UserBuilder.of("TestA", "PasswordTestA")
                .firstname("VornameA")
                .name("NachnameA")
                .build();

        UserEntity testB = UserEntity.UserBuilder.of("TestB", "PasswordTestB")
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
