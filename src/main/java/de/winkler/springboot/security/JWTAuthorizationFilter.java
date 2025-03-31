package de.winkler.springboot.security;

import static de.winkler.springboot.user.SecurityConstants.HEADER_STRING;
import static de.winkler.springboot.user.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.RoleRepository;
import de.winkler.springboot.user.internal.PrivilegeEntity;
import de.winkler.springboot.user.internal.RoleEntity;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final LoginService loginService;
    private final RoleRepository roleRepository;

    public JWTAuthorizationFilter(AuthenticationManager authManager, LoginService loginService, RoleRepository roleRepository) {
        super(authManager);
        this.loginService = loginService;
        this.roleRepository = roleRepository;
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
            Optional<Nickname> nickname = loginService.validate(token.replace(TOKEN_PREFIX, ""));
            return nickname.map(nn -> {
                List<RoleEntity> roles = roleRepository.findRoles(nn);
                //
                // TODO
                // * User und Authorities laden und dem Request zuordnen.
                // * Wie setzt sich die Authority zusammen: Aus Privilegien und/oder Rollen?!?
                //
                List<GrantedAuthority> authorities = roles.stream().map(MyGrantedAuthority::of).collect(Collectors.toList());
                return new UsernamePasswordAuthenticationToken(nn.value(), null, authorities);
            }).orElse(null);
        }
        return null;
    }

    /**
    /* TODO Zuordnung zu {@link RoleEntity} und {@link PrivilegeEntity}.
     * 
     * Könnte hier ebenfalls ein {@link SimpleGrantedAuthority} sein. Die Rolle ist nur eine String Repräsentation.
     */
    public static class MyGrantedAuthority implements GrantedAuthority {
		private static final long serialVersionUID = 1L;

		private String roleName;
        
        public static MyGrantedAuthority of(RoleEntity role) {
            return new MyGrantedAuthority(role.getName());
        }
        
        private MyGrantedAuthority(String roleName) {
            this.roleName = roleName;
        }
        
        @Override
        public String getAuthority() {
            return roleName;
        }
    }

}
