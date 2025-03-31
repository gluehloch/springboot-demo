package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.UserEntity;

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
        assertThat(frosch.roles()).hasSize(2);
        frosch.addRole(userRole);
        frosch.addRole(adminRole);
        assertThat(frosch.roles()).hasSize(2);

        frosch.removeRole(userRole);
        assertThat(frosch.roles()).hasSize(1);
        frosch.removeRole(adminRole);
        assertThat(frosch.roles()).hasSize(0);

        frosch.removeRole(userRole);
        frosch.removeRole(adminRole);
        assertThat(frosch.roles()).hasSize(0);
    }

}
