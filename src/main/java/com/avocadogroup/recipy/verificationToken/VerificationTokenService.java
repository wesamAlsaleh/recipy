package com.avocadogroup.recipy.verificationToken;

import com.avocadogroup.recipy.common.configs.AppConfig;
import com.avocadogroup.recipy.common.exceptions.BadRequestException;
import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.recipy.email.EmailService;
import com.avocadogroup.recipy.email.dtos.SimpleEmailRequest;
import com.avocadogroup.recipy.user.User;
import com.avocadogroup.recipy.user.UserService;
import com.avocadogroup.recipy.verificationToken.dtos.SendEmailVerificationTokenRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@AllArgsConstructor
public class VerificationTokenService {
    private final EmailService emailService;
    private final AppConfig appConfig;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserService userService;

    /**
     * Generates a cryptographically secure, URL-safe string to be used
     * for email verification.
     * * @return A unique 32-byte Base64 encoded token.
     */
    private String generateEmailVerificationToken() {
        // Initialize array of 32 byte
        byte[] bytes = new byte[32];

        // Fill the byte array with cryptographically strong random values
        new SecureRandom().nextBytes(bytes);

        // Return a URL-safe string without padding (e.g., '+' and '/' replaced, '=' removed)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }


//    TODO: enhance this doc
    /**
     * Generates and sends a time-sensitive account verification email to a newly registered user.
     *
//     * @param request the {@link SendEmailVerificationTokenRequest} containing the recipient user details
     * @throws RuntimeException if the email delivery fails or a database persistence issue occurs
     */
    public void sendVerificationEmail(User user) {
        // Generate verification token
        var verificationToken = generateEmailVerificationToken();

        // Prepare the body
        var verificationLink = appConfig.getBaseUrl() + "/api/auth/verify?token=" + verificationToken;

        var body = "Click the link below to verify your account:\n\n"
                + verificationLink
                + "\n\nThis link expires in 15 minutes.";

        // Prepare the expiration time
        var expiresAt = Instant.now().plusSeconds(900);

        // Create the token entity
        var token = new VerificationToken();
        token.setUser(user);
        token.setToken(verificationToken);
        token.setExpiryDate(expiresAt);

        // Save the token in the db
        verificationTokenRepository.save(token);

        // Email the user
        emailService.sendEmail(new SimpleEmailRequest(
                user.getEmail(), // To
                "Email Verification", // Subject
                body // Body
        ));
    }

    /**
     * Verifies a token and updates the associated user's account verification status
     *
     * @param verificationToken the token to be validated
     * @throws ResourceNotFoundException if no matching token is found in the database
     * @throws BadRequestException       if the token is already used or has expired
     */
    public void verifyToken(String verificationToken) {
        // Fetch the token from the db
        var token = verificationTokenRepository.findByToken(verificationToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        // Check if the token is used
        if (token.getUsed()) {
            throw new BadRequestException("Token is already used");
        }

        // Check if the token is not valid (expired)
        if (!token.isValid()) {
            throw new BadRequestException("Token is invalid");
        }

        // Make the token as used
        token.setUsed(true);

        // Verify the user
        var user = token.getUser();
        userService.VerifyUser(user);

        // Update the changes of the token
        verificationTokenRepository.save(token);
    }

}
