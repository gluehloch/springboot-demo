package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;

import de.winkler.springboot.user.internal.*;

@SpringBootTest
//@DataJpaTest
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
        user.setNickname(Nickname.of("Frosch"));
        user.setFirstname("Andre");
        user.setName("Winkler");
        user.setPassword("Password");
        user = userRepository.save(user);
        assertThat(user.getId()).isNotNull();

        UserEntity persistedUser = userRepository.findByNickname(Nickname.of("Frosch")).orElseThrow();
        assertThat(persistedUser).isNotNull();
        assertThat(persistedUser.firstname()).isEqualTo("Andre");
        assertThat(persistedUser.name()).isEqualTo("Winkler");
        assertThat(persistedUser.nickname().value()).isEqualTo("Frosch");

        assertThat(persistedUser).isEqualTo(user);
    }

    @DisplayName("Repository test: Find all roles of a user")
    @Test
    @Tag("repository")
    @Transactional
    void findRoles() {
        UserEntity frosch = UserEntity.UserBuilder
                .of(Nickname.of("Frosch"), "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .build();
        userRepository.save(frosch);

        RoleEntity role = new RoleEntity();
        role.setName("USER");
        roleRepository.save(role);

        frosch.addRole(role);

        List<RoleEntity> roles = roleRepository.findRoles(frosch.nickname());
        assertThat(roles).hasSize(1);
        assertThat(roles.get(0).getName()).isEqualTo("USER");
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
        assertThat(all).isNotNull();
    }

    @DisplayName("Repository test: Find some paged users.")
    @Test
    @Tag("repository")
    @Transactional
    void findPagedUsers() {
        Pageable pageable = Pageable.unpaged();
        Page<UserEntity> page = userRepository.findAll(pageable);
        assertThat(page.getContent()).hasSize(0);
              
        UserEntity frosch = UserEntity.UserBuilder
                .of(Nickname.of("Frosch"), "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .build();
        userRepository.save(frosch);

        Page<UserEntity> page2 = userRepository.findAll(PageRequest.of(0, 5));
        assertThat(page2.getContent()).hasSize(1);
        
        assertThat(userRepository.findAll(PageRequest.of(2, 20))).hasSize(0);
    }

}
