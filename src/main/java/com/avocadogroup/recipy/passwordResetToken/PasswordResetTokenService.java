package com.avocadogroup.recipy.passwordResetToken;

import com.avocadogroup.recipy.common.configs.AppConfig;
import com.avocadogroup.recipy.common.exceptions.BadRequestException;
import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.recipy.email.EmailService;
import com.avocadogroup.recipy.email.dtos.SimpleEmailRequest;
import com.avocadogroup.recipy.user.User;
import com.avocadogroup.recipy.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@AllArgsConstructor
public class PasswordResetTokenService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AppConfig appConfig;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * Generates a cryptographically secure, URL-safe token for password reset.
     *
     * @return A unique 32-byte Base64 URL-encoded string without padding
     */
    private String generateResetToken() {
        // Initialize array of 32 cryptographically strong random bytes
        byte[] bytes = new byte[32];

        // Fill the byte array with secure random values
        new SecureRandom().nextBytes(bytes);

        // Encode to a URL-safe Base64 string without padding characters
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Initiates the password reset process for a user by generating a secure token and emailing a reset link.
     *
     * @param email the email address of the user requesting the password reset
     * @return the generated token string if the user exists; {@code null} if no user matches the given email
     */
    public String sendResetPasswordEmail(String email) {
        // Check if the email exists in the system
        var userOptional = userRepository.findByEmailIgnoreCase(email);

        // If the email is not found
        if (userOptional.isEmpty()) {
            // Do nothing
            return null;
        }

        // Get the user from the optional
        var user = userOptional.get();

        // Generate a cryptographically secure reset token
        var resetToken = generateResetToken();

        // Build the password reset link using the application's base URL
        var resetLink = appConfig.getBaseUrl() + "/api/auth/reset-password?token=" + resetToken;

        // Prepare the email body
        var body = "You have requested a password reset.\n\n"
                + "Click the link below to reset your password:\n\n"
                + resetLink
                + "\n\nThis link expires in 15 minutes.\n"
                + "If you did not request this, please ignore this email.";

        // Set the token expiration to 15 minutes from now
        var expiresAt = Instant.now().plusSeconds(900);

        // Create the token entity & set the data
        var token = new PasswordResetToken();
        token.setToken(resetToken);
        token.setUser(user);
        token.setExpiredAt(expiresAt);

        // Save the token to the database
        passwordResetTokenRepository.save(token);

        // Send the reset email to the user
        emailService.sendEmail(new SimpleEmailRequest(
                user.getEmail(), // Recipient email address
                "Password Reset Request", // Email subject
                body // Email body text
        ));

        // Return the reset token
        return token.getToken();
    }

    /**
     * Validates a password reset token, consumes it to prevent reuse, and returns the associated user.
     *
     * @param token the raw reset token string to validate
     * @return the {@link User} entity associated with the valid token
     * @throws ResourceNotFoundException if the token is not found in the database
     * @throws BadRequestException       if the token has already been used or has expired
     */
    public User verifyAndConsumeToken(String token) {
        // Fetch the token from the database
        var resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired reset token"));

        // Check if the token has already been used
        if (resetToken.getUsed()) {
            throw new BadRequestException("This reset token has already been used");
        }

        // Check if the token has expired
        if (!resetToken.isValid()) {
            throw new BadRequestException("This reset token has expired");
        }

        // Mark the token as used so it cannot be reused
        resetToken.consume();

        // Save the updated token state to the database
        passwordResetTokenRepository.save(resetToken);

        // Return the user associated with this token
        return resetToken.getUser();
    }
}
