package com.example.unicon.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCheckRequestDTO(
        @NotBlank @Email String email
) {
}