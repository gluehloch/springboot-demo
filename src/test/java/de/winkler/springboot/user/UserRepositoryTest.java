package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PrivilegeRepository privilegeRepository;

    @DisplayName("Repository test: Find all users")
    @Test
    @Tag("repository")
    @Transactional
    void findUser() {
        UserEntity user = new UserEntity();
        user.setNickname("Frosch");
        user.setFirstname("Andre");
        user.setName("Winkler");
        user.setPassword("Password");
        user = userRepository.save(user);
        assertThat(user.getId()).isNotNull();

        UserEntity persistedUser = userRepository.findByNameOrderByNameAsc("Winkler");
        assertThat(persistedUser).isNotNull();
        assertThat(persistedUser.getFirstname()).isEqualTo("Andre");
        assertThat(persistedUser.getName()).isEqualTo("Winkler");
        assertThat(persistedUser.getNickname()).isEqualTo("Frosch");

        assertThat(persistedUser).isEqualTo(user);
    }

    @DisplayName("Repository test: Find all roles of a user")
    @Test
    @Tag("repository")
    @Transactional
    void findRoles() {
        UserEntity frosch = UserEntity.UserBuilder
                .of("Frosch", "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .build();
        userRepository.save(frosch);

        RoleEntity role = new RoleEntity();
        role.setRolename("USER");
        roleRepository.save(role);

        frosch.addRole(role);

        List<RoleEntity> roles = roleRepository.findRoles("Frosch");
        assertThat(roles).hasSize(1);
        assertThat(roles.get(0).getRolename()).isEqualTo("USER");
    }

    @DisplayName("Repository test: Find all privilegs of a user")
    @Test
    @Tag("repository")
    @Transactional
    void findPrivileges() {
        PrivilegeEntity readPriv = PrivilegeEntity.PrivilegeBuilder.of("READ_PRIV");
        PrivilegeEntity persistedReadPriv = privilegeRepository.save(readPriv);
        assertThat(persistedReadPriv.getName()).isEqualTo("READ_PRIV");

        Iterable<PrivilegeEntity> all = privilegeRepository.findAll();
    }

}
