package com.danielpg.paymentgateway.infrastructure.security;

import com.danielpg.paymentgateway.application.auth.Token;
import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.user.EmailAddress;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtTokenService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.lifetime}")
    private Long lifetime;

    private final UserRepository userRepository;
    private final AppClock clock;

    public JwtTokenService(UserRepository userRepository, AppClock appClock) {
        this.userRepository = userRepository;
        this.clock = appClock;
    }

    public String generate(EmailAddress emailAddress) {
        return buildRawToken(emailAddress, calculateExpiration());
    }

    private long calculateExpiration() {
        return System.currentTimeMillis() + lifetime;
    }

    private String buildRawToken(EmailAddress emailAddress, long expiration) {
        return Jwts.builder()
                .setSubject(emailAddress.value())
                .setExpiration(new Date(expiration))
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Token decode(String rawToken) {
        var claims = parseClaims(rawToken);
        var email = EmailAddress.of(claims.getSubject());
        var expiration = claims.getExpiration().getTime();
        var user = userRepository.getOrThrow(email);
        return Token.builder()
                .rawToken(rawToken)
                .user(user)
                .expiration(expiration)
                .clock(clock)
                .build();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build().parseClaimsJws(token).getBody();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

}
