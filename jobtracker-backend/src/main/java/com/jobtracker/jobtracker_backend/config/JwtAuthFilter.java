package com.jobtracker.jobtracker_backend.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jobtracker.jobtracker_backend.service.JwtService;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Runs once per request: if a valid {@code Bearer} token is present, sets the
 * security context's principal to the raw user {@link UUID} (no DB lookup).
 * Never rejects a request itself — missing/invalid tokens just pass through
 * unauthenticated, and {@link com.jobtracker.jobtracker_backend.config.SecurityConfig}'s
 * {@code authorizeHttpRequests} rules decide what happens next.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (jwtService.isTokenValid(token)) {
            UUID userId = jwtService.extractUserId(token);
            var authentication = new UsernamePasswordAuthenticationToken(userId, token, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } 
        
        filterChain.doFilter(request, response);
    }
    
}
