package com.avocadogroup.recipy.common.configs;

import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@Data
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtConfig {
    // This properties will be loaded from application.yaml file
    private String secretKey;
    private Long accessTokenValiditySeconds;
    private Long refreshTokenValiditySeconds;

    /**
     * Converts the raw string secret key into a {@link SecretKey} instance using UTF-8 encoding.
     * <p>
     * This method uses the HMAC-SHA algorithm to generate a secure signing key
     * from the application's configured secret string. The string is converted to bytes
     * using {@code StandardCharsets.UTF_8} to ensure consistent key generation across
     * different operating systems and environments.
     * </p>
     *
     * @return a {@link SecretKey} object suitable for HMAC-SHA cryptographic operations.
     * @throws IllegalArgumentException if the {@code secretKey} is null or does not meet
     *                                  the minimum length requirements for the algorithm.
     * @see StandardCharsets#UTF_8
     */
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
