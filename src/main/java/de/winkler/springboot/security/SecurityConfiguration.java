package de.winkler.springboot.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.winkler.springboot.user.internal.RoleRepository;

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
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager ) throws Exception
    {
        http
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .deleteCookies("JSESSIONID"))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers(HttpMethod.GET, "/*.js").permitAll()
                .requestMatchers(HttpMethod.GET, "/home").permitAll()
                .requestMatchers(HttpMethod.GET, "/index.html").permitAll()

                .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/logfile/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/health/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/demo/ping").permitAll()
                .requestMatchers(HttpMethod.GET, "/order").hasAnyRole("USER")
                .requestMatchers(HttpMethod.POST, "/order").hasAnyRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/order").hasAnyRole("USER")
                .requestMatchers(HttpMethod.PUT, "/order").hasAnyRole("USER")

                .requestMatchers(HttpMethod.GET, "/user").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/user").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/user").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/user").hasAnyRole("USER", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/user/role").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/user/role").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/user/role").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/user/role").hasRole("ADMIN")

                // .requestMatchers(PathRequest.toH2Console()).authenticated()
                /*
                .requestMatchers(antMatcher(HttpMethod.GET, "/h2-console")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PUT, "/h2-console")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.POST, "/h2-console")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/h2-console")).hasRole("ADMIN")
                 */
                .requestMatchers("/h2-console/**").authenticated()
                .requestMatchers("/v3/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/logout").hasRole("USER") // TODO
        );

	    http.securityMatcher(EndpointRequest.toAnyEndpoint());
		http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());

        //http.authorizeHttpRequests(auth -> auth.requestMatchers(antMatcher("/h2-console/**")).authenticated());

        http.addFilterBefore(new JWTAuthenticationFilter(authenticationManager, loginService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new JWTAuthorizationFilter(authenticationManager, loginService, roleRepository), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return loginService;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    LogoutSuccessHandler logoutSuccessHandler() {
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