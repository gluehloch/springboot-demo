package de.winkler.springboot.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AWUserDetails implements UserDetails {

    private final String nickname;
    private final String password;

    private final List<GrantedAuthority> authorities = new ArrayList<>();

    public AWUserDetails(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;

        // TODO
        authorities.add(new AWGrantedAuthority("USER"));
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

    public static class AWGrantedAuthority implements GrantedAuthority {

        private final String role;

        public AWGrantedAuthority(String role) {
            this.role = role;
        }

        @Override
        public String getAuthority() {
            return role;
        }
    }

}
