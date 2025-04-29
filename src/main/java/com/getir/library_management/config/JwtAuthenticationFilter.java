package com.getir.library_management.config;

import com.getir.library_management.entity.User;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Retrieve the Authorization header from the incoming request
        final String authHeader = request.getHeader("Authorization");

        // If header is missing or doesn't start with 'Bearer ', skip this filter
        if (!hasBearerToken(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token from the header
        final String token = extractToken(authHeader);

        // Extract the user's email (username) from the token
        final String email = jwtService.extractUsername(token);

        // If email is extracted and no authentication is currently set
        if (email != null && isNotAuthenticated()) {
            // Try to find the user by email in the database
            userRepository.findByEmail(email).ifPresent(user -> {
                // Build authentication token with user's role and attach request details
                UsernamePasswordAuthenticationToken authToken = buildAuthToken(user, request);

                // Set the authentication token in the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            });
        }

        // Continue with the next filter in the chain
        filterChain.doFilter(request, response);
    }

    // Checks if the Authorization header exists and starts with "Bearer "
    private boolean hasBearerToken(String header) {
        return header != null && header.startsWith("Bearer ");
    }

    // Removes the "Bearer " prefix from the Authorization header to get the token
    private String extractToken(String header) {
        return header.substring(7);
    }

    // Checks if there is no authentication set in the current security context
    private boolean isNotAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    // Creates an authentication token with the user's role and attaches request details
    private UsernamePasswordAuthenticationToken buildAuthToken(User user, HttpServletRequest request) {
        var authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        var token = new UsernamePasswordAuthenticationToken(user, null, authorities);
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return token;
    }
}


