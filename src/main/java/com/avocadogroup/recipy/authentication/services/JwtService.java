package com.avocadogroup.recipy.authentication.services;

import com.avocadogroup.recipy.common.configs.JwtConfig;
import com.avocadogroup.recipy.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    /**
     * Generate a JWT string with specific user details and a defined lifespan.
     * <p>
     * This internal helper sets the 'sub' (Subject) to the user's ID and adds
     * a custom 'email' claim. The token is cryptographically signed using the
     * application's secret key to ensure authenticity.
     * </p>
     *
     * @param user                     the user entity providing identity data.
     * @param tokenExpirationInSeconds the duration in seconds until the token becomes invalid.
     * @return a signed, compact JWT string.
     */
    private String buildJwtToken(User user, long tokenExpirationInSeconds) {
        // Build and return the JWT token
        return Jwts.builder()
                .subject(String.valueOf(user.getId())) // Token subject
                .claim("email", user.getEmail()) // Bonus Claim
                .claim("role", user.getRole()) // Bonus Claim
                .issuedAt(new Date()) // Token issue time
                .expiration(new Date(System.currentTimeMillis() + 1000L * tokenExpirationInSeconds)) // Expiry time in milliseconds (sec * 1000L)
                .signWith(jwtConfig.getSecretKey()) // Security signature using the secret key
                .compact();
    }

    /**
     * Decodes and validates a JWT to retrieve its payload (Claims).
     * <p>
     * This method verifies the signature using the configured secret key.
     * If the signature is invalid or the token is malformed, an exception is thrown.
     * </p>
     *
     * @param token the JWT string to be parsed.
     * @return the {@link Claims} object containing the token's payload.
     * @throws io.jsonwebtoken.JwtException if the token is invalid or the signature fails verification.
     */
    private Claims getClaimsFromToken(String token) {
        // Parse the JWT token and return the claims
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey()) // Verify the token using the secret key
                .build() // Build the parser instance after setting the verification key
                .parseSignedClaims(token) // Parse the signed JWT token
                .getPayload(); // Get the payload (claims) from the token
    }

    /**
     * Generates a short-lived Access Token used for authorizing API requests.
     *
     * @param user the user for whom the token is generated.
     * @return a JWT access token string.
     */
    public String generateAccessToken(User user) {
        // Get the expiration time in seconds
        final long tokenExpirationTime = jwtConfig.getAccessTokenValiditySeconds();

        // Build and return the access token using the jwt builder
        return buildJwtToken(user, tokenExpirationTime);
    }

    /**
     * Generates a long-lived Refresh Token used to obtain new access tokens.
     *
     * @param user the user for whom the token is generated.
     * @return a JWT refresh token string.
     */
    public String generateRefreshToken(User user) {
        // Get the expiration time in seconds
        final long tokenExpirationTime = jwtConfig.getRefreshTokenValiditySeconds();

        // Build and return the access token using the jwt builder
        return buildJwtToken(user, tokenExpirationTime);
    }

    /**
     * Validates whether a token has passed its expiration timestamp.
     *
     * @param token the JWT string to check.
     * @return {@code true} if the token is expired or invalid; {@code false} otherwise.
     * @throws io.jsonwebtoken.ExpiredJwtException if the token is expired.
     */
    public boolean isTokenExpired(String token) throws JwtException {
        // Parse the token and extract the claims
        final Claims claims = getClaimsFromToken(token);

        // Return true if the token is expired (expiration date is before the current date)
        return claims.getExpiration().before(new Date());
    }

    /**
     * Extracts the User ID from the token's subject field.
     *
     * @param token the JWT string.
     * @return the User ID as a {@link Long}.
     * @throws NumberFormatException               if the subject is not a valid numeric ID.
     * @throws io.jsonwebtoken.ExpiredJwtException if the token is expired.
     */
    public Long getUserIdFromToken(String token) throws JwtException {
        // Parse the token and extract the claims
        final Claims claims = getClaimsFromToken(token);

        // Return the subject which is the user id from the claim
        return Long.valueOf(claims.getSubject());
    }

    /**
     * Extracts the email address from the custom 'email' claim.
     *
     * @param token the JWT string.
     * @return the user's email address.
     * @throws io.jsonwebtoken.ExpiredJwtException if the token is expired.
     */
    public String getEmailFromToken(String token) throws JwtException {
        // Parse the token and extract the claims
        final Claims claims = getClaimsFromToken(token);

        // Return the user email from the token claims
        return claims.get("email").toString();
    }

    /**
     * Extracts the user role from the custom 'role' claim.
     *
     * @param token the JWT string.
     * @return the user's role.
     * @throws io.jsonwebtoken.ExpiredJwtException if the token is expired.
     */
    public String getUserRoleFromToken(String token) throws JwtException {
        // Parse the token and extract the claims
        final Claims claims = getClaimsFromToken(token);

        // Return the user role from the token claims
        return claims.get("role").toString();
    }

    /**
     * Extracts the token expiry date
     *
     * @param token the JWT string.
     * @return the token expiry date.
     * @throws io.jsonwebtoken.ExpiredJwtException if the token is expired.
     */
    public Instant getTokenExpiryDate(String token) throws JwtException {
        // Parse the token and extract the claims
        final Claims claims = getClaimsFromToken(token);

        // Return the token expiration date from the claims
        return claims.getExpiration().toInstant();
    }

}
