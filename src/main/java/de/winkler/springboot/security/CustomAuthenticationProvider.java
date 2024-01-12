package de.winkler.springboot.security;

import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.UserEntity;
import de.winkler.springboot.user.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Custom Authentification Provider: Defines my own authentication implementation. A nickname/password comparison.
 *
 * @author Andre Winkler
 */
@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = Logger.getLogger(CustomAuthenticationProvider.class.getName());

    private final UserRepository userRepository;

    public CustomAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        Object credentials = authentication.getCredentials();

        LOG.info(() -> "credentials class: " + credentials.getClass());

        if (!(credentials instanceof String)) {
            return null;
        }

        String password = credentials.toString();

        UserEntity user = userRepository.findByNickname(Nickname.of(name))
                .orElseThrow(() -> new BadCredentialsException("Authentication failed for nickname=[" + name + "]."));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //
        // TODO Rollen und Benutzer.
        //
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));

        //
        // TODO ???? UsernamePasswordAuthenticationToken oder lieber was JWT naeheres???
        //
        Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuthorities);

        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
