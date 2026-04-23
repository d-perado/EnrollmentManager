package org.example.enrollmentmanager.dto.user;

import org.example.enrollmentmanager.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String name,

        @NotNull
        UserRole role
) {
}