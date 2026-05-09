package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class UserModelTypesTest {

    @Test
    void nicknameUsesValueForEqualityAndToString() {
        Nickname nickname = Nickname.of("Frosch");

        assertThat(nickname).isEqualTo(Nickname.of("Frosch"));
        assertThat(nickname).isNotEqualTo(Nickname.of("Lars"));
        assertThat(nickname.toString()).isEqualTo("Nickname=[Frosch]");
    }

    @Test
    void roleImplUsesNameForEquality() {
        RoleImpl role = new RoleImpl("ROLE_USER");

        assertThat(role).isEqualTo(new RoleImpl("ROLE_USER"));
        assertThat(role).isNotEqualTo(new RoleImpl("ROLE_ADMIN"));
        assertThat(role.name()).isEqualTo("ROLE_USER");
    }

    @Test
    void userProfileCopiesRolesDefensively() {
        Set<RoleImpl> sourceRoles = new HashSet<>();
        sourceRoles.add(new RoleImpl("ROLE_USER"));

        UserProfileImpl profile = new UserProfileImpl("Frosch", "Winkler", "Andre", 42, sourceRoles);
        sourceRoles.add(new RoleImpl("ROLE_ADMIN"));

        assertThat(profile.roles()).extracting(Role::name).containsExactly("ROLE_USER");
        assertThatThrownBy(() -> profile.roles().add(new RoleImpl("ROLE_AUDITOR")))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(profile).isEqualTo(new UserProfileImpl("Frosch", "Other", "Name", 1, Set.of()));
    }

    @Test
    void userCredentialsExposesConfiguredRolesImmutably() {
        Set<RoleImpl> sourceRoles = new HashSet<>();
        sourceRoles.add(new RoleImpl("ROLE_USER"));

        UserCredentialsImpl credentials = new UserCredentialsImpl("Frosch", "secret", "Winkler", "Andre", sourceRoles);
        sourceRoles.add(new RoleImpl("ROLE_ADMIN"));

        assertThat(credentials.roles()).extracting(Role::name).containsExactly("ROLE_USER");
        assertThatThrownBy(() -> credentials.roles().add(new RoleImpl("ROLE_AUDITOR")))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(credentials).isEqualTo(new UserCredentialsImpl("Frosch", "other", "Else", "Body", Set.of()));
    }

    @Test
    void tokenUsesReadableStringRepresentation() {
        Token token = new Token("abc123");

        assertThat(token.getContent()).isEqualTo("abc123");
        assertThat(token.toString()).isEqualTo("Token=[content='abc123']");
    }
}
