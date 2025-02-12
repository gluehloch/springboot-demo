package de.winkler.springboot.user.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.winkler.springboot.user.Nickname;

class UserRoleEntityTest {

    @Test
    void userRole() {
        UserEntity frosch = UserEntity.UserBuilder
                .of(Nickname.of("Frosch"), "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .age(40)
                .build();

        RoleEntity userRole = RoleEntity.RoleBuilder.of("USER");
        RoleEntity adminRole = RoleEntity.RoleBuilder.of("ADMIN");

        frosch.addRole(userRole);
        frosch.addRole(adminRole);
        assertThat(frosch.getRoles()).hasSize(2);
        frosch.addRole(userRole);
        frosch.addRole(adminRole);
        assertThat(frosch.getRoles()).hasSize(2);

        frosch.removeRole(userRole);
        assertThat(frosch.getRoles()).hasSize(1);
        frosch.removeRole(adminRole);
        assertThat(frosch.getRoles()).hasSize(0);

        frosch.removeRole(userRole);
        frosch.removeRole(adminRole);
        assertThat(frosch.getRoles()).hasSize(0);
    }

}
