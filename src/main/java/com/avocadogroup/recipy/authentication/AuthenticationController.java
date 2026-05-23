package com.avocadogroup.recipy.authentication;

import com.avocadogroup.recipy.authentication.dtos.*;
import com.avocadogroup.recipy.authentication.services.AuthenticationService;
import com.avocadogroup.recipy.common.exceptions.BadRequestException;
import com.avocadogroup.recipy.user.dtos.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    /**
     * Health check or test endpoint
     *
     * @return a {@link ResponseEntity} containing a greeting message ("Hello World")
     */
    @GetMapping("/ping")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World");
    }

    /**
     * A secured heartbeat endpoint used to verify authentication and service availability
     *
     * @return A {@link ResponseEntity} containing a "Protected Hello World" success message.
     */
    @GetMapping("/protected-ping")
    public ResponseEntity<String> protectedPing() {
        return ResponseEntity.ok("Protected Hello World");
    }

    /**
     * Endpoint to registers a new user in the system with email verification
     *
     * @param registerUserRequest the validated request body containing user registration data
     * @param uriBuilder          utility used to construct the URI of the newly created resource
     * @return a {@link ResponseEntity} containing the created {@link com.avocadogroup.recipy.user.dtos.UserDto} and HTTP 201 status
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest registerUserRequest,
            UriComponentsBuilder uriBuilder
    ) {
        // Delegate user creation to the authentication service
        var userDto = authenticationService.register(registerUserRequest); // through exception if something went wrong

        // Build URI for the newly created user resource (REST best practice)
        var uri = uriBuilder
                .path("/users/{id}")
                .buildAndExpand(userDto.id())
                .toUri();

        // Return HTTP 201 Created with Location header and created user payload
        return ResponseEntity.created(uri).body(userDto);
    }

    // Function to verify the user
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam @NotBlank @Size(max = 2048) String token) {
        // Verify the user with its verification token
        authenticationService.verifyUser(token);

        // Return HTTP 204
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to authenticates a user and returns a JWT access token
     *
     * @param request the validated login request containing email and password
     * @return a {@link ResponseEntity} containing the JWT access token
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginUserRequest request
    ) {
        // Authenticate user and generate token (throws exception if credentials are invalid or user not found)
        var authenticationTokens = authenticationService.login(request);

        // Return HTTP 200 OK with access token in response body
        return ResponseEntity.ok().body(new LoginUserResponse(authenticationTokens.getAccessToken()));
    }

    /**
     * Endpoint to retrieves the profile information of the currently authenticated user.
     *
     * @return A {@link ResponseEntity} containing the {@code UserDto} of the requester.
     */
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        // Retrieves the current user profile from the security context
        var userDto = authenticationService.me();

        // Return the user details with OK response
        return ResponseEntity.ok(userDto);
    }

    /**
     * Endpoint to user logout by extracting and invalidating the provided JWT.
     *
     * @param authorizationHeader The raw "Bearer [token]" string from the request headers.
     * @return A {@link ResponseEntity} with HTTP Status 204 (No Content).
     * @throws BadRequestException If the header is missing, empty, or not a Bearer token.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // If the authorization header is missing or does not start with "Bearer " then skip the filter
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // System.out.println("Authorization header is null");
            throw new BadRequestException("Invalid logout request");
        }

        // Isolates the JWT string by removing the "Bearer " prefix
        var token = authorizationHeader.replace("Bearer ", ""); // Remove "Bearer " from "Bearer A2C4"

        // Mark the token as revoked
        var revokedToken = authenticationService.logout(token);

        // Returns 204 No Content as the standard successful response for destructive actions
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to update user login credentials
     *
     * @param request The validated password change data.
     * @return A {@link ResponseEntity} containing the updated {@link UserDto}.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        // Delegates the password verification and encryption logic to the service layer
        var updatedUserDto = authenticationService.changePassword(request);

        // Returns the updated user profile with a 200 OK status
        return ResponseEntity.ok().body(updatedUserDto);
    }

    /**
     * Endpoint to sends a password reset link to the specified email address.
     *
     * @param request The {@link ForgotPasswordRequest} containing the user's email
     * @return A {@link ResponseEntity} with HTTP 200 OK and a generic success message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // Delegates the forgot password logic to the service layer
        var forgotPasswordToken = authenticationService.forgotPassword(request);

        // Return a generic success message regardless of whether the email exists
        return ResponseEntity.ok().body(new ForgotPasswordResponse(
                forgotPasswordToken,
                "A password reset link has been sent."
        ));
    }

    /**
     * Resets the user's password using a valid reset token.
     *
     * @param request The {@link ResetPasswordRequest} containing the token and new password
     * @return A {@link ResponseEntity} with HTTP 200 OK on successful reset
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        // Delegates the password reset logic to the service layer
        authenticationService.resetPassword(request);

        // Return success confirmation
        return ResponseEntity.ok().body(new ResetPasswordResponse("Password has been reset successfully."));
    }
}
