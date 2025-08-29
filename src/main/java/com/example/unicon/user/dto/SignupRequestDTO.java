package com.example.unicon.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequestDTO(
        @NotBlank @Size(min=2, max=50) String userName,
        @NotBlank @Size(min=2, max=50) String tenantName,
        @NotBlank @Email String email,
        String password,
        String passwordConfirm,
        @NotBlank                      String subDomain,
        @NotBlank                      String signupType
) {

}
