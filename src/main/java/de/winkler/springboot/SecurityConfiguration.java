package de.winkler.springboot;

import de.winkler.springboot.security.CustomAuthenticationProvider;
import de.winkler.springboot.security.JWTAuthenticationFilter;
import de.winkler.springboot.security.JWTAuthorizationFilter;
import de.winkler.springboot.security.LoginService;
import de.winkler.springboot.user.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    LoginService loginService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.authenticationProvider(customAuthenticationProvider);
    }

    // Secure the endpoins with HTTP Basic authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler()).deleteCookies("JSESSIONID")
                .and()
                //.formLogin().loginProcessingUrl("/login").and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), loginService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), loginService, roleRepository))
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/demo/ping").permitAll()

                .antMatchers(HttpMethod.GET, "/order").hasAnyRole("USER")
                .antMatchers(HttpMethod.POST, "/order").hasAnyRole("USER")
                .antMatchers(HttpMethod.DELETE, "/order").hasAnyRole("USER")
                .antMatchers(HttpMethod.PUT, "/order").hasAnyRole("USER")

                .antMatchers(HttpMethod.GET, "/user").permitAll()
                .antMatchers(HttpMethod.PUT, "/user").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/user").hasAnyRole("USER", "ADMIN")

                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/logout").hasRole("USER"); // TODO
        //.antMatchers(HttpMethod.GET, "/books/**").hasRole("USER")
        //.antMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
        //.antMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
        //.antMatchers(HttpMethod.PATCH, "/books/**").hasRole("ADMIN")
        //.antMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

        //                .and()
        //                .csrf().disable()
        //                .formLogin().disable();
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