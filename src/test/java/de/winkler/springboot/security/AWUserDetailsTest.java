package de.winkler.springboot.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import de.winkler.springboot.user.Nickname;

class AWUserDetailsTest {

    @Test
    void builderCreatesImmutableUserDetails() {
        AWUserDetails userDetails = AWUserDetails.AWUserDetailsBuilder
                .of(Nickname.of("Frosch"), "secret")
                .addGrantedAuthority(new SimpleGrantedAuthority("ROLE_USER"))
                .addGrantedAuthority(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .build();

        assertThat(userDetails.getUsername()).isEqualTo("Frosch");
        assertThat(userDetails.getPassword()).isEqualTo("secret");
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER", "ROLE_ADMIN");
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();

        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) userDetails.getAuthorities();
        assertThatThrownBy(() -> authorities.add(new SimpleGrantedAuthority("ROLE_AUDITOR")))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
