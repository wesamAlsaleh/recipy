package com.avocadogroup.recipy.user;

import com.avocadogroup.recipy.cloudinary.CloudinaryService;
import com.avocadogroup.recipy.common.exceptions.BadRequestException;
import com.avocadogroup.recipy.common.exceptions.DuplicateResourceException;
import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.recipy.user.dtos.UpdateProfileRequest;
import com.avocadogroup.recipy.user.dtos.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final UserMapper userMapper;

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
     * Converts a User entity into its corresponding Data Transfer Object.
     *
     * @param user the {@link User} entity to convert
     * @return the mapped {@link UserDto}, or {@code null} if the input is null
     */
    private UserDto toDto(User user) {
        return userMapper.toDto(user);
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

    /**
     * Updates the authenticated user's profile (partial update)
     *
     * @param request the update profile request
     * @return the updated user as DTO
     */
    @Transactional // Ensures rollback on failure
    public UserDto updateProfile(Long userId, UpdateProfileRequest request) {
        // Fetch the user from the db
        var user = getUser(userId);

        // Update email if provided
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Check if the new email is already taken by another user
            if (!request.getEmail().equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already in use");
            }

            // Update the email
            user.setEmail(request.getEmail());
        }

        // Update username if provided
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            // Update the username
            user.setUsername(request.getUsername());
        }

        // Upload avatar if provided
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            // Try to upload user avatar
            String avatarUrl = cloudinaryService.uploadFile(request.getAvatar(), "avatars");

            // Delete old image from the cloud
            cloudinaryService.deleteFileByUrl(user.getProfileImageUrl());

            // Update the avatar URL
            user.setProfileImageUrl(avatarUrl);
        }

        // Save the changes
        userRepository.save(user);

        // Return updated user as DTO
        return toDto(user);
    }

    /**
     * Soft-deletes a user by their ID.
     *
     * @param id the unique identifier of the user to soft-delete
     * @return the updated {@link UserDto} with inactive state
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    @Transactional
    public UserDto softDeleteUser(Long id) {
        // Fetch the user
        var user = fetchUser(id);

        // If already deleted
        if (user.getDeleted()) {
            // Do nothing
            return toDto(user);
        }

        // Mark as deleted and inactive
        user.softDelete();

        // Save the changes and return
        return toDto(userRepository.save(user));
    }

    /**
     * Restores a soft-deleted user by their ID.
     *
     * @param id the unique identifier of the user to restore
     * @return the updated {@link UserDto} with active state
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    @Transactional
    public UserDto restoreUser(Long id) {
        // Fetch the user
        var user = fetchUser(id);

        // If not deleted (already active)
        if (!user.getDeleted()) {
            // Do nothing
            return toDto(user);
        }

        // Restore the user
        user.restore();

        // Save the changes and return
        return toDto(userRepository.save(user));
    }
}
