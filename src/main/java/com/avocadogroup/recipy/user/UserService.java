package com.avocadogroup.recipy.user;

import com.avocadogroup.recipy.authentication.services.AuthenticationService;
import com.avocadogroup.recipy.common.exceptions.BadRequestException;
import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Fetches a user by their ID from the database.
     *
     * @param id the unique identifier of the user to find
     * @return the matching {@link User} entity
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    private User fetchUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id the unique identifier of the user to retrieve
     * @return the matching {@link User} entity
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    public User getUser(Long id) {
        // Fetch the user details
        return fetchUser(id);
    }

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
        user.verify();

        // Save the changes
        userRepository.save(user);
    }
}
