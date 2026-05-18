package com.avocadogroup.recipy.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationTokensResponse {
    private String accessToken;
}
