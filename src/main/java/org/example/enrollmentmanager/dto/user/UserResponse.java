package org.example.enrollmentmanager.dto.user;

import org.example.enrollmentmanager.domain.user.User;
import org.example.enrollmentmanager.domain.user.UserRole;

public record UserResponse(
        Long id,
        String email,
        String name,
        UserRole role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}