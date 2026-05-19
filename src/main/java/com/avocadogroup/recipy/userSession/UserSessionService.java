package com.avocadogroup.recipy.userSession;

import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.recipy.user.User;
import com.avocadogroup.recipy.verificationToken.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserSessionService {
    private final UserSessionsRepository userSessionsRepository;

    /**
     *
     * @param token
     * @return
     */
    public String revokeSession(String token) {
        // Fetch the token from the sessions record
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
