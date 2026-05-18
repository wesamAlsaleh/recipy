package com.avocadogroup.recipy.authentication.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginUserRequest {
    @NotEmpty(message = "Email Address cannot be empty")
    @NotBlank(message = "Email Address cannot be blank")
    @Email
    @Size(min = 1, max = 255)
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;
}
