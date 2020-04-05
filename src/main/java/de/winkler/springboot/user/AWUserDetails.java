package de.winkler.springboot.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Eine Implementierung der Spring-Security Vorgabe {@link UserDetails}. Diese Klasse beschreibt Name und
 * andere Kontoinformationen wie Authorities.s
 */
public final class AWUserDetails implements UserDetails {

    private final String nickname;
    private final String password;
    private final List<GrantedAuthority> authorities;

    private AWUserDetails(String nickname, String password, List<GrantedAuthority> authorities) {
        this.nickname = nickname;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static class AWUserDetailsBuilder {
        private String nickname;
        private String password;
        private List<GrantedAuthority> authorities = new ArrayList<>();

        private AWUserDetailsBuilder() {
        }

        public static AWUserDetailsBuilder of(String nickname, String password) {
            AWUserDetailsBuilder builder = new AWUserDetailsBuilder();
            builder.nickname = nickname;
            builder.password = password;
            return builder;
        }

        public AWUserDetailsBuilder addGrantedAuthority(GrantedAuthority grantedAuthority) {
            authorities.add(grantedAuthority);
            return this;
        }

        public AWUserDetails build() {
            return new AWUserDetails(nickname, password, authorities);
        }
    }

}
