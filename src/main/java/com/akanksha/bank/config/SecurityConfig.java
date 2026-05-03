package com.akanksha.bank.config;

import com.akanksha.bank.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // .anyRequest()

                        // Public APIs (no token required)
                        .requestMatchers("/auth/**").permitAll() // login
                        .requestMatchers(HttpMethod.POST, "/users").permitAll() // register only

                        // Admin-only APIs
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        // WHY hasRole("ADMIN") and not ROLE_ADMIN?
                        // Because Spring automatically adds ROLE_... so we just specify ADMIN here.
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

                        // Protected any other APIs other than above ones
                        .anyRequest().authenticated())

                // Add JWT filter before Spring's authentication filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}