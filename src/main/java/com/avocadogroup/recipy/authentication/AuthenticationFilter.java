package com.avocadogroup.recipy.authentication;

import com.avocadogroup.recipy.authentication.services.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// NOTE: A filter that is executed once per request. It will run before any controller is called.
// This filter will check if the request contain a valid JWT token in the Authorization header.
// If the token is valid, it will check if the user has access to the requested resource.

// NOTE: SecurityContextHolder store the authentication information of the current user in a thread-local storage.
// This means that the authentication information is stored in a variable that is specific to the current thread
// and is not shared with other threads. This is important because each request is handled by a different thread.
// By using thread-local storage, we can ensure that the authentication information is only accessible
// to the thread that is handling the current request. This prevents any potential security issues that could arise

@Component
@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
// Extract the JWT token from the authorization header "Bearer A2C4"
        var authorizationHeader = request.getHeader("Authorization");

        // If the authorization header is missing or does not start with "Bearer " then skip the filter
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // If so, continue the request without setting authentication (token is ignored) (continue with the next filter in the chain)
            filterChain.doFilter(request, response);

            // Stop processing in this filter and the spring security will handle the request
            return;
        }

        // Extract the token from the header
        var token = authorizationHeader.replace("Bearer ", ""); // Remove "Bearer " from "Bearer A2C4"

        // If the token not found in the db
        if (jwtService.isTokenExpired(token)) {
            // If so, continue the request without setting authentication (token is ignored) (continue with the next filter in the chain)
            filterChain.doFilter(request, response);

            // Stop processing in this filter and the spring security will handle the request
            return;
        }

        // Fetch the token from the database
        var dbToken = userSessionsRepository.findByToken(token);

        // Check if the token is not available in the DB
        if (dbToken.isEmpty()) {
            // If so, continue the request without setting authentication (token is ignored) (continue with the next filter in the chain)
            filterChain.doFilter(request, response); // Pass the control to the next filter method

            // Stop processing in this filter and the spring security will handle the request
            return;
        }

        // Extract entity from optional
        var tokenEntity = dbToken.get();

        // Extract claims safely if the token is malformed, skip authentication
        Long userId;
        String userRole;

        try {
            // Get the user id from the token
            userId = jwtService.getUserIdFromToken(token);

            // Get the user role from the token claims
            userRole = jwtService.getUserRoleFromToken(token);
        } catch (JwtException | NumberFormatException e) {
            // Token is structurally invalid — continue unauthenticated
            filterChain.doFilter(request, response);
            return;
        }

        // Check if the token is not valid
        if (!tokenEntity.isValid(userId)) {
            // If so, continue the request without setting authentication (token is ignored) (continue with the next filter in the chain)
            filterChain.doFilter(request, response);

            // Stop processing in this filter and the spring security will handle the request
            return;
        }

        // Give an authorization to the user who made the request
        // Build an authentication token object with the user id (the token object is used by Spring Security to represent the authenticated user)
        var authenticationTokenObject = new UsernamePasswordAuthenticationToken(
                userId, // user id as principal
                null, // No credentials because we are using JWT token for authentication token (not username and password)
                List.of(new SimpleGrantedAuthority("ROLE_" + userRole)) // Set the user role in the authentication token (Spring Security expects roles to be prefixed with "ROLE_")
        );

        // Set the request details in the authentication token object (IP address, session ID, etc.) `boilerplate code!`
        authenticationTokenObject.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request) // Set the request details in the authentication token to be used by Spring Security
        );

        // Set the authentication object in the Security Context Holder (store the authentication information for the current request) (This will mark the user as authenticated in the current request context) `boilerplate code!`
        SecurityContextHolder.getContext().setAuthentication(authenticationTokenObject);

        // Continue with the next filter in the chain
        filterChain.doFilter(request, response); // Pass the control to the next filter method
    }
}
