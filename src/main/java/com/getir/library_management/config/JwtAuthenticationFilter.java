package com.getir.library_management.config;

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

        // Get the Authorization header from the incoming request
        String authHeader = request.getHeader("Authorization");

        // If the Authorization header is missing or does not start with "Bearer ", skip authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token by removing the "Bearer " prefix
        String jwt = authHeader.substring(7);

        // Extract the username (email) from the JWT token
        String userEmail = jwtService.extractUsername(jwt);

        // If username is extracted and there is no existing authentication in the security context
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Try to find the user from the database by email
            var user = userRepository.findByEmail(userEmail).orElse(null);

            // If the user exists, create an authentication token
            if (user != null) {
                // Create an authentication token with user details, no credentials, and no authorities (permissions)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority(user.getRole().name())));

                // Attach request details (like remote IP address) to the authentication token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication token in the SecurityContext to mark the user as authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue the filter chain (move to the next filter or controller)
        filterChain.doFilter(request, response);
    }
}

