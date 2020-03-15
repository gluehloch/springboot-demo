package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class UserRoleEntityTest {

    @Test
    public void userRole() {
        UserEntity frosch = UserEntity.UserBuilder
                .of("Frosch", "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .build();

        RoleEntity userRole = RoleEntity.RoleBuilder.of("USER");
        RoleEntity adminRole = RoleEntity.RoleBuilder.of("ADMIN");

        assertThat(frosch.addRole(userRole)).isTrue();
        assertThat(frosch.addRole(adminRole)).isTrue();
        assertThat(frosch.getRoles()).hasSize(2);
        assertThat(frosch.addRole(userRole)).isFalse();
        assertThat(frosch.addRole(adminRole)).isFalse();

        frosch.removeRole(userRole);
        assertThat(frosch.getRoles()).hasSize(1);
        frosch.removeRole(adminRole);
        assertThat(frosch.getRoles()).hasSize(0);

        frosch.removeRole(userRole);
        frosch.removeRole(adminRole);
        assertThat(frosch.getRoles()).hasSize(0);
    }
}
