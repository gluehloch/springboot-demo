package de.winkler.springboot.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import de.winkler.springboot.ObjectToJsonString;
import de.winkler.springboot.user.PrivilegeEntity;
import de.winkler.springboot.user.PrivilegeRepository;
import de.winkler.springboot.user.RoleEntity;
import de.winkler.springboot.user.RoleRepository;
import de.winkler.springboot.user.SecurityConstants;
import de.winkler.springboot.user.UserEntity;
import de.winkler.springboot.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class LoginLogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private LoginService loginService;

    @Test
    @Tag("controller")
    @Transactional
    void loginLogout() throws Exception {
        prepareDatabase();

        //
        // Login
        //

        ResultActions loginAction = this.mockMvc.perform(
                post("/login")
                        .param("nickname", "Frosch")
                        .param("password", "Password"))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult result = loginAction.andReturn();

        // Get the JWT from the response header ...
        String authorizationHeader = result.getResponse().getHeader(SecurityConstants.HEADER_STRING);
        String jwt = authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
        // ... and validate the token.
        Optional<String> validate = loginService.validate(jwt);
        assertThat(validate).isPresent().get().isEqualTo("Frosch");

        //
        // Get all users
        //

        this.mockMvc.perform(get("/user")).andDo(print()).andExpect(status().isOk());

        //
        // Logout
        //

        this.mockMvc.perform(
                post("/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt)
                        .content(ObjectToJsonString.toString(validate.get())))
                .andExpect(status().isOk());
    }

    private void prepareDatabase() {
        UserEntity frosch = UserEntity.UserBuilder
                .of("Frosch", "Password")
                .firstname("Andre")
                .name("Winkler")
                .build();
        frosch = userRepository.save(frosch);

        PrivilegeEntity privilegeReadUsers = PrivilegeEntity.PrivilegeBuilder.of("readUsers");
        privilegeRepository.save(privilegeReadUsers);
        PrivilegeEntity privilegeReadTeams = PrivilegeEntity.PrivilegeBuilder.of("readTeams");
        privilegeRepository.save(privilegeReadTeams);
        PrivilegeEntity privilegeReadGroups = PrivilegeEntity.PrivilegeBuilder.of("readGroups");
        privilegeRepository.save(privilegeReadGroups);

        RoleEntity readOnlyRole = RoleEntity.RoleBuilder.of("readOnly");
        readOnlyRole.addPrivilege(privilegeReadUsers);
        readOnlyRole.addPrivilege(privilegeReadTeams);
        readOnlyRole.addPrivilege(privilegeReadGroups);

        frosch.addRole(readOnlyRole);

        roleRepository.save(readOnlyRole);
    }

}
