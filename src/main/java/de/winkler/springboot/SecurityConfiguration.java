package de.winkler.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.winkler.springboot.user.CustomAuthenticationProvider;
import de.winkler.springboot.user.JWTAuthenticationFilter;
import de.winkler.springboot.user.JWTAuthorizationFilter;
import de.winkler.springboot.user.LoginService;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    LoginService loginService;

    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    // Create 2 users for demo
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("user").password("{noop}password").roles("USER")
//                .and()
//                .withUser("admin").password("{noop}password").roles("USER", "ADMIN");
//    }

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
//                .formLogin().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), loginService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), loginService))
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/demo/ping").permitAll()
                .antMatchers(HttpMethod.GET, "/user").permitAll()
                .antMatchers(HttpMethod.PUT, "/user").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/user").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/logout").hasAnyRole("USER", "ADMIN");
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
//        User.UserBuilder users = User.withDefaultPasswordEncoder();
//
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(users.username("user").password("passwd").roles("USER").build());
//        manager.createUser(users.username("admin").password("passwd").roles("USER", "ADMIN").build());
//        return manager;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
//        corsConfiguration.addAllowedOrigin("*");
//        corsConfiguration.addAllowedMethod("GET");
//        corsConfiguration.addAllowedMethod("PUT");
//        corsConfiguration.addAllowedMethod("POST");
//        corsConfiguration.addAllowedMethod("PATCH");
//        corsConfiguration.addAllowedMethod("DELETE");
//        corsConfiguration.addAllowedMethod("OPTIONS");

        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}