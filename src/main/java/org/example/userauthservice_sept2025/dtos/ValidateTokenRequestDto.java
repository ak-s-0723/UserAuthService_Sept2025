package org.example.userauthservice_sept2025.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValidateTokenRequestDto {
    String token;
    Long userId;
}
