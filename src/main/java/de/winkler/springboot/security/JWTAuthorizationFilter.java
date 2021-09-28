package de.winkler.springboot.security;

import de.winkler.springboot.user.PrivilegeEntity;
import de.winkler.springboot.user.RoleEntity;
import de.winkler.springboot.user.RoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.winkler.springboot.user.SecurityConstants.HEADER_STRING;
import static de.winkler.springboot.user.SecurityConstants.TOKEN_PREFIX;

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
            Optional<String> nickname = loginService.validate(token.replace(TOKEN_PREFIX, ""));
            if (nickname.isPresent()) {
                
                List<RoleEntity> roles = roleRepository.findRoles(nickname.get());
                //
                // TODO
                // * User und Authorities laden und dem Request zuordnen.
                // * Wie setzt sich die Authority zusammen: Aus Privilegien und/oder Rollen?!?
                //
                List<GrantedAuthority> authorities = roles.stream().map(MyGrantedAuthority::of).collect(Collectors.toList());
                return new UsernamePasswordAuthenticationToken(nickname.get(), null, authorities);
            }
            return null;
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
