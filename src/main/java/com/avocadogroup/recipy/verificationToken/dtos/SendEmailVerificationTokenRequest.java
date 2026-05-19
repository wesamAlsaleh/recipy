package com.avocadogroup.recipy.verificationToken.dtos;

import com.avocadogroup.recipy.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendEmailVerificationTokenRequest {
    private User user;
}
