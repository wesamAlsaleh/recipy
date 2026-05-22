package com.avocadogroup.recipy.user;

import com.avocadogroup.recipy.authentication.services.AuthenticationService;
import com.avocadogroup.recipy.user.dtos.UpdateProfileRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    /**
     * Endpoint to update the currently authenticated user's profile.
     *
     * @param request the validated {@link UpdateProfileRequest} containing the updated profile metadata
     * @return a {@link ResponseEntity} containing the updated user DTO with an HTTP 200 OK status
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @ModelAttribute UpdateProfileRequest request) {
        // Get the authenticated user id from the context
        var userId = authenticationService.getUserId();

        // Update the user profile
        var userDto = userService.updateProfile(userId, request);

        // Return updated user with HTTP 200
        return ResponseEntity.ok(userDto);
    }
}
