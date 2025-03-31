package de.winkler.springboot.security;

import static de.winkler.springboot.user.SecurityConstants.HEADER_STRING;
import static de.winkler.springboot.user.SecurityConstants.TOKEN_PREFIX;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import de.winkler.springboot.user.Token;
import de.winkler.springboot.user.internal.UserEntity;

/**
 * This filter tries to authenticate the user. So there has to be an instance the {@link UserEntity} as JSON
 * string in the HTTP request.
 *
 * @author Andre Winkler
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final LoginService loginService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, LoginService loginService) {
        this.authenticationManager = authenticationManager;
        this.loginService = loginService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {

        String nickname = req.getParameter("nickname");
        String password = req.getParameter("password");

        // UserEntity creds = new ObjectMapper().readValue(req.getInputStream(), UserEntity.class);

        // TODO Create and add GrantedAuthorities. `List.of` creates an unmodifiable list.
        List<? extends GrantedAuthority> authorities = List.of();

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nickname, password, authorities));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
            Authentication auth) {

        UserDetails user = loginService.loadUserByUsername((String) auth.getPrincipal());
        Token token = loginService.token(user);

        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token.getContent());
    }

}
