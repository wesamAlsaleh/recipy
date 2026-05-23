package com.avocadogroup.recipy.authentication.services;

import com.avocadogroup.recipy.authentication.UserDetailsImpl;
import com.avocadogroup.recipy.authentication.dtos.AuthenticationTokensResponse;
import com.avocadogroup.recipy.authentication.dtos.ChangePasswordRequest;
import com.avocadogroup.recipy.authentication.dtos.ForgotPasswordRequest;
import com.avocadogroup.recipy.authentication.dtos.LoginUserRequest;
import com.avocadogroup.recipy.authentication.dtos.RegisterUserRequest;
import com.avocadogroup.recipy.authentication.dtos.ResetPasswordRequest;
import com.avocadogroup.recipy.common.exceptions.DuplicateResourceException;
import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.recipy.common.exceptions.UnauthorizedException;
import com.avocadogroup.recipy.user.*;
import com.avocadogroup.recipy.user.dtos.UserDto;
import com.avocadogroup.recipy.userSession.UserSessionService;
import com.avocadogroup.recipy.passwordResetToken.PasswordResetTokenService;
import com.avocadogroup.recipy.verificationToken.VerificationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserSessionService userSessionService;
    private final UserService userService;

    /**
     * Extracts the authenticated user's ID from the Security Context.
     *
     * <p>This method retrieves the {@link org.springframework.security.core.Authentication} object from the
     * {@link SecurityContextHolder}, which stores security information in a
     * thread-local context for the current request.</p>
     *
     * <p>It assumes that the {@code principal} has been set (typically during
     * JWT authentication filtering) and contains the user's unique identifier.</p>
     *
     * @return the unique {@link Long} identifier of the currently authenticated user
     * @throws RuntimeException      if the authentication object is {@code null} or
     *                               if the principal is missing, indicating an unauthorized
     *                               or improperly processed request
     * @throws NumberFormatException if the principal cannot be parsed into a {@link Long}
     */
    private Long getUserIdFromSecurityContext() {
        // Retrieve the current authentication details from the security thread-local storage
        var authenticationObject = SecurityContextHolder.getContext().getAuthentication(); // Authentication is the object that holds the authentication information of the user

        // Ensure the authentication object exists before attempting to access the principal
        if (authenticationObject == null) {
            throw new UnauthorizedException("Authentication object is null");
        }

        // Extract and convert the principal (expected to be the user ID) into a Long
        // Objects.requireNonNull ensures we don't call .toString() on a null principal
        return Long.parseLong(Objects.requireNonNull(authenticationObject.getPrincipal()).toString());
    }

    /**
     * Registers a new user in the system.
     *
     * <p>This method creates a new {@link com.avocadogroup.recipy.user.User} entity from the provided request,
     * encodes the user's password, assigns a default role, persists the user in
     * the database, and returns a mapped {@link UserDto} representation.</p>
     *
     * <p>Note: Additional validation (such as checking for existing email or phone
     * number) and email verification are expected to be handled in future enhancements.</p>
     *
     * @param request the registration request containing user input data
     * @return a {@link UserDto} representing the newly created user
     */
    @Transactional
    public UserDto register(RegisterUserRequest request) {
        // Initialize a new User entity
        var user = new User();

        // If the email is already used
        // TODO: refactor this
        if (userRepository.existsByEmail((request.getEmail()))) {
            throw new DuplicateResourceException("Email already in use");
        }

        // Set the user fields from request
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER.toString()); // Assign default role for newly registered users

        // Save the user entity to the database
        // TODO: refactor this
        userRepository.save(user);

        // send verification token to the user
        verificationTokenService.sendVerificationEmail(user);

        // Return the created entity as a DTO
        return userMapper.toDto(user);
    }

    /**
     * Verifies a user's account using their verification token
     *
     * @param verificationToken the unique token string provided by the user
     */
    public void verifyUser(String verificationToken) {
        // Make the token as user
        verificationTokenService.verifyToken(verificationToken);
    }

    /**
     * Authenticates a user and generates an access token
     *
     * @param request the login request containing user credentials (email and password)
     * @return an {@link AuthenticationTokensResponse} containing the generated access token
     */
    public AuthenticationTokensResponse login(LoginUserRequest request) {
        // Authenticate user credentials using Spring Security AuthenticationManager by providing the email and password to the manager, if failed it will throw an exception
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get the UserDetails object from the security context after login
        var userObj = (UserDetailsImpl) authentication.getPrincipal(); // Authenticated principal (custom UserDetails implementation)

        // Get the User entity from the authenticated principal (from the UserDetails object)
        Objects.requireNonNull(userObj, "Authenticated principal must not be null");
        var user = userObj.getUser();

        // Generate an access token (JWT) for the authenticated user
        var accessToken = jwtService.generateAccessToken(user);

        // Create new session
        userSessionService.createSession(
                user,
                accessToken,
                jwtService.getTokenExpiryDate(accessToken)
        );

        // Return authentication response containing the access token {accessToken:"abc", refreshToken:"xyz"}
        return new AuthenticationTokensResponse(accessToken);
    }

    /**
     * Retrieves the currently authenticated user's profile.
     *
     * <p>This method extracts the user ID from the Security Context and uses it
     * to fetch the corresponding {@link User} entity from the database.</p>
     *
     * @return a {@link UserDto} representing the currently authenticated user
     * @throws ResourceNotFoundException if no user exists for the authenticated user ID
     */
    public UserDto me() {
        // Retrieve the authenticated user ID from the Security Context (user id is the principal in the security context holder)
        var userId = getUserId();

        // Fetch user from database using the authenticated user ID
        var user = userService.getUser(userId);

        // Return the user as UserDto format
        return userMapper.toDto(user);
    }

    /**
     * Retrieves the ID of the currently authenticated user from the security context
     *
     * @return the unique identifier of the authenticated user, or {@code null}
     * if no authentication information is available
     */
    public Long getUserId() {
        // Extracts the user ID from the SecurityContext
        return getUserIdFromSecurityContext();
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the matching {@link User} entity belonging to the authenticated session
     * @throws ResourceNotFoundException if the authenticated user's ID does not exist in the database
     */
    public User getAuthenticatedUser() {
        // Get the authenticated user id
        var userId = getUserId();

        // Fetch the user details
        return userService.getUser(userId);
    }

    /**
     * Processes a logout request by invalidating the provided security token
     *
     * @param token The header token string to be invalidated.
     * @return The invalidated token string upon successful revocation.
     * @throws ResourceNotFoundException If the token does not exist in the database.
     */
    @Transactional
    public String logout(String token) {
        // Revoke the session and return it
        return userSessionService.revokeSession(token);
    }

    /**
     * Updates the password for the currently authenticated user
     *
     * @param request A {@link ChangePasswordRequest} containing the current and new passwords.
     * @return A {@link UserDto} reflecting the updated user state.
     * @throws BadCredentialsException If the current password provided is incorrect.
     */
    @Transactional
    public UserDto changePassword(ChangePasswordRequest request) {
        // Get the user who made the request
        var userId = getUserIdFromSecurityContext();

        // Get the user entity
        var user = userService.getUser(userId); // This should never throw since the user is authenticated

        // Validates the current password before permitting the change
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("The current password you entered is incorrect.");
        }

        // Encrypts the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Revoke all active sessions so stolen tokens become invalid after password change
        revokeAllUserSessions(userId);

        // Updates the persistence layer
        userRepository.save(user);

        // Return the user as UserDto format
        return userMapper.toDto(user);
    }

    /**
     * Initiates the password reset flow by sending a reset link to the user's email.
     *
     * @param request The {@link ForgotPasswordRequest} containing the user's email address
     */
    public String forgotPassword(ForgotPasswordRequest request) {
        // Send the reset email
        return passwordResetTokenService.sendResetPasswordEmail(request.getEmail());
    }

    /**
     * Resets the user's password using a valid reset token.
     *
     * @param request The {@link ResetPasswordRequest} containing the token and new password
     * @throws com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException if the token is invalid
     * @throws com.avocadogroup.recipy.common.exceptions.BadRequestException       if the token is used or expired
     */
    @Transactional // Ensures rollback on failure
    public void resetPassword(ResetPasswordRequest request) {
        // Validate the token and get the associated user
        var user = passwordResetTokenService.verifyAndConsumeToken(request.getToken());

        // Encrypts the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Revoke all active sessions so stolen tokens become invalid after password change
        revokeAllUserSessions(user.getId());

        // Updates the persistence layer
        userRepository.save(user);
    }

    /**
     * Revokes all active sessions for a given user.
     *
     * @param userId The unique identifier of the user whose sessions should be revoked.
     */
    public void revokeAllUserSessions(Long userId) {
        // Try to revoke the sessions
        userSessionService.revokeAllSessions(userId);
    }
}
