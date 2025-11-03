package com.tujulishanehub.backend.config;

import com.tujulishanehub.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;
        String role = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(token);
                role = jwtUtil.getRoleFromToken(token);
            } catch (Exception e) {
                logger.warn("JWT parsing failed for request {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Use the role from the JWT token, or default to ROLE_USER if not present
            String authority = (role != null && !role.isEmpty()) ? "ROLE_" + role : "ROLE_USER";
            logger.debug("JWT authentication established for {} with authority {} on {} {}", username, authority, request.getMethod(), request.getRequestURI());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username, null, Collections.singleton(new SimpleGrantedAuthority(authority)));
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else if (username == null) {
            logger.trace("No JWT token present for {} {}", request.getMethod(), request.getRequestURI());
        } else {
            logger.trace("Security context already populated for {} {}", request.getMethod(), request.getRequestURI());
        }

        chain.doFilter(request, response);
    }
}

