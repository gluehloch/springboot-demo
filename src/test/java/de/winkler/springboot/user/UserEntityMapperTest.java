package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.UserEntity;

class UserEntityMapperTest {

    @Test
    void toOptionalReturnsEmptyWhenUserIsMissing() {
        assertThat(UserEntityMapper.to(Optional.empty())).isEmpty();
    }

    @Test
    void toUserProfileMapsCoreUserFieldsAndRoles() {
        UserEntity user = createUser();

        UserProfile profile = UserEntityMapper.to(user);

        assertThat(profile.nickname()).isEqualTo(Nickname.of("Frosch"));
        assertThat(profile.name()).isEqualTo("Winkler");
        assertThat(profile.firstname()).isEqualTo("Andre");
        assertThat(profile.age()).isEqualTo(42);
        assertThat(profile.roles()).extracting(Role::name).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void toUserCredentialsMapsPasswordAndRoles() {
        UserEntity user = createUser();

        UserCredentials credentials = UserEntityMapper.toUserCredentials(user);

        assertThat(credentials.nickname()).isEqualTo(Nickname.of("Frosch"));
        assertThat(credentials.password()).isEqualTo("PasswordFrosch");
        assertThat(credentials.name()).isEqualTo("Winkler");
        assertThat(credentials.firstname()).isEqualTo("Andre");
        assertThat(credentials.roles()).extracting(Role::name).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    private static UserEntity createUser() {
        UserEntity user = UserEntity.UserBuilder
                .of(Nickname.of("Frosch"), "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .age(42)
                .build();
        user.addRole(RoleEntity.RoleBuilder.of("ROLE_USER"));
        user.addRole(RoleEntity.RoleBuilder.of("ROLE_ADMIN"));
        return user;
    }
}
