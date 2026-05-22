package com.avocadogroup.recipy.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UpdateProfileRequest {
    @Email(message = "Email must be a valid email address")
    @Size(max = 255)
    private String email;

    @Size(min = 1, max = 255)
    private String username;

    private MultipartFile avatar;
}
