package com.avocadogroup.recipy.common.configs;

import com.avocadogroup.recipy.authentication.AuthenticationFilter;
import com.avocadogroup.recipy.authentication.services.UserDetailsServiceImpl;
import com.avocadogroup.recipy.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;

    // Bean to configure the app security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {
        // Disable CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // Set session management to stateless (token-based authentication instead of session-based authentication)
        http.sessionManagement(sessionManagementConfigurer ->
                sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Define endpoint access rules
        http.authorizeHttpRequests(request ->
                request
                        // Public endpoints (no authentication required)
                        .requestMatchers(HttpMethod.GET, "/api/auth/ping").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
                        // Role Based Endpoints (Requires specific role)
                        .requestMatchers("/api/admin/**").hasRole(UserRole.ADMIN.toString()) // Only users with ADMIN role can access /admin/**
                        // All other endpoints (authentication token required) [need to pass the auth filter]
                        .anyRequest().authenticated()
        );

        // Add custom filter before each request
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // Valid access token check

        // Add custom security exceptions
        http.exceptionHandling(exceptionHandler -> {
                    // If the user is not logged in (no valid token) map the default AuthenticationException to http 401 response
                    exceptionHandler.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)); // Tell Spring Security to return 401

                    // If the user is logged in but not authorized (need specific rule (authorities in the authToken obj) send http 403 response
                    exceptionHandler.accessDeniedHandler(
                            (request, response, accessDeniedException) -> {
                                response.setStatus(HttpStatus.FORBIDDEN.value()); // Tell Spring Security to return 403
                            });
                }
        );

        // Build and return the configured SecurityFilterChain (Configuration object to be used by Spring Security at runtime)
        return http.build();
    }

    // Bean to provide a PasswordEncoder at runtime to be used for hashing passwords in the application
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean to use the DaoAuthenticationProvider as the project provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Create a new instance of DaoAuthenticationProvider
        DaoAuthenticationProvider DaoProvider = new DaoAuthenticationProvider(userDetailsService);

        // Set the Dao AuthenticationProvider's tools: UserDetailsService (added above) and PasswordEncoder
        DaoProvider.setPasswordEncoder(passwordEncoder());

        // Return the configured DaoAuthenticationProvider
        return DaoProvider;
    }

    // Bean to provide an AuthenticationManager at runtime (to be used for authentication in the application)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Return the AuthenticationManager from the AuthenticationConfiguration (spring security)
        return authConfig.getAuthenticationManager();
    }
}
