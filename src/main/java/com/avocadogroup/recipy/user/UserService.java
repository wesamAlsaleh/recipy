package com.avocadogroup.recipy.user;

import com.avocadogroup.recipy.common.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Marks a user's account as verified
     *
     * @param user the user entity to be verified
     * @throws BadRequestException if the provided user object is null
     */
    public void VerifyUser(User user) {
        // If the user is null throw error
        if (user == null) {
            throw new BadRequestException("User is null");
        }

        // Make the user verified
        user.verified();

        // Save the changes
        userRepository.save(user);
    }
}
