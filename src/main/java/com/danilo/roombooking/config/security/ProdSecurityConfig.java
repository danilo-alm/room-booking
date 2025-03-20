package com.danilo.roombooking.config.security;


import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.domain.role.RoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class ProdSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(this::configureRequestMatchers);
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
            RoleType.ROLE_ADMIN.name() + " > " + RoleType.ROLE_MANAGER.name() + "\n" +
            RoleType.ROLE_MANAGER.name() + " > " + RoleType.ROLE_USER.name()
        );
    }

    private void configureRequestMatchers(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>
            .AuthorizationManagerRequestMatcherRegistry requests)
    {
        requests
            .requestMatchers(ApiPaths.User.ROOT + "/**").hasAuthority(RoleType.ROLE_ADMIN.name())

            .requestMatchers(ApiPaths.Room.ROOT).hasAuthority(RoleType.ROLE_MANAGER.name())
            .requestMatchers(HttpMethod.GET, ApiPaths.Room.ROOT + "/**").permitAll()

            .requestMatchers(ApiPaths.Amenity.ROOT).hasAuthority(RoleType.ROLE_MANAGER.name())
            .requestMatchers(HttpMethod.GET, ApiPaths.Amenity.ROOT + "/**").permitAll()

            .requestMatchers(HttpMethod.GET, ApiPaths.Booking.ROOT).permitAll()
            .requestMatchers(ApiPaths.Booking.ROOT).hasAuthority(RoleType.ROLE_ADMIN.name());
    }

}