package de.winkler.springboot;

import de.winkler.springboot.security.CustomAuthenticationProvider;
import de.winkler.springboot.security.LoginService;
import de.winkler.springboot.user.RoleRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    LoginService loginService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .deleteCookies("JSESSIONID"))
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/home").permitAll()
                .requestMatchers(new AntPathRequestMatcher("/demo/ping")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/order", HttpMethod.GET.name())).hasAnyRole("USER")
                .requestMatchers(new AntPathRequestMatcher("/order", HttpMethod.POST.name())).hasAnyRole("USER")
                .requestMatchers(new AntPathRequestMatcher("/order", HttpMethod.DELETE.name())).hasAnyRole("USER")
                .requestMatchers(new AntPathRequestMatcher("/order", HttpMethod.PUT.name())).hasAnyRole("USER")

                .requestMatchers(new AntPathRequestMatcher("/user", HttpMethod.GET.name())).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/user", HttpMethod.PUT.name())).hasAnyRole("USER", "ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/user", HttpMethod.POST.name())).hasAnyRole("USER", "ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/user", HttpMethod.DELETE.name())).hasAnyRole("USER", "ADMIN")

                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/logout")).hasRole("USER") // TODO
        );
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