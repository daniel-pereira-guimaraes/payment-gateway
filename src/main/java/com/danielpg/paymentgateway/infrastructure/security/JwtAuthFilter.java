package com.danielpg.paymentgateway.infrastructure.security;

import java.io.IOException;
import java.util.Optional;

import com.danielpg.paymentgateway.application.auth.Token;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;

    public JwtAuthFilter(JwtTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isUnauthenticated()) {
                processAuthentication(request);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
           writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (JwtException e) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }

    private boolean isUnauthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void processAuthentication(HttpServletRequest request) {
        extractToken(request).ifPresent(rawToken -> {
            var token = tokenService.decode(rawToken);
            if (!token.isExpired()) {
                authenticateUser(token, request);
            }
        });
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        return isTokenPresent(authorizationHeader)
                ? Optional.of(authorizationHeader.substring(7))
                : Optional.empty();
    }

    private boolean isTokenPresent(String authorizationHeader) {
        return authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ");
    }

    private void authenticateUser(Token token, HttpServletRequest request) {
        var userDetails = new UserDetailsImpl(token.user());
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}