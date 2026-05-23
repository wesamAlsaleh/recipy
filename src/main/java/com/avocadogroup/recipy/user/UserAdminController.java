package com.avocadogroup.recipy.user;

import com.avocadogroup.recipy.user.dtos.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/users")
public class UserAdminController {
    private final UserService userService;

    /**
     * Endpoint to soft-delete a specific user by their ID.
     *
     * @param userId the unique ID of the user to soft-delete from the URL path
     * @return a {@link ResponseEntity} containing the updated user DTO with an HTTP 200 OK status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteUser(@PathVariable("id") Long userId) {
        // Delegate the deletion to the service layer
        var userDto = userService.softDeleteUser(userId);

        // Return the soft deleted user data with an HTTP 200 OK status
        return ResponseEntity.ok(userDto);
    }

    /**
     * Endpoint to restore a soft-deleted user by their ID.
     *
     * @param userId the unique ID of the user to restore from the URL path
     * @return a {@link ResponseEntity} containing the restored user DTO with an HTTP 200 OK status
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreUser(@PathVariable("id") Long userId) {
        // Delegate the activation to the service layer
        var userDto = userService.restoreUser(userId);

        // Return the restored user data with an HTTP 200 OK status
        return ResponseEntity.ok(userDto);
    }
}
