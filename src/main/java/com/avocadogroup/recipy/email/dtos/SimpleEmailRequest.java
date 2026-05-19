package com.avocadogroup.recipy.email.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleEmailRequest {
    private String to;
    private String subject;
    private String body;
}
