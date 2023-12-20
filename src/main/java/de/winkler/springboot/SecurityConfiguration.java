package de.winkler.springboot;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
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
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

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
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager,
            MvcRequestMatcher.Builder mvc) throws Exception
    {
        http
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .deleteCookies("JSESSIONID"))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(mvc.pattern(HttpMethod.GET, "/")).permitAll()
                .requestMatchers(mvc.pattern(HttpMethod.GET, "/*.js")).permitAll()
                .requestMatchers(mvc.pattern(HttpMethod.GET, "/home")).permitAll()
                .requestMatchers(mvc.pattern(HttpMethod.GET, "/index.html")).permitAll()

                .requestMatchers(mvc.pattern(HttpMethod.GET, "/actuator/**")).permitAll()
                .requestMatchers(mvc.pattern(HttpMethod.GET, "/actuator/logfile/**")).permitAll()
                .requestMatchers(mvc.pattern(HttpMethod.GET, "/actuator/health/**")).permitAll()

                .requestMatchers(mvc.pattern(HttpMethod.GET, "/demo/ping")).permitAll()
                .requestMatchers(mvc.pattern(HttpMethod.GET, "/order")).hasAnyRole("USER")
                .requestMatchers(mvc.pattern(HttpMethod.POST, "/order")).hasAnyRole("USER")
                .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/order")).hasAnyRole("USER")
                .requestMatchers(mvc.pattern(HttpMethod.PUT, "/order")).hasAnyRole("USER")

                .requestMatchers(mvc.pattern(HttpMethod.GET, "/user")).hasRole("ADMIN")
                .requestMatchers(mvc.pattern(HttpMethod.GET, "/user/**")).hasAnyRole("USER", "ADMIN")
                .requestMatchers(mvc.pattern(HttpMethod.PUT, "/user")).hasAnyRole("USER", "ADMIN")
                .requestMatchers(mvc.pattern(HttpMethod.POST, "/user")).hasAnyRole("ADMIN")
                .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/user")).hasAnyRole("USER", "ADMIN")

                .requestMatchers(mvc.pattern(HttpMethod.GET, "/user/role")).hasRole("ADMIN")
                .requestMatchers(mvc.pattern(HttpMethod.PUT, "/user/role")).hasRole("ADMIN")
                .requestMatchers(mvc.pattern(HttpMethod.POST, "/user/role")).hasRole("ADMIN")
                .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/user/role")).hasRole("ADMIN")

                // .requestMatchers(PathRequest.toH2Console()).authenticated()
                /*
                .requestMatchers(antMatcher(HttpMethod.GET, "/h2-console")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PUT, "/h2-console")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.POST, "/h2-console")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/h2-console")).hasRole("ADMIN")
                 */
                .requestMatchers(antMatcher("/h2-console/**")).authenticated()
                .requestMatchers(antMatcher("/v3/**")).permitAll()
                .requestMatchers(antMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(mvc.pattern(HttpMethod.POST, "/login")).permitAll()
                .requestMatchers(mvc.pattern(HttpMethod.POST, "/logout")).hasRole("USER") // TODO
        );

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