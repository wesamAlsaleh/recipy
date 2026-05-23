package com.avocadogroup.recipy.authentication.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Old password must not be blank")
    @NotNull(message = "Old password cannot be empty")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String currentPassword;

    @NotEmpty(message = "New password cannot be empty")
    @NotNull(message = "New password cannot be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String newPassword;
}
