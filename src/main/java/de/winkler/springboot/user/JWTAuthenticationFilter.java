package de.winkler.springboot.user;

import static de.winkler.springboot.user.SecurityConstants.HEADER_STRING;
import static de.winkler.springboot.user.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nickname, password, new ArrayList<>()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {

        UserDetails user = loginService.loadUserByUsername((String) auth.getPrincipal());
        Token token = loginService.token(user);

        // TODO create JWT and add to http-response
        //        String token = JWT.create()
        //                .withSubject(((User) auth.getPrincipal()).getUsername())
        //                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        //                .sign(HMAC512(SECRET.getBytes()));
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token.getContent());
    }

}
