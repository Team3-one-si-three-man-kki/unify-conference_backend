package com.example.unicon.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank String subDomain,
        @NotBlank String email,
        @NotBlank String password
) {
}
