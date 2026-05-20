package com.avocadogroup.recipy.authentication.services;

import com.avocadogroup.recipy.authentication.UserDetailsImpl;
import com.avocadogroup.recipy.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Note that UserDetailsService is a core interface in Spring Security, and it expects exception to UsernameNotFoundException, not custom exceptions

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Locates the user based on the email address.
     * <p>
     * This method is a core part of Spring Security's authentication process.
     * It searches the database for a user associated with the provided email
     * and wraps the entity in a {@link UserDetailsImpl} object for security context management.
     * </p>
     *
     * @param email the email identifying the user whose data is required.
     * @return a fully populated {@link UserDetails} object (never {@code null}).
     * @throws UsernameNotFoundException if the user could not be found or has no granted authorities.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user from repository or throw exception if the email does not exist
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // If the user found return the custom UserDetails implementation
        return new UserDetailsImpl(user);
    }
}
