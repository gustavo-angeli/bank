package com.gusta.bank.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gusta.bank.security.domain.dto.TokenDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;
    private String secretKey;
    private String previousSecretKey;
    private static final long validityInMilliseconds = 3600000;
    private static final long keyRefreshInterval = 86400000;

    @PostConstruct
    public void init() {
        refreshSecretKey();
    }

    private void refreshSecretKey() {
        if (secretKey != null) {
            log.info("Setting previousSecretKey to secretKey value");
            previousSecretKey = secretKey;
        }

        byte[] keyBytes = new byte[64];
        new SecureRandom().nextBytes(keyBytes);
        secretKey = Base64.getEncoder().encodeToString(keyBytes);
    }

    @Scheduled(fixedDelay = keyRefreshInterval)
    private void scheduleKeyRefresh() {
        log.info("Updating secretKey");
        refreshSecretKey();
    }

    public TokenDTO createTokenDTO(String username, List<String> roles) {
        return new TokenDTO(username, true, generateJWT(username, roles), generateRefreshJWT(username, roles));
    }

    public TokenDTO refreshTokenDTO(String refreshJwt) {
        refreshJwt = resolveToken(refreshJwt);
        DecodedJWT decodedJWT = decodedToken(refreshJwt);
        String username = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        return createTokenDTO(username, roles);
    }

    public String getSubject(String token) {
        token = resolveToken(token);
        return decodedToken(token).getSubject();
    }

    Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                "",
                userDetails.getAuthorities());
    }

    Boolean validateToken(String token) {
        try {
            DecodedJWT decodedJWT = decodedToken(token);
            return decodedJWT.getExpiresAt().after(new Date());
        } catch (Exception ignored) {
            return false;
        }
    }

    String resolveToken(String authHeader) {
        return (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
    }

    private String generateJWT(String username, List<String> roles) {
        return generateToken(username, roles, validityInMilliseconds);
    }

    private String generateRefreshJWT(String username, List<String> roles) {
        return generateToken(username, roles, validityInMilliseconds * 4);
    }

    private String generateToken(String username, List<String> roles, long validityMilliseconds) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + validityMilliseconds);
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withSubject(username)
                .sign(Algorithm.HMAC512(secretKey))
                .strip();
    }

    private DecodedJWT decodedToken(String token) {
        try {
            return verifyTokenWithSecret(token, secretKey);
        } catch (SignatureVerificationException e) {
            log.error("Error verifying token with current key. Trying with the previous key.");
            // If the check fails with the current key, try with the previous key
            try {
                return verifyTokenWithSecret(token, previousSecretKey);
            } catch (SignatureVerificationException ex) {
                log.error("Error verifying token with previous secret-key.");
                throw ex;
            }
        } catch (JWTDecodeException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private DecodedJWT verifyTokenWithSecret(String token, String secret) {
        Algorithm alg = Algorithm.HMAC512(secret.getBytes());
        JWTVerifier verifier = JWT.require(alg).build();
        return verifier.verify(token);
    }

}
