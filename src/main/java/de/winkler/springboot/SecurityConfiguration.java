package de.winkler.springboot;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.winkler.springboot.security.CustomAuthenticationProvider;
import de.winkler.springboot.security.JWTAuthenticationFilter;
import de.winkler.springboot.security.JWTAuthorizationFilter;
import de.winkler.springboot.security.LoginService;
import de.winkler.springboot.user.RoleRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    LoginService loginService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .deleteCookies("JSESSIONID"))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/home", "/index.html").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/actuator/logfile/**").permitAll()
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers(antMatcher("/demo/ping")).permitAll()
                .requestMatchers(antMatcher(HttpMethod.GET, "/order")).hasAnyRole("USER")
                .requestMatchers(antMatcher(HttpMethod.POST, "/order")).hasAnyRole("USER")
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/order")).hasAnyRole("USER")
                .requestMatchers(antMatcher(HttpMethod.PUT, "/order")).hasAnyRole("USER")

                .requestMatchers(antMatcher(HttpMethod.GET, "/user")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.GET, "/user/**")).hasAnyRole("USER", "ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PUT, "/user")).hasAnyRole("USER", "ADMIN")
                .requestMatchers(antMatcher(HttpMethod.POST, "/user")).hasAnyRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/user")).hasAnyRole("USER", "ADMIN")

                .requestMatchers(antMatcher(HttpMethod.GET, "/user/role")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PUT, "/user/role")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.POST, "/user/role")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/user/role")).hasRole("ADMIN")

                .requestMatchers(PathRequest.toH2Console()).authenticated()

                .requestMatchers(antMatcher("/login")).permitAll()
                .requestMatchers(antMatcher("/logout")).hasRole("USER") // TODO
        );

        http.addFilterBefore(new JWTAuthenticationFilter(authenticationManager, loginService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new JWTAuthorizationFilter(authenticationManager, loginService, roleRepository), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return loginService;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        var x = new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {

                loginService.logout(null);
            }
        };
        return x;
    }

}