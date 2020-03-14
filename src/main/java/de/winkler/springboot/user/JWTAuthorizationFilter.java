package de.winkler.springboot.user;

import static de.winkler.springboot.user.SecurityConstants.HEADER_STRING;
import static de.winkler.springboot.user.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final LoginService loginService;

    public JWTAuthorizationFilter(AuthenticationManager authManager, LoginService loginService) {
        super(authManager);
        this.loginService = loginService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws
            IOException, ServletException {

        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            Optional<String> validate = loginService.validate(token.replace(TOKEN_PREFIX, ""));

            if (validate.isPresent()) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new MyGrantedAuthority());

                return new UsernamePasswordAuthenticationToken(validate.get(), null, authorities);
            }
            return null;
        }
        return null;
    }

    // TODO Refactor me!

    public static class MyGrantedAuthority implements GrantedAuthority {

        @Override
        public String getAuthority() {
            return "ROLE_USER";
        }
    }

}
