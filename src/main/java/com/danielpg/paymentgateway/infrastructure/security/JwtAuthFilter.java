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
        } catch (JwtException e) {
            writeUnauthorizedResponse(response, e);
        }
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, Throwable e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
                {"message": "%s"}
                """.formatted(e.getMessage()));

    }

    private boolean isUnauthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void processAuthentication(HttpServletRequest request) {
        var tokenOpt = extractToken(request);

        if (tokenOpt.isEmpty()) {
            throw new JwtException("Token nao fornecido.;");
        }

        var token = tokenService.decode(tokenOpt.get());

        if (token.isExpired()) {
            throw new ExpiredJwtException(null, null, "Token expirado.");
        }

        authenticateUser(token, request);
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