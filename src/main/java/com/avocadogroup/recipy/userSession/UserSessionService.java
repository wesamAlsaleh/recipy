package com.avocadogroup.recipy.userSession;

import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.recipy.user.User;
import com.avocadogroup.recipy.verificationToken.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class UserSessionService {
    private final UserSessionsRepository userSessionsRepository;

    /**
     * Fetches a user session from the DB using the provided token in the header
     *
     * @param token the unique session token string to look up
     * @return the matching {@link UserSession} entity, or {@code null} if no session is found
     */
    public UserSession fetchUserSession(String token) {
        return userSessionsRepository.findByToken(token)
                .orElse(null);
    }

    // Function to create user session
    public UserSession createSession(User user, String token, Instant expiryDate) {
        // Create new verification token record
        var session = new UserSession();

        // Set the token metadata
        session.setUser(user);
        session.setToken(token);
        session.setExpiryDate(expiryDate);

        // Save the token in database for session tracking
        return userSessionsRepository.save(session);
    }

    /**
     * Revokes an active user session based on the provided session token
     *
     * @param token the unique session token string to be invalidated
     * @return the token string confirming the successful completion of the revocation
     * @throws ResourceNotFoundException if the provided token does not match any existing session record
     */
    public String revokeSession(String token) {
        // Fetch the token from the sessions record
        // TODO: refactor this line
        var tokenSession = userSessionsRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid session"));

        // If token is revoked do nothing
        if (tokenSession.getRevoked()) {
            // Return the revoked token to reduce db queries (the save operation below)
            return tokenSession.getToken();
        }

        // Updates the token's internal state to revoked
        tokenSession.revokeToken();

        // Save the changes
        userSessionsRepository.save(tokenSession);

        // Returns the revoked token to confirm the operation is complete
        return tokenSession.getToken();
    }
}
